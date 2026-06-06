package com.example.medicoapplication.viewmodel.paciente.consulta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaUpdateRequestDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.ConsultaRepository
import com.example.medicoapplication.data.repository.toNetworkError
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
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    // idConsultaOfertada extraído da consulta carregada, usado ao reagendar
    private var idConsultaOfertada: Long = -1L

    fun carregarConsulta(idEvento: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getConsultaByIdPaciente(idEvento)
                .onSuccess { consulta ->
                    idConsultaOfertada = consulta.idConsultaOfertada
                    _uiState.value = UiState.Idle
                }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())
                }
        }
    }

    fun reagendar(
        idEvento: Long,
        data: String,
        horaInicio: String
    ) {
        if (idConsultaOfertada == -1L) {
            // A consulta ainda não foi carregada; não deve chegar aqui em uso normal
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val dto = ConsultaUpdateRequestDto(
                idConsultaOfertada = idConsultaOfertada,
                idConvenio         = null,
                data               = data,
                horaInicio         = horaInicio
            )
            repository.reagendarConsulta(idEvento, dto)
                .onSuccess { _uiState.value = UiState.Sucesso }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())
                }
        }
    }
}
