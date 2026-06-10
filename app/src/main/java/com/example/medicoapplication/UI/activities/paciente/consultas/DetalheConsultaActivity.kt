package com.example.medicoapplication.UI.activities.paciente.consultas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.UI.common.mappers.ConsultaMapper
import com.example.medicoapplication.data.remote.DTO.StatusConsulta
import com.example.medicoapplication.viewmodel.paciente.consulta.DetalheConsultaViewModel
import kotlinx.coroutines.launch

/**
 * Shows full details of a paciente's consulta.
 * Receives via Intent:
 *   - ID_EVENTO (Long)
 *
 * FIX: cancel button now shows an AlertDialog before firing the API call.
 */
class DetalheConsultaActivity : BaseActivity() {

    private val viewModel: DetalheConsultaViewModel by viewModels()

    override val menuType = BottomMenuType.PACIENTE

    private var idEvento: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhe_consulta)

        idEvento = intent.getLongExtra("ID_EVENTO", -1L)

        findViewById<ImageButton>(R.id.btnVoltarDetalhe).setOnClickListener { finish() }

        observeViewModel()

        if (idEvento != -1L) {
            viewModel.carregarConsulta(idEvento)
        } else {
            showToast("Consulta não encontrada.")
            finish()
        }

        setupBottomNavigation(R.id.nav_consultas_paciente)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is DetalheConsultaViewModel.UiState.Idle    -> Unit
                    is DetalheConsultaViewModel.UiState.Loading -> Unit
                    is DetalheConsultaViewModel.UiState.Error   -> handleError(state.error)
                    is DetalheConsultaViewModel.UiState.Success -> {
                        val display = ConsultaMapper.toDisplay(state.consulta)

                        findViewById<TextView>(R.id.tvDetalheMedico).text   = display.nomeMedico
                        findViewById<TextView>(R.id.tvDetalheData).text     = display.data
                        findViewById<TextView>(R.id.tvDetalheHora).text     = display.horaInicio
                        findViewById<TextView>(R.id.tvDetalheConvenio).text = display.convenio
                        findViewById<TextView>(R.id.tvDetalheStatus).text   = display.status

                        val podeAcionar  = display.statusEnum == StatusConsulta.AGENDADA
                        val btnReagendar = findViewById<Button>(R.id.btnReagendarDetalhe)
                        val btnCancelar  = findViewById<Button>(R.id.btnCancelarDetalhe)

                        btnReagendar.visibility = if (podeAcionar) View.VISIBLE else View.GONE
                        btnCancelar.visibility  = if (podeAcionar) View.VISIBLE else View.GONE

                        btnReagendar.setOnClickListener {
                            startActivity(
                                Intent(
                                    this@DetalheConsultaActivity,
                                    ReagendarConsultaActivity::class.java
                                ).apply {
                                    putExtra("ID_EVENTO",   idEvento)
                                    putExtra("NOME_MEDICO", state.consulta.nomeMedico ?: "")
                                }
                            )
                        }

                        btnCancelar.setOnClickListener {
                            confirmarCancelamento()
                        }
                    }
                }
            }
        }
    }

    /**
     * FIX: shows an AlertDialog asking the user to confirm before calling the API.
     * Cancellation is irreversible, so a two-step confirmation is required.
     */
    private fun confirmarCancelamento() {
        AlertDialog.Builder(this)
            .setTitle("Cancelar consulta")
            .setMessage("Tem certeza que deseja cancelar esta consulta? Esta ação não pode ser desfeita.")
            .setPositiveButton("Sim, cancelar") { _, _ ->
                viewModel.cancelarConsulta(idEvento) {
                    showToast("Consulta cancelada.")
                    finish()
                }
            }
            .setNegativeButton("Voltar", null)
            .show()
    }
}
