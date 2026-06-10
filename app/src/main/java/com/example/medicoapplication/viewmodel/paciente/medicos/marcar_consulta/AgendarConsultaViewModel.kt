package com.example.medicoapplication.viewmodel.paciente.medicos.marcar_consulta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicoapplication.data.remote.DTO.agenda.DisponibilidadeDiaDTO
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaResponseDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.data.repository.AgendaRepository
import com.example.medicoapplication.data.repository.ConsultaOfertadaRepository
import com.example.medicoapplication.data.repository.PacienteRepository
import com.example.medicoapplication.data.repository.toNetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AgendarConsultaViewModel(
    private val pacienteRepository: PacienteRepository = PacienteRepository(),
    private val consultaOfertadaRepository: ConsultaOfertadaRepository = ConsultaOfertadaRepository()
) : ViewModel() {

    // ─── Estados ─────────────────────────────────────────────────────────────

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()

        data class ConsultaOfertadaCarregada(
            val consultaOfertada: ConsultaOfertadaResponseDto
        ) : UiState()

        data class Error(
            val error: NetworkError
        ) : UiState()
    }

    sealed class DisponibilidadeState {
        object Idle        : DisponibilidadeState()
        object Loading     : DisponibilidadeState()
        object Vazio       : DisponibilidadeState()
        data class Success(val dias: List<DisponibilidadeDiaDTO>) : DisponibilidadeState()
        data class Error(val error: NetworkError) : DisponibilidadeState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _disponibilidadeState =
        MutableStateFlow<DisponibilidadeState>(DisponibilidadeState.Idle)
    val disponibilidadeState: StateFlow<DisponibilidadeState> =
        _disponibilidadeState.asStateFlow()

    // Cache completo: data (yyyy-MM-dd) → lista de horários "HH:mm"
    private val _disponibilidadePorDia = MutableStateFlow<Map<String, List<String>>>(emptyMap())

    // ─── Horários do dia selecionado (expostos à Activity) ───────────────────

    private val _horariosDoDia = MutableStateFlow<List<String>>(emptyList())
    val horariosDoDia: StateFlow<List<String>> = _horariosDoDia.asStateFlow()

    // ─── Ações ───────────────────────────────────────────────────────────────

    /** Carrega as informações do médico para exibição no header. */
    fun carregarConsultaOfertada(
        idMedico: Long,
        idConsultaOfertada: Long
    ) {
        viewModelScope.launch {

            _uiState.value = UiState.Loading

            consultaOfertadaRepository
                .getConsultaOfertada(
                    idMedico,
                    idConsultaOfertada
                )
                .onSuccess {

                    _uiState.value =
                        UiState.ConsultaOfertadaCarregada(it)
                }
                .onFailure {

                    _uiState.value =
                        UiState.Error(it.toNetworkError())
                }
        }
    }

    /**
     * Busca a disponibilidade real do médico para a consulta ofertada.
     * Usa o endpoint GET /medicos/{idMedico}/consultas-ofertadas/{idConsulta}/agenda/disponibilidade
     * e armazena o resultado num mapa data→horários para consulta local posterior.
     *
     * @param idMedico           ID do médico (vem via Intent da tela anterior)
     * @param idConsultaOfertada ID da consulta ofertada selecionada
     * @param semanas            Janela de semanas a buscar (padrão 4)
     */
    fun carregarDisponibilidade(
        idMedico: Long,
        idConsultaOfertada: Long,
        semanas: Int = 4
    ) {
        viewModelScope.launch {
            _disponibilidadeState.value = DisponibilidadeState.Loading

            // consultaOfertadaRepository.getDisponibilidade usa requireUserId() para o médico logado,
            // mas aqui somos um paciente consultando o médico de outro usuário — precisamos
            // chamar a API diretamente passando idMedico explicitamente.
            consultaOfertadaRepository.getDisponibilidadePublica(idMedico, idConsultaOfertada, semanas)
                .onSuccess { semanasDtos ->
                    // Achata List<DisponibilidadeSemanaDTO> → Map<String, List<String>>
                    val mapa = semanasDtos
                        .flatMap { it.dias }
                        .filter { it.horarios.isNotEmpty() }
                        .associate { dia -> dia.data to dia.horarios }

                    _disponibilidadePorDia.value = mapa

                    if (mapa.isEmpty()) {
                        _disponibilidadeState.value = DisponibilidadeState.Vazio
                    } else {
                        _disponibilidadeState.value = DisponibilidadeState.Success(
                            semanasDtos.flatMap { it.dias }
                        )
                    }
                }
                .onFailure {
                    _disponibilidadeState.value =
                        DisponibilidadeState.Error(it.toNetworkError())
                }
        }
    }

    /**
     * Atualiza [horariosDoDia] com os horários disponíveis para a data informada.
     * Chamado pela Activity sempre que o usuário muda o dia selecionado.
     *
     * @param data Data no formato "yyyy-MM-dd"
     */
    fun selecionarDia(data: String) {
        _horariosDoDia.value = _disponibilidadePorDia.value[data] ?: emptyList()
    }
}
