package com.example.medicoapplication.viewmodel.medico.consulta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaResponseDto
import com.example.medicoapplication.data.remote.DTO.consultaofertada.TipoConsulta
import com.example.medicoapplication.data.remote.DTO.convenio.ConvenioResponseDto
import com.example.medicoapplication.data.remote.DTO.local.LocalResponseDto
import com.example.medicoapplication.data.remote.DTO.medicoespecialidade.MedicoEspecialidadeResponseDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.ConsultaOfertadaRepository
import com.example.medicoapplication.data.repository.ConvenioRepository
import com.example.medicoapplication.data.repository.LocalRepository
import com.example.medicoapplication.data.repository.MedicoEspecialidadeRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConsultaOfertadaViewModel(
    private val repository: ConsultaOfertadaRepository = ConsultaOfertadaRepository(),
    private val medicoEspecialidadeRepo: MedicoEspecialidadeRepository = MedicoEspecialidadeRepository(),
    private val localRepo: LocalRepository = LocalRepository(),
    private val convenioRepo: ConvenioRepository = ConvenioRepository()
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Success : UiState()
        // Após criar consulta ofertada, carrega o id para navegar para SetarAgenda
        data class SuccessComId(val idConsultaOfertada: Long) : UiState()
        data class Error(val error: NetworkError) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _consultas = MutableStateFlow<List<ConsultaOfertadaResponseDto>>(emptyList())
    val consultas: StateFlow<List<ConsultaOfertadaResponseDto>> = _consultas.asStateFlow()

    // Especialidades do médico logado (MedicoEspecialidade, não todas as especialidades do sistema)
    private val _minhasEspecialidades = MutableStateFlow<List<MedicoEspecialidadeResponseDto>>(emptyList())
    val minhasEspecialidades: StateFlow<List<MedicoEspecialidadeResponseDto>> = _minhasEspecialidades.asStateFlow()

    private val _locais = MutableStateFlow<List<LocalResponseDto>>(emptyList())
    val locais: StateFlow<List<LocalResponseDto>> = _locais.asStateFlow()

    private val _convenios = MutableStateFlow<List<ConvenioResponseDto>>(emptyList())
    val convenios: StateFlow<List<ConvenioResponseDto>> = _convenios.asStateFlow()

    fun carregarConsultasOfertadas() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getMinhaConsultasOfertadas()
                .onSuccess { _consultas.value = it; _uiState.value = UiState.Idle }
                .onFailure { _uiState.value = UiState.Error(it.toNetworkError()) }
        }
    }

    fun carregarDadosFormulario() {
        viewModelScope.launch {
            // Carrega apenas as especialidades que o médico possui vínculo
            medicoEspecialidadeRepo.getMinhasEspecialidades()
                .onSuccess { _minhasEspecialidades.value = it }
                .onFailure { /* silencia — o spinner ficará vazio */ }

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
            repository.createConsultaOfertada(dto)
                .onSuccess { criada ->
                    _uiState.value = UiState.SuccessComId(criada.id)
                }
                .onFailure { _uiState.value = UiState.Error(it.toNetworkError()) }
        }
    }

    fun deletarConsultaOfertada(idConsulta: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.deleteConsultaOfertada(idConsulta)
                .onSuccess { carregarConsultasOfertadas() }
                .onFailure { _uiState.value = UiState.Error(it.toNetworkError()) }
        }
    }

    fun resetState() { _uiState.value = UiState.Idle }
}
