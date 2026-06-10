package com.example.medicoapplication.viewmodel.medico.servicos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadeResponseDto
import com.example.medicoapplication.data.remote.DTO.medicoespecialidade.MedicoEspecialidadeResponseDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.EspecialidadeRepository
import com.example.medicoapplication.data.repository.MedicoEspecialidadeRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EspecialidadesViewModel(
    private val repository: MedicoEspecialidadeRepository = MedicoEspecialidadeRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val especialidade: List<MedicoEspecialidadeResponseDto>) : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState =
        MutableStateFlow<UiState>(UiState.Idle)

    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val _especialidades =
        MutableStateFlow<List<MedicoEspecialidadeResponseDto>>(emptyList())

    val especialidades:
            StateFlow<List<MedicoEspecialidadeResponseDto>> =
        _especialidades.asStateFlow()

    fun carregarEspecialidades() {

        viewModelScope.launch {

            _uiState.value = UiState.Loading

            repository.getMinhasEspecialidades()
                .onSuccess {

                    _especialidades.value = it

                    _uiState.value =
                        UiState.Success(it)
                }
                .onFailure {

                    _uiState.value =
                        UiState.Error(it.toNetworkError())
                }
        }
    }

    fun deletarEspecialidade(id: Long) {

        viewModelScope.launch {

            _uiState.value = UiState.Loading

            repository.deleteMedicoEspecialidade(id)
                .onSuccess {
                    carregarEspecialidades()
                }
                .onFailure {

                    _uiState.value =
                        UiState.Error(it.toNetworkError())
                }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }
}