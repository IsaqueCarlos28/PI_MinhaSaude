package com.example.medicoapplication.viewmodel.medico.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.MedicoRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AgendaMedicoViewModel(
    private val repository: MedicoRepository = MedicoRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle    : UiState()
        object Loading : UiState()
        data class NomeCarregado(val nome: String) : UiState()
        data class Error(val error: NetworkError)  : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun carregarNome() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getMedico()
                .onSuccess { medico ->
                    val nome = medico.usuario?.nome ?: "Médico"
                    _uiState.value = UiState.NomeCarregado(nome)
                }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())
                }
        }
    }
}
