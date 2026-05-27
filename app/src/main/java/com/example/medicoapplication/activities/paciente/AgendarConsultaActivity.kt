package com.example.medicoapplication.activities.paciente

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.activities.paciente.viewmodel.AgendarConsultaViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AgendarConsultaActivity : AppCompatActivity() {

    private val viewModel: AgendarConsultaViewModel by viewModels()

    private val calendar = Calendar.getInstance()
    private val formatoData = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    private var horarioSelecionado: String? = null
    private var medicoId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendar_consulta)

        medicoId = intent.getLongExtra("ID_MEDICO", intent.getLongExtra("MEDICO_ID", -1L))

        val nomeExtra = intent.getStringExtra("NOME_MEDICO")
        if (!nomeExtra.isNullOrBlank()) {
            findViewById<TextView>(R.id.tvNomeMedicoAgendar).text = nomeExtra
        }

        findViewById<ImageButton>(R.id.btnDataAnterior).setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }
        findViewById<ImageButton>(R.id.btnProximaData).setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val botoesHorario = listOf(
            Pair(R.id.btnHorario0900, "09:00"),
            Pair(R.id.btnHorario0930, "09:30"),
            Pair(R.id.btnHorario1000, "10:00"),
            Pair(R.id.btnHorario1030, "10:30"),
            Pair(R.id.btnHorario1100, "11:00"),
            Pair(R.id.btnHorario1130, "11:30"),
            Pair(R.id.btnHorario1200, "12:00"),
            Pair(R.id.btnHorario1230, "12:30")
        )
        botoesHorario.forEach { (idBotao, horario) ->
            findViewById<Button>(idBotao).setOnClickListener {
                selecionarHorario(idBotao, horario, botoesHorario.map { it.first })
            }
        }

        findViewById<Button>(R.id.btnConfirmarConsulta).setOnClickListener { confirmarConsulta() }
        findViewById<TextView>(R.id.tvVerMaisHorarios).setOnClickListener {
            Toast.makeText(this, "Mais horários em breve", Toast.LENGTH_SHORT).show()
        }

        observeViewModel()

        if (medicoId != -1L) viewModel.carregarMedico(medicoId)

        configurarBottomNav(R.id.nav_consultas)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is AgendarConsultaViewModel.UiState.Idle    -> Unit
                    is AgendarConsultaViewModel.UiState.Loading -> Unit
                    is AgendarConsultaViewModel.UiState.Error   -> Unit
                    is AgendarConsultaViewModel.UiState.MedicoCarregado -> {
                        val medico = state.medico
                        findViewById<TextView>(R.id.tvNomeMedicoAgendar).text =
                            medico.usuario?.nome ?: "Médico"
                        findViewById<TextView>(R.id.tvEspecialidadeAgendar).text =
                            medico.especialidades.firstOrNull()?.especialidade?.nome ?: ""
                        findViewById<TextView>(R.id.tvCrmAgendar).text =
                            "CRM: ${medico.crmDigitos ?: ""}/${medico.crmUf ?: ""}"
                    }
                }
            }
        }
    }

    private fun selecionarHorario(idSelecionado: Int, horario: String, idsHorarios: List<Int>) {
        horarioSelecionado = horario
        idsHorarios.forEach { id ->
            val btn = findViewById<Button>(id)
            btn.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#E2E8F0"))
            btn.setTextColor(Color.parseColor("#1E293B"))
        }
        val btnSel = findViewById<Button>(idSelecionado)
        btnSel.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#3B82F6"))
        btnSel.setTextColor(Color.WHITE)
    }

    private fun confirmarConsulta() {
        if (horarioSelecionado == null) {
            Toast.makeText(this, "Selecione um horário", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(
            this,
            "Consulta agendada para ${formatoData.format(calendar.time)} às $horarioSelecionado",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavAgendar)
        bottomNav.selectedItemId = itemSelecionado
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home      -> { startActivity(Intent(this, HomePacienteActivity::class.java)); false }
                R.id.nav_consultas -> { startActivity(Intent(this, MinhasConsultasActivity::class.java)); false }
                R.id.nav_medicos   -> { startActivity(Intent(this, BuscaMedicosActivity::class.java)); false }
                R.id.nav_perfil    -> { startActivity(Intent(this, PerfilPacienteActivity::class.java)); false }
                else -> false
            }
        }
    }
}