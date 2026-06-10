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

    private val calendar         = Calendar.getInstance()
    private val formatoApi       = SimpleDateFormat("yyyy-MM-dd", Locale("pt", "BR"))
    private val formatoExibicao  = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    private val formatoDiaSemana = SimpleDateFormat("EEE", Locale("pt", "BR"))

    private var horarioSelecionado: String? = null
    private var medicoId: Long = -1L
    private var idConsultaOfertada: Long = -1L

    override val menuType = BottomMenuType.PACIENTE

    private val idsBlocoDia = listOf(
        R.id.diaBloco0, R.id.diaBloco1, R.id.diaBloco2,
        R.id.diaBloco3, R.id.diaBloco4, R.id.diaBloco5, R.id.diaBloco6
    )
    private var diaOffset = 0

    private lateinit var containerHorarios: LinearLayout
    private lateinit var progressHorarios: ProgressBar
    private lateinit var tvHorariosVazio: TextView
    private lateinit var tvHorariosErro: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendar_consulta)

        // FIX: standardised to MEDICO_ID — removed the dual-key fallback.
        // All callers must pass MEDICO_ID. If you are migrating old code that
        // passes ID_MEDICO, update those call sites to use MEDICO_ID instead.
        medicoId           = intent.getLongExtra("MEDICO_ID", -1L)
        idConsultaOfertada = intent.getLongExtra("ID_CONSULTA_OFERTADA", -1L)

        val nomeExtra = intent.getStringExtra("NOME_MEDICO")
        if (!nomeExtra.isNullOrBlank()) {
            findViewById<TextView>(R.id.tvNomeMedicoAgendar).text = nomeExtra
        }

        containerHorarios = findViewById(R.id.containerHorariosAgendar)
        progressHorarios  = findViewById(R.id.progressHorariosAgendar)
        tvHorariosVazio   = findViewById(R.id.tvHorariosVazioAgendar)
        tvHorariosErro    = findViewById(R.id.tvHorariosErroAgendar)

        renderizarDias()
        vincularNavegacaoDias()

        findViewById<Button>(R.id.btnConfirmarConsulta).setOnClickListener { confirmarConsulta() }
        findViewById<TextView>(R.id.tvVerMaisHorarios).setOnClickListener { finish() }

        observeViewModel()

        if (
            medicoId != -1L &&
            idConsultaOfertada != -1L
        ) {
            viewModel.carregarConsultaOfertada(
                medicoId,
                idConsultaOfertada
            )
        }
        if (idConsultaOfertada != -1L) viewModel.carregarDisponibilidade(medicoId, idConsultaOfertada)

        setupBottomNavigation(R.id.nav_medicos_paciente)
    }

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
        horarioSelecionado = null
        viewModel.selecionarDia(formatoApi.format(calendar.time))
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

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is AgendarConsultaViewModel.UiState.ConsultaOfertadaCarregada -> {

                        val consulta = state.consultaOfertada

                        findViewById<TextView>(R.id.tvEspecialidadeAgendar).text =
                            consulta.especialidade?.nome ?: "-"

                        findViewById<TextView>(R.id.tvTipoConsultaAgendar).text =
                            consulta.tipoConsulta.name

                        findViewById<TextView>(R.id.tvValorConsultaAgendar).text =
                            "R$ ${consulta.valorConsulta}"

                        findViewById<TextView>(R.id.tvLocalConsultaAgendar).text =
                            consulta.local?.nome ?: "Local não informado"

                        findViewById<TextView>(R.id.tvNomeMedicoAgendar).text =
                            intent.getStringExtra("NOME_MEDICO") ?: "Médico"
                    }
                    else -> Unit
                }
            }
        }

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

        lifecycleScope.launch {
            viewModel.horariosDoDia.collect { horarios ->
                renderizarBotoesHorario(horarios)
            }
        }
    }

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
                    text           = horario
                    textSize       = 12f
                    isAllCaps      = false
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
                        // FIX: pass medicoId so ConfirmacaoConsultaActivity can load convenios
                        putExtra("ID_MEDICO",            medicoId)
                    }
                )
            }
            is ValidationResult.Error -> showToast(result.message)
        }
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density + 0.5f).toInt()
}
