package com.example.medicoapplication.viewmodel.paciente.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteEditRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteResponseDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.PacienteRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditarPerfilPacienteViewModel(
    private val repository: PacienteRepository = PacienteRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle    : UiState()
        object Loading : UiState()
        object Salvo   : UiState()
        data class Carregado(val paciente: PacienteResponseDto) : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun carregarPerfil() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getPaciente()
                .onSuccess { _uiState.value = UiState.Carregado(it) }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError()) }
        }
    }

    fun salvarPerfil(
        dto : PacienteEditRequestDto
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.updatePaciente( dto)
            .onSuccess{_uiState.value = UiState.Carregado(it) }
            .onFailure {throwable ->
                _uiState.value = UiState.Error(throwable.toNetworkError(""))
            }
        }
    }
}