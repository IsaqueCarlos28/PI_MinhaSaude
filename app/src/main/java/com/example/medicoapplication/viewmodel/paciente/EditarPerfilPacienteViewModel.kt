package com.example.medicoapplication.activities.paciente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.Genero
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteEditRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteResponseDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.remote.RetrofitClient
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

    fun carregarPerfil(idPaciente: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getPaciente(idPaciente)
                .onSuccess { _uiState.value = UiState.Carregado(it) }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError()) }
        }
    }

    fun salvarPerfil(
        idPaciente: Long,
        dto : PacienteEditRequestDto
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.updatePaciente(idPaciente, dto)
            .onSuccess{_uiState.value = UiState.Carregado(it) }
            .onFailure {throwable ->
                _uiState.value = UiState.Error(throwable.toNetworkError(""))
            }
        }
    }
}
