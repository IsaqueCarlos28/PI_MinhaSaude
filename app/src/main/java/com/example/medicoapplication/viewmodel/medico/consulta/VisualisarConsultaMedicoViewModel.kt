package com.example.medicoapplication.viewmodel.medico.consulta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.StatusConsulta
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaStatusRequestDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.ConsultaRepository
import com.example.medicoapplication.data.repository.MedicoRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VisualisarConsultaMedicoViewModel(
    private val medicoRepository: MedicoRepository = MedicoRepository(),
    private val consultaRepository: ConsultaRepository = ConsultaRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle    : UiState()
        object Loading : UiState()
        data class Success(val consulta: ConsultaResponseDto) : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    sealed class AcaoState {
        object Idle    : AcaoState()
        object Loading : AcaoState()
        object Sucesso : AcaoState()
        data class Error(val error: NetworkError) : AcaoState()
    }

    private val _uiState    = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _acaoState  = MutableStateFlow<AcaoState>(AcaoState.Idle)
    val acaoState: StateFlow<AcaoState> = _acaoState.asStateFlow()

    // -------------------------------------------------------------------
    // Carrega o detalhe via GET medicos/{id}/consultas-agendadas/{idEvento}
    // -------------------------------------------------------------------
    fun carregarConsulta(idEvento: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            medicoRepository.getConsultaById(idEvento)
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.toNetworkError()) }
        }
    }

    // -------------------------------------------------------------------
    // PATCH medicos/{id}/consultas-agendadas/{idEvento}/status → REALIZADA
    // -------------------------------------------------------------------
    fun marcarComoRealizada(idEvento: Long) {
        atualizarStatus(idEvento, StatusConsulta.REALIZADA)
    }

    // -------------------------------------------------------------------
    // PATCH medicos/{id}/consultas-agendadas/{idEvento}/status → CANCELADA
    // -------------------------------------------------------------------
    fun cancelarConsulta(idEvento: Long) {
        atualizarStatus(idEvento, StatusConsulta.CANCELADA)
    }

    fun resetAcaoState() {
        _acaoState.value = AcaoState.Idle
    }

    // -------------------------------------------------------------------
    private fun atualizarStatus(idEvento: Long, novoStatus: StatusConsulta) {
        viewModelScope.launch {
            _acaoState.value = AcaoState.Loading
            consultaRepository.atualizarStatusMedico(
                idEvento,
                ConsultaStatusRequestDto(novoStatus)
            )
                .onSuccess { _acaoState.value = AcaoState.Sucesso }
                .onFailure { _acaoState.value = AcaoState.Error(it.toNetworkError()) }
        }
    }
}
