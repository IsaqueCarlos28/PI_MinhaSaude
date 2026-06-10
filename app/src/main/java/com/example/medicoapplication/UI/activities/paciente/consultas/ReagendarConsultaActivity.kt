package com.example.medicoapplication.UI.activities.paciente.consultas

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
import com.example.medicoapplication.UI.common.validations.ConsultaValidator
import com.example.medicoapplication.UI.common.validations.ValidationResult
import com.example.medicoapplication.viewmodel.paciente.consulta.ReagendarConsultaViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Reschedule screen. Receives via Intent:
 *   - ID_EVENTO   (Long)  — id of the consulta to reschedule
 *   - NOME_MEDICO (String) — shown while the consulta loads
 *
 * FIX: previously showed 8 hardcoded time slots (09:00–12:30).
 * Now loads real availability from the API via ReagendarConsultaViewModel,
 * following the same pattern as AgendarConsultaActivity.
 */
class ReagendarConsultaActivity : BaseActivity() {

    private val viewModel: ReagendarConsultaViewModel by viewModels()

    private val calendar      = Calendar.getInstance()
    private val formatoData   = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    private val formatoDataApi = SimpleDateFormat("yyyy-MM-dd", Locale("pt", "BR"))
    private val formatoDiaSemana = SimpleDateFormat("EEE", Locale("pt", "BR"))

    private var horarioSelecionado: String? = null
    private var idEvento: Long = -1L

    // ─── Day-strip IDs (same 7-slot strip used in AgendarConsultaActivity) ──
    private val idsBlocoDia = listOf(
        R.id.diaBloco0Reagendar, R.id.diaBloco1Reagendar, R.id.diaBloco2Reagendar,
        R.id.diaBloco3Reagendar, R.id.diaBloco4Reagendar, R.id.diaBloco5Reagendar,
        R.id.diaBloco6Reagendar
    )
    private var diaOffset = 0

    private lateinit var containerHorarios: LinearLayout
    private lateinit var progressHorarios: ProgressBar
    private lateinit var tvHorariosVazio: TextView
    private lateinit var tvHorariosErro: TextView
    private lateinit var btnConfirmar: Button

