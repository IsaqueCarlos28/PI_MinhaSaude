package com.example.medicoapplication.activities.auth_e_cadastro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.medico.MedicoCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteCreateRequestDto
import com.example.medicoapplication.data.repository.AuthRepository
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
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun cadastrarPaciente(dto: PacienteCreateRequestDto) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.cadastrarPaciente(dto)
                .onSuccess { _uiState.value = UiState.Success }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Erro desconhecido") }
        }
    }

    fun cadastrarMedico(dto: MedicoCreateRequestDto) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.cadastrarMedico(dto)
                .onSuccess { _uiState.value = UiState.Success }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Erro desconhecido") }
        }
    }

    fun resetState() { _uiState.value = UiState.Idle }
}
