package com.example.medicoapplication.viewmodel.paciente.consulta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaStatusRequestDto
import com.example.medicoapplication.data.remote.DTO.consulta.StatusConsulta
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.remote.RetrofitClient
import com.example.medicoapplication.data.repository.ConsultaRepository
import com.example.medicoapplication.data.repository.NetworkException
import com.example.medicoapplication.data.repository.toNetworkError
import com.example.medicoapplication.viewmodel.medico.consulta.ConsultasMedicoViewModel
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
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    // Usa o endpoint direto GET pacientes/{id}/consultas/{idEvento}
    fun carregarConsulta(idEvento: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            consultaRepository.getConsultaByIdPaciente(idEvento)
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())
                }

        }

    }


    fun cancelarConsulta( idEvento: Long, onSucesso: () -> Unit) {
        viewModelScope.launch {
            consultaRepository.atualizarStatusPaciente(
                idEvento,
                ConsultaStatusRequestDto(StatusConsulta.CANCELADA)
            )
                .onSuccess { onSucesso() }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())
                }

        }

    }

}
