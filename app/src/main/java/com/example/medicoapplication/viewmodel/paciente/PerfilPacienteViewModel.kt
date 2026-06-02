package com.example.medicoapplication.activities.paciente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteResponseDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.PacienteRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PerfilPacienteViewModel(
    private val repository: PacienteRepository = PacienteRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val paciente: PacienteResponseDto) : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun carregarPerfil(idPaciente: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getPaciente(idPaciente)
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError()) }
        }
    }
}
