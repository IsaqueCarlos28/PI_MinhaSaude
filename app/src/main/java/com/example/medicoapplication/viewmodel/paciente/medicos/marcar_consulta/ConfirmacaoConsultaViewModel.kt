package com.example.medicoapplication.viewmodel.paciente.medicos.marcar_consulta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaResponseDto
import com.example.medicoapplication.data.remote.DTO.convenio.ConvenioResponseDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.ConsultaOfertadaRepository
import com.example.medicoapplication.data.repository.ConsultaRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConfirmacaoConsultaViewModel(
    private val consultaRepository: ConsultaRepository = ConsultaRepository(),
    private val consultaOfertadaRepository: ConsultaOfertadaRepository = ConsultaOfertadaRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle    : UiState()
        object Loading : UiState()
        data class ConsultaOfertadaCarregada(val consulta: ConsultaOfertadaResponseDto) : UiState()
        data class Success(val consulta: ConsultaResponseDto) : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // The convenio the user has selected (null = particular)
    private val _convenioSelecionado = MutableStateFlow<ConvenioResponseDto?>(null)
    val convenioSelecionado: StateFlow<ConvenioResponseDto?> = _convenioSelecionado.asStateFlow()

    // Cached consulta ofertada so we know aceitaParticular + accepted convenios
    private var consultaOfertada: ConsultaOfertadaResponseDto? = null

    /**
     * Loads the ConsultaOfertada to expose aceitaParticular and conveniosAceitos
     * to the Activity, which uses them to build the convenio selection UI.
     */
    fun carregarConsultaOfertada(idMedico: Long, idConsultaOfertada: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            consultaOfertadaRepository.getConsultaOfertada(idMedico, idConsultaOfertada)
                .onSuccess { co ->
                    consultaOfertada = co
                    _uiState.value = UiState.ConsultaOfertadaCarregada(co)
                }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.toNetworkError())
                }
        }
    }

    /** Called when the user picks a convenio from the list, or taps "Particular". */
    fun selecionarConvenio(convenio: ConvenioResponseDto?) {
        _convenioSelecionado.value = convenio
    }

    fun confirmarConsulta(
        idConsultaOfertada: Long,
        data: String,
        horaInicio: String
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val dto = ConsultaCreateRequestDto(
                idConsultaOfertada = idConsultaOfertada,
                idConvenio         = _convenioSelecionado.value?.id,
                data               = data,
                horaInicio         = horaInicio
            )
            consultaRepository.agendarConsulta(dto)
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.toNetworkError()) }
        }
    }
}
