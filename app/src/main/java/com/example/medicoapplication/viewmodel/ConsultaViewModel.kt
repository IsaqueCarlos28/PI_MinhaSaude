package com.example.medicoapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaStatusRequestDto
import com.example.medicoapplication.data.repository.ConsultaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConsultaViewModel(
    private val repository: ConsultaRepository = ConsultaRepository()
) : ViewModel(){

    sealed class UiState {
        object Idle : UiState()
        object Loanding : UiState()
        data class Success(val consulta: ConsultaResponseDto) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    var uiState: StateFlow<UiState>  = _uiState.asStateFlow()

    fun cancelarConsulta(idPaciente: Long,
                         idEvento: Long,
                         dto: ConsultaStatusRequestDto){
        _uiState.value = UiState.Loanding
        viewModelScope.launch {
            repository.atualizarStatusPaciente(idPaciente,idEvento,dto)
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Erro desconhecido") }
        }
    }


}