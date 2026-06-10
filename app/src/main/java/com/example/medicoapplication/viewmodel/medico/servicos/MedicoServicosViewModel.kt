package com.example.medicoapplication.viewmodel.medico.servicos


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaResponseDto
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadeResponseDto
import com.example.medicoapplication.data.remote.DTO.medicoespecialidade.MedicoEspecialidadeResponseDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.ConsultaOfertadaRepository
import com.example.medicoapplication.data.repository.EspecialidadeRepository
import com.example.medicoapplication.data.repository.MedicoEspecialidadeRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedicoServicosViewModel(
    private val medicoEspecialidadeRepository: MedicoEspecialidadeRepository = MedicoEspecialidadeRepository(),
    private val consultaRepository: ConsultaOfertadaRepository = ConsultaOfertadaRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _especialidades =
        MutableStateFlow<List<MedicoEspecialidadeResponseDto>>(emptyList())
    val especialidades: StateFlow<List<MedicoEspecialidadeResponseDto>>
            = _especialidades.asStateFlow()

    private val _consultas =
        MutableStateFlow<List<ConsultaOfertadaResponseDto>>(emptyList())
    val consultas: StateFlow<List<ConsultaOfertadaResponseDto>>
            = _consultas.asStateFlow()

    fun carregarDados() {

        viewModelScope.launch {

            _uiState.value = UiState.Loading

            try {

                medicoEspecialidadeRepository.getMinhasEspecialidades()
                    .onSuccess {
                        _especialidades.value = it.take(6)
                    }
                    .onFailure {
                        _uiState.value =
                            UiState.Error(it.toNetworkError())
                        return@launch
                    }

                consultaRepository.getMinhaConsultasOfertadas()
                    .onSuccess {
                        _consultas.value = it.take(3)
                    }
                    .onFailure {
                        _uiState.value =
                            UiState.Error(it.toNetworkError())
                        return@launch
                    }

                _uiState.value = UiState.Success

            } catch (e: Exception) {
                _uiState.value =
                    UiState.Error(e.toNetworkError())
            }
        }
    }

    fun refresh() {
        carregarDados()
    }
}