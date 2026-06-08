package com.example.medicoapplication.UI.activities.medico.consultas

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.UI.common.mappers.ConsultaMapper
import com.example.medicoapplication.data.remote.DTO.StatusConsulta
import com.example.medicoapplication.viewmodel.medico.consulta.VisualisarConsultaMedicoViewModel
import kotlinx.coroutines.launch

/**
 * Exibe o detalhe completo de uma consulta do ponto de vista do médico.
 *
 * Recebe via Intent:
 *   - ID_EVENTO    (Long)   — obrigatório
 *   - NOME_PACIENTE (String) — opcional, usado como fallback antes da resposta da API
 */
class VisualisarConsultaMedico : BaseActivity() {

    private val viewModel: VisualisarConsultaMedicoViewModel by viewModels()

    override val menuType = BottomMenuType.MEDICO

    private var idEvento: Long = -1L

    // — views
    private lateinit var tvAvatarPaciente: TextView
    private lateinit var tvNomePaciente: TextView
    private lateinit var tvData: TextView
    private lateinit var tvHoraInicio: TextView
    private lateinit var tvHoraFim: TextView
    private lateinit var tvConvenio: TextView
    private lateinit var tvStatus: TextView
    private lateinit var layoutAcoes: LinearLayout
    private lateinit var btnRealizada: Button
    private lateinit var btnCancelar: Button
    private lateinit var layoutLoading: LinearLayout
    private lateinit var layoutConteudo: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualisar_consulta_medico)

        idEvento = intent.getLongExtra("ID_EVENTO", -1L)
        val nomeFallback = intent.getStringExtra("NOME_PACIENTE") ?: "Paciente"

        bindViews()

        // Preenche nome e avatar com fallback enquanto a requisição não volta
        preencherNomePaciente(nomeFallback)

        findViewById<ImageButton>(R.id.btnVoltarVisualisar).setOnClickListener { finish() }

        observeUiState()
        observeAcaoState()

        if (idEvento != -1L) {
            viewModel.carregarConsulta(idEvento)
        } else {
            showToast("Consulta não encontrada.")
            finish()
        }

        setupBottomNavigation(R.id.nav_consultas_medico)
    }

    // -----------------------------------------------------------------------
    // Bind
    // -----------------------------------------------------------------------
    private fun bindViews() {
        tvAvatarPaciente = findViewById(R.id.tvAvatarPaciente)
        tvNomePaciente   = findViewById(R.id.tvVisPaciente)
        tvData           = findViewById(R.id.tvVisData)
        tvHoraInicio     = findViewById(R.id.tvVisHoraInicio)
        tvHoraFim        = findViewById(R.id.tvVisHoraFim)
        tvConvenio       = findViewById(R.id.tvVisConvenio)
        tvStatus         = findViewById(R.id.tvVisStatus)
        layoutAcoes      = findViewById(R.id.layoutAcoes)
        btnRealizada     = findViewById(R.id.btnMarcarRealizada)
        btnCancelar      = findViewById(R.id.btnCancelarConsultaMedico)
        layoutLoading    = findViewById(R.id.layoutLoadingVisualisar)
        layoutConteudo   = findViewById(R.id.layoutConteudoVisualisar)
    }

    // -----------------------------------------------------------------------
    // Helpers de preenchimento
    // -----------------------------------------------------------------------

    /** Exibe o nome e deriva a inicial para o avatar circular. */
    private fun preencherNomePaciente(nome: String) {
        tvNomePaciente.text = nome
        tvAvatarPaciente.text = nome
            .trim()
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercaseChar().toString() }
            .ifBlank { "?" }
    }

    /** Aplica cor de fundo e texto ao badge de status. */
    private fun aplicarEstiloStatus(statusEnum: StatusConsulta) {
        val (bg, fg) = when (statusEnum) {
            StatusConsulta.AGENDADA  -> 0xFFDCFCE7.toInt() to 0xFF166534.toInt()
            StatusConsulta.REALIZADA -> 0xFFDBEAFE.toInt() to 0xFF1E3A8A.toInt()
            StatusConsulta.CANCELADA -> 0xFFFEE2E2.toInt() to 0xFF991B1B.toInt()
        }
        tvStatus.setBackgroundColor(bg)
        tvStatus.setTextColor(fg)
    }

    // -----------------------------------------------------------------------
    // Observadores
    // -----------------------------------------------------------------------
    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is VisualisarConsultaMedicoViewModel.UiState.Idle -> {
                            setLoading(false)
                        }
                        is VisualisarConsultaMedicoViewModel.UiState.Loading -> {
                            setLoading(true)
                        }
                        is VisualisarConsultaMedicoViewModel.UiState.Error -> {
                            setLoading(false)
                            handleError(state.error)
                        }
                        is VisualisarConsultaMedicoViewModel.UiState.Success -> {
                            setLoading(false)

                            val consulta = state.consulta
                            val display  = ConsultaMapper.toDisplay(consulta)

                            // Paciente + avatar
                            preencherNomePaciente(display.nomePaciente)

                            // Data / horários — agora todos vindos do mapper
                            tvData.text       = display.data
                            tvHoraInicio.text = display.horaInicio
                            tvHoraFim.text    = display.horaFim   // corrigido: vem do mapper

                            // Convênio e status
                            tvConvenio.text = display.convenio
                            tvStatus.text   = display.status
                            aplicarEstiloStatus(display.statusEnum)

                            // Botões de ação só aparecem para consultas AGENDADAS
                            layoutAcoes.visibility =
                                if (display.statusEnum == StatusConsulta.AGENDADA) View.VISIBLE
                                else View.GONE

                            btnRealizada.setOnClickListener {
                                confirmarAcao(
                                    titulo   = "Marcar como realizada",
                                    mensagem = "Confirma que a consulta com ${display.nomePaciente} foi realizada?",
                                    acao     = { viewModel.marcarComoRealizada(idEvento) }
                                )
                            }

                            btnCancelar.setOnClickListener {
                                confirmarAcao(
                                    titulo   = "Cancelar consulta",
                                    mensagem = "Tem certeza que deseja cancelar a consulta com ${display.nomePaciente}?",
                                    acao     = { viewModel.cancelarConsulta(idEvento) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun observeAcaoState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.acaoState.collect { state ->
                    when (state) {
                        is VisualisarConsultaMedicoViewModel.AcaoState.Idle    -> setBotoesBloqueados(false)
                        is VisualisarConsultaMedicoViewModel.AcaoState.Loading -> setBotoesBloqueados(true)
                        is VisualisarConsultaMedicoViewModel.AcaoState.Error   -> {
                            setBotoesBloqueados(false)
                            handleError(state.error)
                            viewModel.resetAcaoState()
                        }
                        is VisualisarConsultaMedicoViewModel.AcaoState.Sucesso -> {
                            showToast("Consulta atualizada com sucesso.")
                            finish()
                        }
                    }
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------
    private fun setLoading(isLoading: Boolean) {
        layoutLoading.visibility  = if (isLoading) View.VISIBLE else View.GONE
        layoutConteudo.visibility = if (isLoading) View.GONE    else View.VISIBLE
    }

    private fun setBotoesBloqueados(bloqueado: Boolean) {
        btnRealizada.isEnabled = !bloqueado
        btnCancelar.isEnabled  = !bloqueado
    }

    private fun confirmarAcao(titulo: String, mensagem: String, acao: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensagem)
            .setPositiveButton("Confirmar") { _, _ -> acao() }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}