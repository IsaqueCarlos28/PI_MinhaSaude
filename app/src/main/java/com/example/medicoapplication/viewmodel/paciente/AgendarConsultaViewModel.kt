package com.example.medicoapplication.activities.paciente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.PacienteRepository
import com.example.medicoapplication.data.repository.toNetworkError
import com.example.medicoapplication.viewmodel.paciente.consulta.DetalheConsultaViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AgendarConsultaViewModel(
    private val repository: PacienteRepository = PacienteRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class MedicoCarregado(val medico: MedicoResponseDto) : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun carregarMedico(idMedico: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getMedico(idMedico)
                .onSuccess { _uiState.value = UiState.MedicoCarregado(it) }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError()) }
        }
    }
}
