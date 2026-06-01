package com.example.medicoapplication.viewmodel.paciente.consulta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.ConsultaRepository
import com.example.medicoapplication.data.repository.toNetworkError
import com.example.medicoapplication.viewmodel.medico.consulta.ConsultasMedicoViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConfirmacaoConsultaViewModel(
    private val repository: ConsultaRepository = ConsultaRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle    : UiState()
        object Loading : UiState()
        data class Success (val consulta: ConsultaResponseDto): UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun confirmarConsulta(
        idPaciente: Long,
        idConsultaOfertada: Long,
        data: String,
        horaInicio: String,
        idConvenio: Long? = null
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val dto = ConsultaCreateRequestDto(
                idConsultaOfertada = idConsultaOfertada,
                idConvenio = idConvenio,
                data = data,
                horaInicio = horaInicio
            )
            repository.agendarConsulta(idPaciente, dto)
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())
                }
        }
    }
}