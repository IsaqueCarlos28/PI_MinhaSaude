package com.example.medicoapplication.UI.activities.paciente

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
import com.example.medicoapplication.activities.paciente.viewmodel.ReagendarConsultaViewModel
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaUpdateRequestDto
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Tela de reagendamento. Recebe via Intent:
 *   - ID_PACIENTE (Long)
 *   - ID_EVENTO   (Long)  — id da consulta a ser reagendada
 *   - ID_MEDICO   (Long)
 *   - NOME_MEDICO (String)
 */
class ReagendarConsultaActivity : BaseActivity() {

    private val viewModel: ReagendarConsultaViewModel by viewModels()

    private val calendar = Calendar.getInstance()
    private val formatoData = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    private val formatoDataApi = SimpleDateFormat("yyyy-MM-dd", Locale("pt", "BR"))

    private var horarioSelecionado: String? = null
    private var idPaciente: Long = -1L
    private var idEvento: Long   = -1L
    private var idMedico: Long   = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reagendar_consulta)

        idPaciente = intent.getLongExtra("ID_PACIENTE", -1L)
        idEvento   = intent.getLongExtra("ID_EVENTO",   -1L)
        idMedico   = intent.getLongExtra("ID_MEDICO",   -1L)
        val nomeMedico = intent.getStringExtra("NOME_MEDICO") ?: "Médico"

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
        configurarBottomNav(R.id.nav_consultas)
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
            showToast( "Selecione um horário")
            return
        }

        viewModel.reagendar(
            idPaciente,
            idEvento,
            dto = ConsultaUpdateRequestDto(
                idMedico,
                null,
                formatoDataApi.format(calendar.time),
                horarioSelecionado!!)
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
                                putExtra("ID_PACIENTE", idPaciente)
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

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavReagendar)
        bottomNav.selectedItemId = itemSelecionado
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home      -> { startActivity(Intent(this, HomePacienteActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                R.id.nav_consultas -> { startActivity(Intent(this, MinhasConsultasActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                R.id.nav_medicos   -> { startActivity(Intent(this, BuscaMedicosActivity::class.java)); false }
                R.id.nav_perfil    -> { startActivity(Intent(this, PerfilPacienteActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                else -> false
            }
        }
    }
}
