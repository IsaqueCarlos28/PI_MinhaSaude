package com.example.medicoapplication.viewmodel.medico.consulta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.MedicoRepository
import com.example.medicoapplication.data.repository.NetworkException
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConsultasMedicoViewModel(
    private val repository: MedicoRepository = MedicoRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val consultas: List<ConsultaResponseDto>) : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun carregarConsultas(idMedico: Long, filtroStatus: String? = null) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getConsultas(idMedico)
            .onSuccess {
                _uiState.value = UiState.Success(it)
            }
            .onFailure { throwable ->
                _uiState.value = UiState.Error(throwable.toNetworkError())
            }
        }
    }
}