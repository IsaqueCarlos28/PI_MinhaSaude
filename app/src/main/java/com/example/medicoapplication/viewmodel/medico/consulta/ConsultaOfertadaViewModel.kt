package com.example.medicoapplication.viewmodel.medico.consulta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.local.SessionManager
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaResponseDto
import com.example.medicoapplication.data.remote.DTO.consultaofertada.TipoConsulta
import com.example.medicoapplication.data.remote.DTO.convenio.ConvenioResponseDto
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadeResponseDto
import com.example.medicoapplication.data.remote.DTO.local.LocalResponseDto
import com.example.medicoapplication.data.repository.ConsultaOfertadaRepository
import com.example.medicoapplication.data.repository.ConvenioRepository
import com.example.medicoapplication.data.repository.EspecialidadeRepository
import com.example.medicoapplication.data.repository.LocalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConsultaOfertadaViewModel(
    private val repository: ConsultaOfertadaRepository = ConsultaOfertadaRepository(),
    private val especialidadeRepo: EspecialidadeRepository = EspecialidadeRepository(),
    private val localRepo: LocalRepository = LocalRepository(),
    private val convenioRepo: ConvenioRepository = ConvenioRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _consultas = MutableStateFlow<List<ConsultaOfertadaResponseDto>>(emptyList())
    val consultas: StateFlow<List<ConsultaOfertadaResponseDto>> = _consultas.asStateFlow()

    private val _especialidades = MutableStateFlow<List<EspecialidadeResponseDto>>(emptyList())
    val especialidades: StateFlow<List<EspecialidadeResponseDto>> = _especialidades.asStateFlow()

    private val _locais = MutableStateFlow<List<LocalResponseDto>>(emptyList())
    val locais: StateFlow<List<LocalResponseDto>> = _locais.asStateFlow()

    private val _convenios = MutableStateFlow<List<ConvenioResponseDto>>(emptyList())
    val convenios: StateFlow<List<ConvenioResponseDto>> = _convenios.asStateFlow()

    fun carregarConsultas(idMedico: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getConsultasOfertadas(idMedico)
                .onSuccess { _consultas.value = it; _uiState.value = UiState.Idle }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Erro ao carregar consultas") }
        }
    }

    fun carregarDadosFormulario(idMedico: Long) {
        viewModelScope.launch {
            especialidadeRepo.getEspecialidades()
                .onSuccess { page ->
                    _especialidades.value = page._embedded.especialidadeResponseDTOList
                }
            localRepo.getLocais()
                .onSuccess { page ->
                    _locais.value = page._embedded.localResponseDTOList
                }
            convenioRepo.getConvenios()
                .onSuccess { page ->
                    _convenios.value = page._embedded.convenioResponseDTOList
                }
        }
    }

    fun criarConsultaOfertada(
        idMedico: Long,
        idEspecialidade: Long,
        idLocal: Long?,
        tipoConsulta: TipoConsulta,
        valorConsulta: Double,
        aceitaParticular: Boolean,
        duracaoMinutos: Int,
        conveniosIds: Set<Long>
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val dto = ConsultaOfertadaCreateRequestDto(
                idEspecialidade = idEspecialidade,
                idLocal = idLocal,
                tipoConsulta = tipoConsulta,
                valorConsulta = valorConsulta,
                aceitaParticular = aceitaParticular,
                duracaoMinutos = duracaoMinutos,
                conveniosAceitosIds = conveniosIds
            )
            repository.createConsultaOfertada(idMedico, dto)
                .onSuccess { _uiState.value = UiState.Success }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Erro ao criar consulta") }
        }
    }

    fun deletarConsultaOfertada(idMedico: Long, idConsulta: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.deleteConsultaOfertada(idMedico, idConsulta)
                .onSuccess { carregarConsultas(idMedico) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Erro ao deletar") }
        }
    }

    fun resetState() { _uiState.value = UiState.Idle }
}