    override val menuType = BottomMenuType.PACIENTE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reagendar_consulta)

        idEvento = intent.getLongExtra("ID_EVENTO", -1L)
        val nomeMedico = intent.getStringExtra("NOME_MEDICO") ?: "Médico"

        if (idEvento == -1L) {
            showToast("Consulta não encontrada.")
            finish()
            return
        }

        findViewById<TextView>(R.id.tvNomeMedicoReagendar).text = nomeMedico

        containerHorarios = findViewById(R.id.containerHorariosReagendar)
        progressHorarios  = findViewById(R.id.progressHorariosReagendar)
        tvHorariosVazio   = findViewById(R.id.tvHorariosVazioReagendar)
        tvHorariosErro    = findViewById(R.id.tvHorariosErroReagendar)
        btnConfirmar      = findViewById(R.id.btnConfirmarReagendamento)

        renderizarDias()
        vincularNavegacaoDias()

        findViewById<ImageButton>(R.id.btnVoltarReagendar).setOnClickListener { finish() }
        btnConfirmar.setOnClickListener { confirmarReagendamento() }

        observeViewModel()
        viewModel.carregarConsultaEDisponibilidade(idEvento)

        setupBottomNavigation(R.id.nav_consultas_paciente)
    }

    // ─── Day strip ────────────────────────────────────────────────────────────

    private fun vincularNavegacaoDias() {
        findViewById<ImageButton>(R.id.btnDataAnteriorReagendar).setOnClickListener {
            if (diaOffset > 0) {
                diaOffset--
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                renderizarDias()
                notificarDiaSelecionado()
            }
        }
        findViewById<ImageButton>(R.id.btnProximaDataReagendar).setOnClickListener {
            diaOffset++
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            renderizarDias()
            notificarDiaSelecionado()
        }
    }

    private fun notificarDiaSelecionado() {
        horarioSelecionado = null
        viewModel.selecionarDia(formatoDataApi.format(calendar.time))
    }

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

    // ─── Observers ────────────────────────────────────────────────────────────

    private fun observeViewModel() {

        // General loading / success / error (consulta load + reagendar submit)
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ReagendarConsultaViewModel.UiState.Idle    -> setLoading(false)
                    is ReagendarConsultaViewModel.UiState.Loading -> setLoading(true)
                    is ReagendarConsultaViewModel.UiState.Error   -> {
                        setLoading(false)
                        handleError(state.error)
                    }
                    is ReagendarConsultaViewModel.UiState.Sucesso -> {
                        setLoading(false)
                        showToast("Consulta reagendada com sucesso!")
                        startActivity(
                            Intent(this@ReagendarConsultaActivity, MinhasConsultasActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            }
                        )
                        finish()
                    }
                }
            }
        }

        // Availability panel state
        lifecycleScope.launch {
            viewModel.disponibilidadeState.collect { state ->
                when (state) {
                    is ReagendarConsultaViewModel.DisponibilidadeState.Idle    -> Unit
                    is ReagendarConsultaViewModel.DisponibilidadeState.Loading -> {
                        progressHorarios.visibility  = View.VISIBLE
                        tvHorariosVazio.visibility   = View.GONE
                        tvHorariosErro.visibility    = View.GONE
                        containerHorarios.visibility = View.GONE
                        btnConfirmar.isEnabled = false
                    }
                    is ReagendarConsultaViewModel.DisponibilidadeState.Success -> {
                        progressHorarios.visibility  = View.GONE
                        tvHorariosVazio.visibility   = View.GONE
                        tvHorariosErro.visibility    = View.GONE
                        containerHorarios.visibility = View.VISIBLE
                        btnConfirmar.isEnabled = true
                        // Trigger filter for today
                        viewModel.selecionarDia(formatoDataApi.format(calendar.time))
                    }
                    is ReagendarConsultaViewModel.DisponibilidadeState.Vazio  -> {
                        progressHorarios.visibility  = View.GONE
                        tvHorariosVazio.visibility   = View.VISIBLE
                        tvHorariosErro.visibility    = View.GONE
                        containerHorarios.visibility = View.GONE
                        btnConfirmar.isEnabled = false
                    }
                    is ReagendarConsultaViewModel.DisponibilidadeState.Error  -> {
                        progressHorarios.visibility  = View.GONE
                        tvHorariosVazio.visibility   = View.GONE
                        tvHorariosErro.visibility    = View.VISIBLE
                        containerHorarios.visibility = View.GONE
                        btnConfirmar.isEnabled = false
                        handleError(state.error)
                    }
                }
            }
        }

        // Slots for the selected day → render buttons dynamically
        lifecycleScope.launch {
            viewModel.horariosDoDia.collect { horarios ->
                renderizarBotoesHorario(horarios)
            }
        }
    }

    // ─── Dynamic slot buttons (same logic as AgendarConsultaActivity) ─────────

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
                    text      = horario
                    textSize  = 12f
                    isAllCaps = false
                    setTextColor(Color.parseColor("#1E293B"))
                    backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E2E8F0"))
                    layoutParams = LinearLayout.LayoutParams(0, dpToPx(38), 1f)
                        .also { it.marginEnd = dpToPx(6) }
                    setOnClickListener { selecionarHorario(this, horario) }
                }
                linha.addView(botao)
            }

            val faltando = linhaSize - grupo.size
            repeat(faltando) {
                linha.addView(View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(0, dpToPx(38), 1f)
                        .also { it.marginEnd = dpToPx(6) }
                })
            }

            containerHorarios.addView(linha)
        }
    }

    private fun selecionarHorario(botaoSelecionado: Button, horario: String) {
        horarioSelecionado = horario

        for (i in 0 until containerHorarios.childCount) {
            val linha = containerHorarios.getChildAt(i) as? LinearLayout ?: continue
            for (j in 0 until linha.childCount) {
                (linha.getChildAt(j) as? Button)?.apply {
                    backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E2E8F0"))
                    setTextColor(Color.parseColor("#1E293B"))
                }
            }
        }

        botaoSelecionado.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3B82F6"))
        botaoSelecionado.setTextColor(Color.WHITE)
    }

    // ─── Confirm ──────────────────────────────────────────────────────────────

    private fun confirmarReagendamento() {
        val dataApi = formatoDataApi.format(calendar.time)

        when (val result = ConsultaValidator.validarAgendamento(dataApi, horarioSelecionado)) {
            is ValidationResult.Success ->
                viewModel.reagendar(
                    idEvento   = idEvento,
                    data       = dataApi,
                    horaInicio = horarioSelecionado!!
                )
            is ValidationResult.Error -> showToast(result.message)
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private fun setLoading(carregando: Boolean) {
        btnConfirmar.text = if (carregando) "Reagendando..." else "Confirmar Reagendamento"
        // Keep isEnabled tied to availability state, not loading state,
        // so the button re-enables automatically once the API call completes.
        if (carregando) btnConfirmar.isEnabled = false
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density + 0.5f).toInt()
}
