package com.example.medicoapplication.activities.paciente

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.viewmodel.paciente.consulta.ConfirmacaoConsultaViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

/**
 * Tela de confirmação antes de efetivar o agendamento.
 * Recebe via Intent:
 *   - ID_PACIENTE     (Long)
 *   - ID_MEDICO       (Long)
 *   - NOME_MEDICO     (String)
 *   - ESPECIALIDADE   (String)
 *   - DATA_CONSULTA   (String  — formato "yyyy-MM-dd")
 *   - HORA_CONSULTA   (String  — formato "HH:mm")
 *   - ID_CONSULTA_OFERTADA (Long)
 */
class ConfirmacaoConsultaActivity : AppCompatActivity() {

    private val viewModel: ConfirmacaoConsultaViewModel by viewModels()

    private var idPaciente: Long = -1L
    private var idMedico: Long = -1L
    private var idConsultaOfertada: Long = -1L
    private var dataConsulta: String = ""
    private var horaConsulta: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmacao_consulta)

        idPaciente          = intent.getLongExtra("ID_PACIENTE", -1L)
        idMedico            = intent.getLongExtra("ID_MEDICO", -1L)
        idConsultaOfertada  = intent.getLongExtra("ID_CONSULTA_OFERTADA", -1L)
        dataConsulta        = intent.getStringExtra("DATA_CONSULTA") ?: ""
        horaConsulta        = intent.getStringExtra("HORA_CONSULTA") ?: ""

        val nomeMedico   = intent.getStringExtra("NOME_MEDICO")    ?: "Médico"
        val especialidade = intent.getStringExtra("ESPECIALIDADE") ?: ""

        preencherResumo(nomeMedico, especialidade)
        configurarBotoes()
        observeViewModel()
        configurarBottomNav(R.id.nav_consultas)
    }

    private fun preencherResumo(nomeMedico: String, especialidade: String) {
        findViewById<TextView>(R.id.tvConfNomeMedico).text       = nomeMedico
        findViewById<TextView>(R.id.tvConfEspecialidade).text    = especialidade
        findViewById<TextView>(R.id.tvConfData).text             = formatarData(dataConsulta)
        findViewById<TextView>(R.id.tvConfHora).text             = horaConsulta
    }

    private fun configurarBotoes() {
        findViewById<ImageButton>(R.id.btnVoltarConfirmacao).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnConfirmarAgendamento).setOnClickListener {
            if (idPaciente == -1L || idConsultaOfertada == -1L || dataConsulta.isEmpty() || horaConsulta.isEmpty()) {
                Toast.makeText(this, "Dados incompletos. Volte e tente novamente.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.confirmarConsulta(idPaciente, idConsultaOfertada, dataConsulta, horaConsulta)
        }

        findViewById<Button>(R.id.btnVoltarEscolha).setOnClickListener { finish() }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ConfirmacaoConsultaViewModel.UiState.Idle    -> setLoading(false)
                    is ConfirmacaoConsultaViewModel.UiState.Loading -> setLoading(true)
                    is ConfirmacaoConsultaViewModel.UiState.Error   -> {
                        setLoading(false)
                        Toast.makeText(this@ConfirmacaoConsultaActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                    is ConfirmacaoConsultaViewModel.UiState.Sucesso -> {
                        setLoading(false)
                        Toast.makeText(this@ConfirmacaoConsultaActivity, "Consulta agendada com sucesso!", Toast.LENGTH_LONG).show()
                        startActivity(
                            Intent(this@ConfirmacaoConsultaActivity, MinhasConsultasActivity::class.java).apply {
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
        val btn = findViewById<Button>(R.id.btnConfirmarAgendamento)
        btn.isEnabled = !carregando
        btn.text = if (carregando) "Agendando..." else "Confirmar Agendamento"
    }

    /** "2025-06-15" → "15/06/2025" */
    private fun formatarData(data: String): String {
        return try {
            val partes = data.split("-")
            "${partes[2]}/${partes[1]}/${partes[0]}"
        } catch (e: Exception) { data }
    }

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavConfirmacao)
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
