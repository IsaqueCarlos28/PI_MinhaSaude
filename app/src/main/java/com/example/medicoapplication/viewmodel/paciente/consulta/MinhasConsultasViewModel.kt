package com.example.medicoapplication.viewmodel.paciente.consulta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaStatusRequestDto
import com.example.medicoapplication.data.remote.DTO.consulta.StatusConsulta
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.ConsultaRepository
import com.example.medicoapplication.data.repository.PacienteRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MinhasConsultasViewModel(
    private val pacienteRepository: PacienteRepository = PacienteRepository(),
    private val consultaRepository: ConsultaRepository = ConsultaRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle    : UiState()
        object Loading : UiState()
        data class Success(val consultas: List<ConsultaResponseDto>) : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun carregarConsultas() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            pacienteRepository.getConsultas()
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())}
        }
    }

    fun cancelarConsulta(consulta: ConsultaResponseDto, onSucesso: () -> Unit) {
        viewModelScope.launch {
            consultaRepository.atualizarStatusPaciente(
                consulta.id,
                ConsultaStatusRequestDto(StatusConsulta.CANCELADA)
            )
                .onSuccess { onSucesso() }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())}
        }
    }
}