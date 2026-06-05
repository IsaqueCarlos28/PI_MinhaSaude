package com.example.medicoapplication.UI.activities.paciente

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
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.common.formatters.DateFormatter
import com.example.medicoapplication.UI.common.validations.ConsultaValidator
import com.example.medicoapplication.UI.common.validations.ValidationResult
import com.example.medicoapplication.data.remote.NetworkError
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
class ConfirmacaoConsultaActivity : BaseActivity() {

    private val viewModel: ConfirmacaoConsultaViewModel by viewModels()

    private var idPaciente: Long = -1L
    private var idMedico: Long = -1L
    private var idConsultaOfertada: Long = -1L
    private var dataConsulta: String = ""
    private var horaConsulta: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmacao_consulta)

        idPaciente         = intent.getLongExtra("ID_PACIENTE", -1L)
        idMedico           = intent.getLongExtra("ID_MEDICO", -1L)
        idConsultaOfertada = intent.getLongExtra("ID_CONSULTA_OFERTADA", -1L)
        dataConsulta       = intent.getStringExtra("DATA_CONSULTA") ?: ""
        horaConsulta       = intent.getStringExtra("HORA_CONSULTA") ?: ""

        val nomeMedico    = intent.getStringExtra("NOME_MEDICO")    ?: "Médico"
        val especialidade = intent.getStringExtra("ESPECIALIDADE")  ?: ""

        preencherResumo(nomeMedico, especialidade)
        configurarBotoes()
        observeViewModel()
        setupBottomNavigation(R.id.nav_consultas_paciente)
    }

    private fun preencherResumo(nomeMedico: String, especialidade: String) {
        // DateFormatter used instead of inline split logic
        findViewById<TextView>(R.id.tvConfNomeMedico).text    = nomeMedico
        findViewById<TextView>(R.id.tvConfEspecialidade).text = especialidade
        findViewById<TextView>(R.id.tvConfData).text          = DateFormatter.apiToUiDate(dataConsulta)
        findViewById<TextView>(R.id.tvConfHora).text          = horaConsulta
    }

    private fun configurarBotoes() {
        findViewById<ImageButton>(R.id.btnVoltarConfirmacao).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnConfirmarAgendamento).setOnClickListener {
            when (val result = ConsultaValidator.validarConfirmacao(
                idPaciente, idConsultaOfertada, dataConsulta, horaConsulta
            )) {
                is ValidationResult.Success ->
                    viewModel.confirmarConsulta(idPaciente, idConsultaOfertada, dataConsulta, horaConsulta)
                is ValidationResult.Error   ->
                    showToast(result.message)
            }
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
                        handleError(state.error)
                    }
                    is ConfirmacaoConsultaViewModel.UiState.Success -> {
                        setLoading(false)
                        showToast("Consulta agendada com sucesso!")
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

}