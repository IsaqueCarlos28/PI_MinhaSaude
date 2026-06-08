package com.example.medicoapplication.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.viewmodel.paciente.consulta.ReagendarConsultaViewModel
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.AuthRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {
    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    var tokenRecuperacao: String? = " "
        private set

    private lateinit var token: String

    fun setToken(token: String) {
        this.token = token
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }

    fun esqueceuSenha(email: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            repository.esqueceuSenha(email)
                .onSuccess {
                    _uiState.value = UiState.Success
                }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())
                }
        }
    }

    fun validarCodigo(email: String?, codigo: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            repository.validarCodigo(email, codigo)
                .onSuccess { response ->

                    // Adjust this if your DTO field name differs
                    tokenRecuperacao = response.tokenRecuperacao

                    _uiState.value = UiState.Success
                }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())
                }
        }
    }

    fun alterarSenha(novaSenha: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            repository.alterarSenha(token, novaSenha)
                .onSuccess {
                    _uiState.value = UiState.Success
                }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())

                }
        }
    }
}