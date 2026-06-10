package com.example.medicoapplication.viewmodel.medico.servicos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.agenda.AgendaRequestDTO
import com.example.medicoapplication.data.remote.DTO.agenda.AgendaResponseDto
import com.example.medicoapplication.data.remote.DTO.agenda.DiaSemana
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.AgendaRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SetarAgendaViewModel(
    private val agendaRepository: AgendaRepository = AgendaRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Salvo : UiState()     // agenda adicionada/editada com sucesso
        object Excluido : UiState()  // agenda removida com sucesso
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _agendas = MutableStateFlow<List<AgendaResponseDto>>(emptyList())
    val agendas: StateFlow<List<AgendaResponseDto>> = _agendas.asStateFlow()

    fun carregarAgendas(idConsulta: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            agendaRepository.getAgendas(idConsulta)
                .onSuccess {
                    _agendas.value = it
                    _uiState.value = UiState.Idle
                }
                .onFailure {
                    _uiState.value = UiState.Error(it.toNetworkError())
                }
        }
    }

    fun adicionarAgenda(
        idConsulta: Long,
        diaSemana: DiaSemana,
        horaInicio: String,
        horaFim: String
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val dto = AgendaRequestDTO(diaSemana, horaInicio, horaFim)
            agendaRepository.createAgenda(idConsulta, dto)
                .onSuccess {
                    carregarAgendas(idConsulta)
                    _uiState.value = UiState.Salvo
                }
                .onFailure {
                    _uiState.value = UiState.Error(it.toNetworkError())
                }
        }
    }

    fun editarAgenda(
        idConsulta: Long,
        idAgenda: Long,
        diaSemana: DiaSemana,
        horaInicio: String,
        horaFim: String
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val dto = AgendaRequestDTO(diaSemana, horaInicio, horaFim)
            agendaRepository.updateAgenda(idConsulta, idAgenda, dto)
                .onSuccess {
                    carregarAgendas(idConsulta)
                    _uiState.value = UiState.Salvo
                }
                .onFailure {
                    _uiState.value = UiState.Error(it.toNetworkError())
                }
        }
    }

    fun excluirAgenda(idConsulta: Long, idAgenda: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            agendaRepository.deleteAgenda(idConsulta, idAgenda)
                .onSuccess {
                    carregarAgendas(idConsulta)
                    _uiState.value = UiState.Excluido
                }
                .onFailure {
                    _uiState.value = UiState.Error(it.toNetworkError())
                }
        }
    }

    fun resetState() { _uiState.value = UiState.Idle }
}
