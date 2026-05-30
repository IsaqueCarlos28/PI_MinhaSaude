package com.example.medicoapplication.activities.paciente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaStatusRequestDto
import com.example.medicoapplication.data.remote.DTO.consulta.StatusConsulta
import com.example.medicoapplication.data.remote.RetrofitClient
import com.example.medicoapplication.data.repository.ConsultaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetalheConsultaViewModel(
    private val consultaRepository: ConsultaRepository = ConsultaRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle    : UiState()
        object Loading : UiState()
        data class Success(val consulta: ConsultaResponseDto) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    // Usa o endpoint direto GET pacientes/{id}/consultas/{idEvento}
    fun carregarConsulta(idPaciente: Long, idEvento: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching {
                val response = RetrofitClient.api.getConsultaByIdPaciente(idPaciente, idEvento)
                if (response.isSuccessful) {
                    response.body() ?: error("Resposta vazia do servidor")
                } else {
                    error("Erro ${response.code()}: ${response.message()}")
                }
            }
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Erro ao carregar consulta") }
        }
    }

    fun cancelarConsulta(idPaciente: Long, idEvento: Long, onSucesso: () -> Unit) {
        viewModelScope.launch {
            consultaRepository.atualizarStatusPaciente(
                idPaciente,
                idEvento,
                ConsultaStatusRequestDto(StatusConsulta.CANCELADA)
            )
                .onSuccess { onSucesso() }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Não foi possível cancelar") }
        }
    }
}
