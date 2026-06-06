package com.example.medicoapplication.viewmodel.paciente.medicos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaResponseDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.ConsultaOfertadaRepository
import com.example.medicoapplication.data.repository.MedicoRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilMedicoPublicoViewModel(
    private val medicoRepository: MedicoRepository = MedicoRepository(),
    private val consultaOfertadaRepository: ConsultaOfertadaRepository = ConsultaOfertadaRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle    : UiState()
        object Loading : UiState()
        data class Success(
            val medico: MedicoResponseDto,
            val consultasOfertadas: List<ConsultaOfertadaResponseDto>
        ) : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun carregarPerfil(idMedico: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val medicoResult = medicoRepository.getMedicoById(idMedico)
            medicoResult.onFailure { throwable ->
                _uiState.value = UiState.Error(throwable.toNetworkError())
                return@launch
            }
            val medico = medicoResult.getOrThrow()

            val consultasResult = consultaOfertadaRepository.getConsultasOfertadas(idMedico)
            consultasResult.onFailure { throwable ->
                _uiState.value = UiState.Error(throwable.toNetworkError())
                return@launch
            }
            val consultas = consultasResult.getOrThrow()

            _uiState.value = UiState.Success(medico, consultas)
        }
    }
}
