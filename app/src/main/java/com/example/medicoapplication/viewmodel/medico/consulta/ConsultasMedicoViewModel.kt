package com.example.medicoapplication.viewmodel.medico.consulta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.StatusConsulta
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.MedicoRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConsultasMedicoViewModel(
    private val repository: MedicoRepository = MedicoRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle    : UiState()
        object Loading : UiState()
        data class Success(val consultas: List<ConsultaResponseDto>) : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Lista completa carregada da API — nunca alterada após o carregamento
    private var todasConsultas: List<ConsultaResponseDto> = emptyList()

    // Filtro ativo no momento (null = todas)
    private var filtroAtivo: StatusConsulta? = null

    fun carregarConsultas() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getConsultas()
                .onSuccess { lista ->
                    todasConsultas = lista
                    aplicarFiltro(filtroAtivo)
                }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())
                }
        }
    }

    /**
     * Aplica o filtro de status sobre a lista já carregada.
     * Não faz nova chamada de rede.
     * @param status null = mostrar todas
     */
    fun filtrar(status: StatusConsulta?) {
        filtroAtivo = status
        aplicarFiltro(status)
    }

    private fun aplicarFiltro(status: StatusConsulta?) {
        val filtradas = if (status == null) {
            todasConsultas
        } else {
            todasConsultas.filter { it.status == status }
        }
        _uiState.value = UiState.Success(filtradas)
    }
}
