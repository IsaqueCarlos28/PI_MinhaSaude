package com.example.medicoapplication.UI.activities.paciente.consultas

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.viewmodel.paciente.consulta.ReagendarConsultaViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Tela de reagendamento. Recebe via Intent:
 *   - ID_EVENTO   (Long)  — id da consulta a ser reagendada
 *   - NOME_MEDICO (String) — exibição enquanto carrega
 */
class ReagendarConsultaActivity : BaseActivity() {

    private val viewModel: ReagendarConsultaViewModel by viewModels()

    private val calendar = Calendar.getInstance()
    private val formatoData   = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    private val formatoDataApi = SimpleDateFormat("yyyy-MM-dd", Locale("pt", "BR"))

    private var horarioSelecionado: String? = null
    private var idEvento: Long = -1L

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
        atualizarDataExibida()

        findViewById<ImageButton>(R.id.btnVoltarReagendar).setOnClickListener { finish() }

        findViewById<ImageButton>(R.id.btnDataAnteriorReagendar).setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            atualizarDataExibida()
        }
        findViewById<ImageButton>(R.id.btnProximaDataReagendar).setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            atualizarDataExibida()
        }

        val botoesHorario = listOf(
            Pair(R.id.btnReagHorario0900, "09:00"),
            Pair(R.id.btnReagHorario0930, "09:30"),
            Pair(R.id.btnReagHorario1000, "10:00"),
            Pair(R.id.btnReagHorario1030, "10:30"),
            Pair(R.id.btnReagHorario1100, "11:00"),
            Pair(R.id.btnReagHorario1130, "11:30"),
            Pair(R.id.btnReagHorario1200, "12:00"),
            Pair(R.id.btnReagHorario1230, "12:30")
        )
        botoesHorario.forEach { (idBotao, horario) ->
            findViewById<Button>(idBotao).setOnClickListener {
                selecionarHorario(idBotao, horario, botoesHorario.map { it.first })
            }
        }

        findViewById<Button>(R.id.btnConfirmarReagendamento).setOnClickListener { confirmarReagendamento() }

        observeViewModel()
        viewModel.carregarConsulta(idEvento)
        setupBottomNavigation(R.id.nav_consultas_paciente)
    }

    private fun atualizarDataExibida() {
        findViewById<TextView>(R.id.tvDataSelecionadaReagendar).text = formatoData.format(calendar.time)
    }

    private fun selecionarHorario(idSelecionado: Int, horario: String, idsHorarios: List<Int>) {
        horarioSelecionado = horario
        idsHorarios.forEach { id ->
            val btn = findViewById<Button>(id)
            btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E2E8F0"))
            btn.setTextColor(Color.parseColor("#1E293B"))
        }
        val btnSel = findViewById<Button>(idSelecionado)
        btnSel.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3B82F6"))
        btnSel.setTextColor(Color.WHITE)
    }

    private fun confirmarReagendamento() {
        if (horarioSelecionado == null) {
            showToast("Selecione um horário")
            return
        }
        viewModel.reagendar(
            idEvento   = idEvento,
            data       = formatoDataApi.format(calendar.time),
            horaInicio = horarioSelecionado!!
        )
    }

    private fun observeViewModel() {
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
    }

    private fun setLoading(carregando: Boolean) {
        val btn = findViewById<Button>(R.id.btnConfirmarReagendamento)
        btn.isEnabled = !carregando
        btn.text = if (carregando) "Reagendando..." else "Confirmar Reagendamento"
    }
}
