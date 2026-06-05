package com.example.medicoapplication.activities.paciente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.StatusConsulta
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaStatusRequestDto
import com.example.medicoapplication.data.repository.PacienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomePacienteViewModel(
    private val repository: PacienteRepository = PacienteRepository()
) : ViewModel() {

    sealed class NomeState {
        object Idle : NomeState()
        data class Success(val primeiroNome: String) : NomeState()
    }

    sealed class ConsultasState {
        object Idle : ConsultasState()
        object Loading : ConsultasState()
        data class Success(val consultas: List<ConsultaResponseDto>) : ConsultasState()
        data class Error(val message: String) : ConsultasState()
    }

    private val _nomeState = MutableStateFlow<NomeState>(NomeState.Idle)
    val nomeState: StateFlow<NomeState> = _nomeState

    private val _consultasState = MutableStateFlow<ConsultasState>(ConsultasState.Idle)
    val consultasState: StateFlow<ConsultasState> = _consultasState

    fun carregarNome(idPaciente: Long, emailFallback: String) {
        viewModelScope.launch {
            repository.getPaciente()
                .onSuccess { paciente ->
                    val nome = paciente.nome?.split(" ")?.firstOrNull()
                        ?: emailFallback.substringBefore("@")
                    _nomeState.value = NomeState.Success(nome)
                }
                .onFailure {
                    _nomeState.value = NomeState.Success(emailFallback.substringBefore("@"))
                }
        }
    }

    fun carregarConsultas(idPaciente: Long) {
        viewModelScope.launch {
            _consultasState.value = ConsultasState.Loading
            repository.getConsultas()
                .onSuccess { todas ->
                    val proximas = todas
                        .filter { it.status == StatusConsulta.AGENDADA }
                        .sortedWith(compareBy({ it.data }, { it.horaInicio }))
                    _consultasState.value = ConsultasState.Success(proximas)
                }
                .onFailure { _consultasState.value = ConsultasState.Error(it.message ?: "Erro de conexão") }
        }
    }

}
