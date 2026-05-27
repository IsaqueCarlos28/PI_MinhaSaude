package com.example.medicoapplication.viewmodel.paciente
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto
import com.example.medicoapplication.data.repository.MedicoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BuscaMedicosViewModel : ViewModel() {

    private val repository = MedicoRepository()

    private var listaCompleta: List<MedicoResponseDto> = emptyList()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val medicos: List<MedicoResponseDto>) : UiState()
        data class Error(val message: String) : UiState()
    }

    fun carregarMedicos() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            repository.getMedicos()
                .onSuccess { medicos ->
                    listaCompleta = medicos
                    _uiState.value = UiState.Success(medicos)
                }
                .onFailure { erro ->
                    _uiState.value = UiState.Error(
                        erro.message ?: "Erro ao carregar médicos"
                    )
                }
        }
    }

    fun filtrar(texto: String) {

        if (texto.isBlank()) {
            _uiState.value = UiState.Success(listaCompleta)
            return
        }

        val textoBusca = texto.trim().lowercase()

        val listaFiltrada = listaCompleta.filter { medico ->

            val nome = medico.usuario?.nome
                ?.lowercase()
                ?: ""

            val especialidade = medico.especialidades
                .firstOrNull()
                ?.especialidade
                ?.nome
                ?.lowercase()
                ?: ""

            nome.contains(textoBusca) ||
                    especialidade.contains(textoBusca)
        }

        _uiState.value = UiState.Success(listaFiltrada)
    }
}