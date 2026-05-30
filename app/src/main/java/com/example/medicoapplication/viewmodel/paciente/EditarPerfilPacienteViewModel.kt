package com.example.medicoapplication.activities.paciente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.Genero
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteEditRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteResponseDto
import com.example.medicoapplication.data.remote.RetrofitClient
import com.example.medicoapplication.data.repository.PacienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditarPerfilPacienteViewModel(
    private val repository: PacienteRepository = PacienteRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle    : UiState()
        object Loading : UiState()
        object Salvo   : UiState()
        data class Carregado(val paciente: PacienteResponseDto) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun carregarPerfil(idPaciente: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getPaciente(idPaciente)
                .onSuccess { _uiState.value = UiState.Carregado(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Erro ao carregar perfil") }
        }
    }

    fun salvarPerfil(
        idPaciente: Long,
        nome: String,
        cpf: String,
        email: String,
        telefone: String,
        genero: Genero,
        dataNascimento: String
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val dto = PacienteEditRequestDto(
                nome           = nome,
                cpf            = cpf,
                email          = email,
                telefone       = telefone,
                genero         = genero,
                dataNascimento = dataNascimento
            )
            runCatching {
                val response = RetrofitClient.api.updatePaciente(idPaciente, dto)
                if (response.isSuccessful) {
                    _uiState.value = UiState.Salvo
                } else {
                    _uiState.value = UiState.Error("Erro ${response.code()}: não foi possível salvar")
                }
            }.onFailure {
                _uiState.value = UiState.Error(it.message ?: "Erro de rede")
            }
        }
    }
}
