package com.example.medicoapplication.viewmodel.paciente.consulta

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaUpdateRequestDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.ConsultaOfertadaRepository
import com.example.medicoapplication.data.repository.ConsultaRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReagendarConsultaViewModel(
    private val consultaRepository: ConsultaRepository = ConsultaRepository(),
    private val consultaOfertadaRepository: ConsultaOfertadaRepository = ConsultaOfertadaRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle    : UiState()
        object Loading : UiState()
        object Sucesso : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    // Separate state for the availability panel so a loading skeleton
    // can be shown without blocking the whole screen.
    sealed class DisponibilidadeState {
        object Idle        : DisponibilidadeState()
        object Loading     : DisponibilidadeState()
        object Vazio       : DisponibilidadeState()
        data class Success(val horariosPorDia: Map<String, List<String>>) : DisponibilidadeState()
        data class Error(val error: NetworkError) : DisponibilidadeState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _disponibilidadeState =
        MutableStateFlow<DisponibilidadeState>(DisponibilidadeState.Idle)
    val disponibilidadeState: StateFlow<DisponibilidadeState> = _disponibilidadeState

    // Horários available for the currently-selected day.
    private val _horariosDoDia = MutableStateFlow<List<String>>(emptyList())
    val horariosDoDia: StateFlow<List<String>> = _horariosDoDia

    // Internal state extracted from the loaded consulta.
    private var idConsultaOfertada: Long = -1L
    private var idMedico: Long = -1L
    private var horariosPorDia: Map<String, List<String>> = emptyMap()

    /**
     * Loads the consulta to get idConsultaOfertada + idMedico, then immediately
     * fetches the real availability so the paciente only sees valid slots.
     *
     * FIX: previously idConsultaOfertada was populated asynchronously and
     * reagendar() could run before it was ready, silently doing nothing.
     * Now the whole load-and-fetch is a single coroutine; reagendar() is
     * only enabled once disponibilidadeState is Success.
     */
    fun carregarConsultaEDisponibilidade(idEvento: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            consultaRepository.getConsultaByIdPaciente(idEvento)
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())
                    return@launch
                }
                .onSuccess { consulta ->
                    Log.d("REAGENDAR", "Consulta carregada:")
                    Log.d("REAGENDAR", "idEvento=$idEvento")
                    Log.d("REAGENDAR", "idConsultaOfertada=${consulta.idConsultaOfertada}")
                    Log.d("REAGENDAR", "idMedico=${consulta.idUsuarioMedico}")

                    idConsultaOfertada = consulta.idConsultaOfertada
                    idMedico           = consulta.idUsuarioMedico
                    _uiState.value     = UiState.Idle
                }

            // Fetch real availability — only reachable when onSuccess above ran.
            _disponibilidadeState.value = DisponibilidadeState.Loading

            consultaOfertadaRepository.getDisponibilidadePublica(idMedico, idConsultaOfertada)
                .onSuccess { semanas ->
                    val mapa = semanas
                        .flatMap { it.dias }
                        .filter { it.horarios.isNotEmpty() }
                        .associate { dia -> dia.data to dia.horarios }

                    horariosPorDia = mapa

                    _disponibilidadeState.value = if (mapa.isEmpty()) {
                        DisponibilidadeState.Vazio
                    } else {
                        DisponibilidadeState.Success(mapa)
                    }
                }
                .onFailure { throwable ->
                    _disponibilidadeState.value =
                        DisponibilidadeState.Error(throwable.toNetworkError())
                }
        }
    }

    /**
     * Filters available slots for the given date.
     * Called by the Activity whenever the user navigates days.
     */
    fun selecionarDia(data: String) {
        _horariosDoDia.value = horariosPorDia[data] ?: emptyList()
    }

    /**
     * Submits the reschedule request.
     *
     * FIX: guard now also checks that availability was loaded successfully,
     * so the method is only callable from a state where idConsultaOfertada
     * is guaranteed to be populated.
     */
    fun reagendar(
        idEvento: Long,
        data: String,
        horaInicio: String
    ) {
        if (idConsultaOfertada == -1L || _disponibilidadeState.value !is DisponibilidadeState.Success) {
            // Availability not ready yet — UI should keep the confirm button
            // disabled while disponibilidadeState != Success, so this is a
            // defensive guard, not a user-facing path.
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
            consultaRepository.reagendarConsulta(idEvento, dto)
                .onSuccess { _uiState.value = UiState.Sucesso }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())
                }
        }
    }
}
