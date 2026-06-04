package com.example.medicoapplication.UI.activities.paciente

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.common.mappers.ConsultaMapper
import com.example.medicoapplication.viewmodel.paciente.consulta.DetalheConsultaViewModel
import com.example.medicoapplication.data.remote.DTO.StatusConsulta
import com.example.medicoapplication.data.remote.NetworkError
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

/**
 * Exibe os detalhes completos de uma consulta do paciente.
 * Recebe via Intent:
 *   - ID_PACIENTE (Long)
 *   - ID_EVENTO   (Long)
 */
class DetalheConsultaActivity : BaseActivity() {

    private val viewModel: DetalheConsultaViewModel by viewModels()

    private var idPaciente: Long = -1L
    private var idEvento:   Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhe_consulta)

        idPaciente = intent.getLongExtra("ID_PACIENTE", -1L)
        idEvento   = intent.getLongExtra("ID_EVENTO",   -1L)

        findViewById<ImageButton>(R.id.btnVoltarDetalhe).setOnClickListener { finish() }

        observeViewModel()

        if (idPaciente != -1L && idEvento != -1L) {
            viewModel.carregarConsulta(idPaciente, idEvento)
        } else {
            showToast("Consulta não encontrada.")
            finish()
        }

        configurarBottomNav(R.id.nav_consultas)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is DetalheConsultaViewModel.UiState.Idle    -> Unit
                    is DetalheConsultaViewModel.UiState.Loading -> Unit
                    is DetalheConsultaViewModel.UiState.Error   -> handleError(state.error)
                    is DetalheConsultaViewModel.UiState.Success -> {
                        // All display formatting is done by the mapper
                        val display = ConsultaMapper.toDisplay(state.consulta)

                        findViewById<TextView>(R.id.tvDetalheMedico).text   = display.nomeMedico
                        findViewById<TextView>(R.id.tvDetalheData).text     = display.data
                        findViewById<TextView>(R.id.tvDetalheHora).text     = display.horaInicio
                        findViewById<TextView>(R.id.tvDetalheConvenio).text = display.convenio
                        findViewById<TextView>(R.id.tvDetalheStatus).text   = display.status

                        val podeAcionar = display.statusEnum == StatusConsulta.AGENDADA
                        val btnReagendar = findViewById<Button>(R.id.btnReagendarDetalhe)
                        val btnCancelar  = findViewById<Button>(R.id.btnCancelarDetalhe)

                        btnReagendar.visibility = if (podeAcionar) View.VISIBLE else View.GONE
                        btnCancelar.visibility  = if (podeAcionar) View.VISIBLE else View.GONE

                        btnReagendar.setOnClickListener {
                            startActivity(
                                Intent(this@DetalheConsultaActivity, ReagendarConsultaActivity::class.java).apply {
                                    putExtra("ID_PACIENTE", idPaciente)
                                    putExtra("ID_EVENTO",   idEvento)
                                    putExtra("ID_MEDICO",   state.consulta.idMedico)
                                    putExtra("NOME_MEDICO", state.consulta.nomeMedico ?: "")
                                }
                            )
                        }

                        btnCancelar.setOnClickListener {
                            viewModel.cancelarConsulta(idPaciente, idEvento) {
                                showToast("Consulta cancelada.")
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavDetalhe)
        bottomNav.selectedItemId = itemSelecionado
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home      -> { startActivity(Intent(this, HomePacienteActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                R.id.nav_consultas -> { startActivity(Intent(this, MinhasConsultasActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                R.id.nav_medicos   -> { startActivity(Intent(this, BuscaMedicosActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                R.id.nav_perfil    -> { startActivity(Intent(this, PerfilPacienteActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                else -> false
            }
        }
    }
}