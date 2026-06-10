package com.example.medicoapplication.viewmodel.paciente.consulta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
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

    // Full unfiltered list – kept in memory so date filtering is local (no extra API call)
    private var todasConsultas: List<ConsultaResponseDto> = emptyList()

    // Dates that have at least one consulta – used to highlight calendar cells
    private val _datasComConsulta = MutableStateFlow<Set<String>>(emptySet())
    val datasComConsulta: StateFlow<Set<String>> = _datasComConsulta

    fun carregarConsultas() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            pacienteRepository.getConsultas()
                .onSuccess { consultas ->
                    todasConsultas = consultas
                    _datasComConsulta.value = consultas.map { it.data }.toSet()
                    _uiState.value = UiState.Success(consultas)
                }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())
                }
        }
    }

    /**
     * FIX: filters the loaded list by date and emits a new Success state so
     * the Activity's RecyclerView updates. No API call is made.
     *
     * @param dataApi Date in "yyyy-MM-dd" format, matching ConsultaResponseDto.data.
     *                Pass null to clear the filter and show all.
     */
    fun filtrarPorData(dataApi: String?) {
        val filtradas = if (dataApi == null) {
            todasConsultas
        } else {
            todasConsultas.filter { it.data == dataApi }
        }
        _uiState.value = UiState.Success(filtradas)
    }
}
