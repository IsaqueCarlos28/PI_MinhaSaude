package com.example.medicoapplication.viewmodel.medico.servicos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadeResponseDto
import com.example.medicoapplication.data.remote.DTO.medicoespecialidade.MedicoEspecialidadeCreateRequestDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.EspecialidadeRepository
import com.example.medicoapplication.data.repository.MedicoEspecialidadeRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdicionarEspecialidadeViewModel(
    private val especialidadeRepository: EspecialidadeRepository =
        EspecialidadeRepository(),

    private val medicoEspecialidadeRepository:
    MedicoEspecialidadeRepository =
        MedicoEspecialidadeRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState =
        MutableStateFlow<UiState>(UiState.Idle)

    val uiState =
        _uiState.asStateFlow()

    private val _especialidades =
        MutableStateFlow<List<EspecialidadeResponseDto>>(emptyList())

    val especialidades =
        _especialidades.asStateFlow()

    fun carregarEspecialidades() {

        viewModelScope.launch {

            especialidadeRepository.getEspecialidades()
                .onSuccess {
                    _especialidades.value = it._embedded.especialidadeResponseDTOList
                }
                .onFailure {
                    _uiState.value =
                        UiState.Error(it.toNetworkError())
                }
        }
    }

    fun salvar(
        especialidadeId: Long,
        rqe: String
    ) {

        viewModelScope.launch {

            _uiState.value = UiState.Loading

            medicoEspecialidadeRepository
                .addMedicoEspecialidade(
                    MedicoEspecialidadeCreateRequestDto(
                        especialidadeId,
                        rqe
                    )
                )
                .onSuccess {
                    _uiState.value = UiState.Success
                }
                .onFailure {
                    _uiState.value =
                        UiState.Error(it.toNetworkError())
                }
        }
    }
}