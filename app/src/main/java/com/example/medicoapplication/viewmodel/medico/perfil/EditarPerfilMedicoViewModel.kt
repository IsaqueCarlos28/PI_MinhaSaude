package com.example.medicoapplication.viewmodel.medico.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.medico.MedicoEditRequestDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.MedicoRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditarPerfilMedicoViewModel(
    private val repository: MedicoRepository = MedicoRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Salvo : UiState()
        data class Carregado(val medico: MedicoResponseDto) : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun carregarPerfil() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getMedico()
                .onSuccess { _uiState.value = UiState.Carregado(it) }
                .onFailure { throwable -> _uiState.value = UiState.Error(throwable.toNetworkError()) }
        }
    }

    fun salvarPerfil(dto: MedicoEditRequestDto) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.updateMedico(dto)
                .onSuccess { _uiState.value = UiState.Salvo }
                .onFailure { throwable -> _uiState.value = UiState.Error(throwable.toNetworkError()) }
        }
    }
}
