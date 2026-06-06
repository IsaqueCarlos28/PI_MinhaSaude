package com.example.medicoapplication.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.local.AppDependencies.sessionManager
import com.example.medicoapplication.data.local.SessionManager
import com.example.medicoapplication.data.remote.DTO.auth.LoginResponseDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.AuthRepository
import com.example.medicoapplication.data.repository.NetworkException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val usuario: LoginResponseDto) : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun login(email: String, senha: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.login(email, senha)
                .onSuccess {
                    _uiState.value = UiState.Success(it)
                }
                .onFailure {
                    throwable ->
                    val erro = (throwable as? NetworkException)?.error
                        ?: NetworkError.Desconhecido(throwable.message ?: "")
                    _uiState.value = UiState.Error(erro)

                }
            }
        }
    fun resetState() { _uiState.value = UiState.Idle }
}