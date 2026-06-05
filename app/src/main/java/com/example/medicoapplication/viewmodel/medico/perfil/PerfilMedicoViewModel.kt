package com.example.medicoapplication.viewmodel.medico.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.MedicoRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilMedicoViewModel(
    private val repository: MedicoRepository = MedicoRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val medico: MedicoResponseDto) : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun carregarPerfil() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getMedico()
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure {throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())}
        }
    }
}