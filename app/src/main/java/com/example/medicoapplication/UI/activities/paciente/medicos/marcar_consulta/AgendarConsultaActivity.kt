package com.example.medicoapplication.UI.activities.paciente.medicos

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.UI.common.mappers.MedicoMapper
import com.example.medicoapplication.UI.common.validations.ConsultaValidator
import com.example.medicoapplication.UI.common.validations.ValidationResult
import com.example.medicoapplication.viewmodel.paciente.medicos.marcar_consulta.AgendarConsultaViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AgendarConsultaActivity : BaseActivity() {

    private val viewModel: AgendarConsultaViewModel by viewModels()

    // ─── Calendário e formatadores ────────────────────────────────────────────
    private val calendar        = Calendar.getInstance()
    private val formatoApi       = SimpleDateFormat("yyyy-MM-dd", Locale("pt", "BR"))
    private val formatoExibicao  = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    private val formatoDiaSemana = SimpleDateFormat("EEE", Locale("pt", "BR"))

    // ─── Estado de seleção ────────────────────────────────────────────────────
    private var horarioSelecionado: String? = null
    private var medicoId: Long = -1L
    private var idConsultaOfertada: Long = -1L

    override val menuType = BottomMenuType.PACIENTE

    // ─── IDs dos blocos de dia na barra de seleção ────────────────────────────
    private val idsBlocoDia = listOf(
        R.id.diaBloco0, R.id.diaBloco1, R.id.diaBloco2,
        R.id.diaBloco3, R.id.diaBloco4, R.id.diaBloco5, R.id.diaBloco6
    )
    private var diaOffset = 0

    // ─── Views de horários (criadas dinamicamente) ────────────────────────────
    private lateinit var containerHorarios: LinearLayout
    private lateinit var progressHorarios: ProgressBar
    private lateinit var tvHorariosVazio: TextView
    private lateinit var tvHorariosErro: TextView

    // ─── onCreate ─────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendar_consulta)

        medicoId           = intent.getLongExtra("ID_MEDICO", intent.getLongExtra("MEDICO_ID", -1L))
        idConsultaOfertada = intent.getLongExtra("ID_CONSULTA_OFERTADA", -1L)

        val nomeExtra = intent.getStringExtra("NOME_MEDICO")
        if (!nomeExtra.isNullOrBlank()) {
            findViewById<TextView>(R.id.tvNomeMedicoAgendar).text = nomeExtra
        }

        // Vincula as views de estado dos horários
        containerHorarios = findViewById(R.id.containerHorariosAgendar)
        progressHorarios  = findViewById(R.id.progressHorariosAgendar)
        tvHorariosVazio   = findViewById(R.id.tvHorariosVazioAgendar)
        tvHorariosErro    = findViewById(R.id.tvHorariosErroAgendar)

        renderizarDias()
        vincularNavegacaoDias()

        findViewById<Button>(R.id.btnConfirmarConsulta).setOnClickListener { confirmarConsulta() }

        // "Voltar a agendar" vira simples finish() para voltar ao perfil do médico
        findViewById<TextView>(R.id.tvVerMaisHorarios).setOnClickListener { finish() }

        observeViewModel()

        if (medicoId != -1L)           viewModel.carregarMedico(medicoId)
        if (idConsultaOfertada != -1L) viewModel.carregarDisponibilidade(medicoId, idConsultaOfertada)

        setupBottomNavigation(R.id.nav_medicos_paciente)
    }

    // ─── Navegação entre dias ─────────────────────────────────────────────────

    private fun vincularNavegacaoDias() {
        findViewById<ImageButton>(R.id.btnDataAnterior).setOnClickListener {
            if (diaOffset > 0) {
                diaOffset--
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                renderizarDias()
                notificarDiaSelecionado()
            }
        }
        findViewById<ImageButton>(R.id.btnProximaData).setOnClickListener {
            diaOffset++
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            renderizarDias()
            notificarDiaSelecionado()
        }
    }

    private fun notificarDiaSelecionado() {
        horarioSelecionado = null                          // limpa a seleção anterior
        viewModel.selecionarDia(formatoApi.format(calendar.time))
    }

    // ─── Renderização dos blocos de dia ───────────────────────────────────────

    private fun renderizarDias() {
        val base = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, diaOffset - 3) }
        idsBlocoDia.forEachIndexed { index, idBloco ->
            val bloco = try { findViewById<LinearLayout>(idBloco) } catch (e: Exception) { null }
                ?: return@forEachIndexed
            val cal = base.clone() as Calendar
            cal.add(Calendar.DAY_OF_MONTH, index)
            val tvSemana = bloco.getChildAt(0) as? TextView
            val tvDia    = bloco.getChildAt(1) as? TextView
            tvSemana?.text = formatoDiaSemana.format(cal.time).uppercase()
            tvDia?.text    = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH))
            val isSelecionado = index == 3
            if (isSelecionado) {
                bloco.setBackgroundColor(Color.parseColor("#3B82F6"))
                tvSemana?.setTextColor(Color.WHITE); tvDia?.setTextColor(Color.WHITE)
            } else {
                bloco.setBackgroundColor(Color.TRANSPARENT)
                tvSemana?.setTextColor(Color.parseColor("#64748B"))
                tvDia?.setTextColor(Color.parseColor("#64748B"))
            }
        }
    }

    // ─── Observers ───────────────────────────────────────────────────────────

    private fun observeViewModel() {
        // Dados do médico (header)
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is AgendarConsultaViewModel.UiState.MedicoCarregado -> {
                        val info = MedicoMapper.toAgendamentoInfo(state.medico)
                        findViewById<TextView>(R.id.tvNomeMedicoAgendar).text    = info.nome
                        findViewById<TextView>(R.id.tvEspecialidadeAgendar).text = info.especialidade
                        findViewById<TextView>(R.id.tvCrmAgendar).text           = info.crm
                    }
                    else -> Unit
                }
            }
        }

        // Estado geral da disponibilidade (loading / erro / vazio)
        lifecycleScope.launch {
            viewModel.disponibilidadeState.collect { state ->
                when (state) {
                    is AgendarConsultaViewModel.DisponibilidadeState.Idle    -> Unit
                    is AgendarConsultaViewModel.DisponibilidadeState.Loading -> {
                        progressHorarios.visibility  = View.VISIBLE
                        tvHorariosVazio.visibility   = View.GONE
                        tvHorariosErro.visibility    = View.GONE
                        containerHorarios.visibility = View.GONE
                    }
                    is AgendarConsultaViewModel.DisponibilidadeState.Success -> {
                        progressHorarios.visibility  = View.GONE
                        tvHorariosVazio.visibility   = View.GONE
                        tvHorariosErro.visibility    = View.GONE
                        containerHorarios.visibility = View.VISIBLE
                        // Dispara a filtragem para o dia já selecionado
                        viewModel.selecionarDia(formatoApi.format(calendar.time))
                    }
                    is AgendarConsultaViewModel.DisponibilidadeState.Vazio  -> {
                        progressHorarios.visibility  = View.GONE
                        tvHorariosVazio.visibility   = View.VISIBLE
                        tvHorariosErro.visibility    = View.GONE
                        containerHorarios.visibility = View.GONE
                    }
                    is AgendarConsultaViewModel.DisponibilidadeState.Error  -> {
                        progressHorarios.visibility  = View.GONE
                        tvHorariosVazio.visibility   = View.GONE
                        tvHorariosErro.visibility    = View.VISIBLE
                        containerHorarios.visibility = View.GONE
                        handleError(state.error)
                    }
                }
            }
        }

        // Horários do dia selecionado → renderiza botões dinamicamente
        lifecycleScope.launch {
            viewModel.horariosDoDia.collect { horarios ->
                renderizarBotoesHorario(horarios)
            }
        }
    }

    // ─── Renderização dinâmica dos botões de horário ─────────────────────────

    /**
     * Limpa o container e gera um botão por horário disponível retornado pela API.
     * Os botões são agrupados em linhas de 4 para manter o mesmo visual do layout original.
     */
    private fun renderizarBotoesHorario(horarios: List<String>) {
        containerHorarios.removeAllViews()
        horarioSelecionado = null

        if (horarios.isEmpty()) {
            tvHorariosVazio.text       = "Nenhum horário disponível para esta data."
            tvHorariosVazio.visibility = View.VISIBLE
            containerHorarios.visibility = View.GONE
            return
        }

        tvHorariosVazio.visibility   = View.GONE
        containerHorarios.visibility = View.VISIBLE

        // Agrupa em linhas de 4 colunas
        val linhaSize = 4
        horarios.chunked(linhaSize).forEach { grupo ->
            val linha = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.bottomMargin = dpToPx(8) }
            }

            grupo.forEach { horario ->
                val botao = Button(this).apply {
                    text           = horario
                    textSize       = 12f
                    isAllCaps      = false
                    setTextColor(Color.parseColor("#1E293B"))
                    backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E2E8F0"))
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        dpToPx(38),
                        1f
                    ).also { it.marginEnd = dpToPx(6) }

                    setOnClickListener { selecionarHorario(this, horario) }
                }
                linha.addView(botao)
            }

            // Preenche espaços vazios na última linha para manter alinhamento
            val faltando = linhaSize - grupo.size
            repeat(faltando) {
                val espacador = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(0, dpToPx(38), 1f)
                        .also { it.marginEnd = dpToPx(6) }
                }
                linha.addView(espacador)
            }

            containerHorarios.addView(linha)
        }
    }

    // ─── Seleção de horário ───────────────────────────────────────────────────

    private fun selecionarHorario(botaoSelecionado: Button, horario: String) {
        horarioSelecionado = horario

        // Reseta todos os botões no container
        for (i in 0 until containerHorarios.childCount) {
            val linha = containerHorarios.getChildAt(i) as? LinearLayout ?: continue
            for (j in 0 until linha.childCount) {
                (linha.getChildAt(j) as? Button)?.apply {
                    backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E2E8F0"))
                    setTextColor(Color.parseColor("#1E293B"))
                }
            }
        }

        // Destaca o botão selecionado
        botaoSelecionado.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3B82F6"))
        botaoSelecionado.setTextColor(Color.WHITE)
    }

    // ─── Confirmação ─────────────────────────────────────────────────────────

    private fun confirmarConsulta() {
        val dataApi = formatoApi.format(calendar.time)

        when (val result = ConsultaValidator.validarAgendamento(dataApi, horarioSelecionado)) {
            is ValidationResult.Success -> {
                startActivity(
                    Intent(this, ConfirmacaoConsultaActivity::class.java).apply {
                        putExtra("NOME_MEDICO",          findViewById<TextView>(R.id.tvNomeMedicoAgendar).text.toString())
                        putExtra("ESPECIALIDADE",        findViewById<TextView>(R.id.tvEspecialidadeAgendar).text.toString())
                        putExtra("DATA_CONSULTA",        dataApi)
                        putExtra("HORA_CONSULTA",        horarioSelecionado!!)
                        putExtra("ID_CONSULTA_OFERTADA", idConsultaOfertada)
                    }
                )
            }
            is ValidationResult.Error -> showToast(result.message)
        }
    }

    // ─── Utilitário ──────────────────────────────────────────────────────────

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density + 0.5f).toInt()
}
