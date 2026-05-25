package com.example.medicoapplication.activities.medico.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.repository.MedicoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConsultasMedicoViewModel(
    private val repository: MedicoRepository = MedicoRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val consultas: List<ConsultaResponseDto>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun carregarConsultas(idMedico: Long, filtroStatus: String? = null) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getConsultas(idMedico)
                .onSuccess { todas ->
                    val filtradas = if (filtroStatus != null)
                        todas.filter { it.status?.name == filtroStatus }
                    else todas
                    _uiState.value = UiState.Success(filtradas)
                }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Erro ao carregar consultas") }
        }
    }
}
