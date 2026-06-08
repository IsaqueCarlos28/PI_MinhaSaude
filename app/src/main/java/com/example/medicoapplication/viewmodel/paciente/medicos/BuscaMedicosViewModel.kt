package com.example.medicoapplication.viewmodel.paciente.medicos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.consultaofertada.TipoConsulta
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.MedicoRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BuscaMedicosViewModel : ViewModel() {

    private val repository = MedicoRepository()

    /**
     * Modelo interno que une MedicoResponseDto com os tipos de consulta
     * que ele oferece, sem alterar os DTOs da API.
     */
    data class MedicoComTipo(
        val medico: MedicoResponseDto,
        val tipos: Set<TipoConsulta>
    )

    private var listaCompleta: List<MedicoComTipo> = emptyList()
    private var filtroTipoAtivo: TipoConsulta = TipoConsulta.PRESENCIAL

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val medicos: List<MedicoResponseDto>) : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    fun carregarMedicos(page: Int, size: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            repository.getMedicos(page, size)
                .onSuccess { pagina ->
                    val medicos = pagina._embedded?.medicos ?: emptyList()

                    // Busca consultas ofertadas de cada médico em paralelo
                    val medicosComTipo = medicos.map { medico ->
                        async {
                            val tipos = repository.getConsultasOfertadasDoMedico(medico.usuario!!.id)
                                .getOrNull()
                                ?.map { it.tipoConsulta }
                                ?.toSet()
                                ?: emptySet()
                            MedicoComTipo(medico, tipos)
                        }
                    }.awaitAll()

                    listaCompleta = medicosComTipo
                    aplicarFiltros(texto = "", tipo = filtroTipoAtivo)
                }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())
                }
        }
    }

    fun setFiltroTipo(tipo: TipoConsulta) {
        filtroTipoAtivo = tipo
        aplicarFiltros(texto = textoAtual, tipo = tipo)
    }

    fun filtrar(texto: String) {
        textoAtual = texto
        aplicarFiltros(texto = texto, tipo = filtroTipoAtivo)
    }

    private var textoAtual: String = ""

    private fun aplicarFiltros(texto: String, tipo: TipoConsulta) {
        val textoBusca = texto.trim().lowercase()

        val filtrados = listaCompleta
            // 1. Filtro por tipo de consulta
            .filter { it.tipos.isEmpty() || it.tipos.contains(tipo) }
            // 2. Filtro por nome ou especialidade
            .filter { entry ->
                if (textoBusca.isBlank()) return@filter true
                val nome = entry.medico.usuario?.nome?.lowercase() ?: ""
                val especialidade = entry.medico.especialidades
                    .firstOrNull()?.especialidade?.nome?.lowercase() ?: ""
                nome.contains(textoBusca) || especialidade.contains(textoBusca)
            }
            .map { it.medico }

        _uiState.value = UiState.Success(filtrados)
    }
}