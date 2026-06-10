package com.example.medicoapplication.viewmodel.paciente.consulta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaStatusRequestDto
import com.example.medicoapplication.data.remote.DTO.consulta.StatusConsulta
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.ConsultaOfertadaRepository
import com.example.medicoapplication.data.repository.ConsultaRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetalheConsultaViewModel(
    private val consultaRepository: ConsultaRepository = ConsultaRepository(),
    private val consultaOfertadaRepository: ConsultaOfertadaRepository = ConsultaOfertadaRepository()
) : ViewModel() {

    data class ConsultaDetalheUi(
        val consulta: ConsultaResponseDto,
        val enderecoCompleto: String
    )

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val detalhe: ConsultaDetalheUi) : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun carregarConsulta(idEvento: Long) {
        viewModelScope.launch {

            _uiState.value = UiState.Loading

            consultaRepository.getConsultaByIdPaciente(idEvento)
                .onSuccess { consulta ->

                    consultaOfertadaRepository
                        .getConsultaOfertada(
                            consulta.idUsuarioMedico,
                            consulta.idConsultaOfertada
                        )
                        .onSuccess { consultaOfertada ->

                            val endereco = consultaOfertada.local?.endereco

                            val enderecoCompleto = listOfNotNull(
                                endereco?.logradouro?.let {
                                    if (!endereco.numero.isNullOrBlank())
                                        "$it, ${endereco.numero}"
                                    else
                                        it
                                },
                                endereco?.bairro,
                                endereco?.cidade?.let { cidade ->
                                    if (!endereco.uf.isNullOrBlank())
                                        "$cidade - ${endereco.uf}"
                                    else
                                        cidade
                                },
                                endereco?.cep
                            ).joinToString("\n")

                            _uiState.value = UiState.Success(
                                ConsultaDetalheUi(
                                    consulta = consulta,
                                    enderecoCompleto = enderecoCompleto.ifBlank {
                                        "Endereço não informado"
                                    }
                                )
                            )
                        }
                        .onFailure {
                            _uiState.value = UiState.Success(
                                ConsultaDetalheUi(
                                    consulta = consulta,
                                    enderecoCompleto = "Endereço não informado"
                                )
                            )
                        }
                }
                .onFailure {
                    _uiState.value = UiState.Error(it.toNetworkError())
                }
        }
    }

    fun cancelarConsulta(
        idEvento: Long,
        onSucesso: () -> Unit
    ) {
        viewModelScope.launch {
            consultaRepository.atualizarStatusPaciente(
                idEvento,
                ConsultaStatusRequestDto(StatusConsulta.CANCELADA)
            )
                .onSuccess { onSucesso() }
                .onFailure {
                    _uiState.value = UiState.Error(it.toNetworkError())
                }
        }
    }
}