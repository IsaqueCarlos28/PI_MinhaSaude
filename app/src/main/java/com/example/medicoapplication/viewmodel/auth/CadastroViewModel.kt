package com.example.medicoapplication.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.activities.paciente.viewmodel.ReagendarConsultaViewModel
import com.example.medicoapplication.data.remote.DTO.medico.MedicoCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteCreateRequestDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.AuthRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CadastroViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun cadastrarPaciente(dto: PacienteCreateRequestDto) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.cadastrarPaciente(dto)
                .onSuccess { _uiState.value = UiState.Success }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())  }
        }
    }

    fun cadastrarMedico(dto: MedicoCreateRequestDto) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.cadastrarMedico(dto)
                .onSuccess { _uiState.value = UiState.Success }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())  }
        }
    }

    fun resetState() { _uiState.value = UiState.Idle }
}