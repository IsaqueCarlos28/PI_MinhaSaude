package com.example.medicoapplication.activities.auth_e_cadastro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    // Token returned by validarCodigo. Forwarded via Intent to ResetPasswordActivity,
    // which injects it back via setToken().
    var tokenRecuperacao: String? = null
        private set

    fun setToken(token: String?) { tokenRecuperacao = token }

    fun esqueceuSenha(email: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.esqueceuSenha(email)
                .onSuccess { _uiState.value = UiState.Success }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Erro desconhecido") }
        }
    }

    fun validarCodigo(email: String?, codigo: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.validarCodigo(email, codigo)
                .onSuccess {
                    tokenRecuperacao = it.tokenRecuperacao
                    _uiState.value = UiState.Success
                }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Erro desconhecido") }
        }
    }

    fun alterarSenha(novaSenha: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.alterarSenha(tokenRecuperacao, novaSenha)
                .onSuccess { _uiState.value = UiState.Success }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Erro desconhecido") }
        }
    }

    fun resetState() { _uiState.value = UiState.Idle }
}
