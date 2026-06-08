package com.example.medicoapplication.viewmodel.medico.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.MedicoRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class AgendaMedicoViewModel(
    private val repository: MedicoRepository = MedicoRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle    : UiState()
        object Loading : UiState()
        data class NomeCarregado(val nome: String) : UiState()
        data class ConsultasCarregadas(val consultas: List<ConsultaResponseDto>) : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Cache de todas as consultas para filtrar por dia sem nova requisição
    private var todasConsultas: List<ConsultaResponseDto> = emptyList()

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

    fun carregarConsultas() {
        viewModelScope.launch {
            repository.getConsultas()
                .onSuccess { lista ->
                    todasConsultas = lista
                    // Ao carregar, filtra para o dia de hoje
                    filtrarPorData(LocalDate.now())
                }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())
                }
        }
    }

    fun filtrarPorData(data: LocalDate) {
        val dataStr = data.toString() // "yyyy-MM-dd"
        val filtradas = todasConsultas.filter { it.data == dataStr }
        _uiState.value = UiState.ConsultasCarregadas(filtradas)
    }
}