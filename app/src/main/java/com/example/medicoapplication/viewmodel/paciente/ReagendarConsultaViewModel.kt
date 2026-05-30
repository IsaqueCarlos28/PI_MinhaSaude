package com.example.medicoapplication.activities.paciente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaUpdateRequestDto
import com.example.medicoapplication.data.repository.ConsultaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReagendarConsultaViewModel(
    private val repository: ConsultaRepository = ConsultaRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle    : UiState()
        object Loading : UiState()
        object Sucesso : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun reagendar(
        idPaciente: Long,
        idEvento: Long,
        idConsultaOfertada: Long,
        data: String,
        horaInicio: String,
        idConvenio: Long? = null
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val dto = ConsultaUpdateRequestDto(
                idConsultaOfertada = idConsultaOfertada,
                idConvenio         = idConvenio,
                data               = data,
                horaInicio         = horaInicio
            )
            repository.reagendarConsulta(idPaciente, idEvento, dto)
                .onSuccess { _uiState.value = UiState.Sucesso }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Erro ao reagendar consulta") }
        }
    }
}
