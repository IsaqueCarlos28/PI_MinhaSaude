package com.example.medicoapplication.UI.activities.medico.consultas

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.viewmodel.medico.consulta.VisualizarConsultaMedicoViewModel
import kotlinx.coroutines.launch

class VisualisarConsultaMedico : BaseActivity() {

    private val viewModel: VisualizarConsultaMedicoViewModel by viewModels()
    private var idEvento: Long = -1L

    override val menuType = BottomMenuType.MEDICO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualisar_consulta_medico)

        idEvento = intent.getLongExtra("ID_EVENTO", -1L)

        findViewById<ImageButton>(R.id.btnVoltarConsultaMedico).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnRealizarConsultaMedico).setOnClickListener {
            if (idEvento > 0) viewModel.marcarComoRealizada(idEvento)
        }

        findViewById<Button>(R.id.btnCancelarConsultaMedico).setOnClickListener {
            if (idEvento > 0) viewModel.cancelarConsulta(idEvento)
        }

        observeViewModel()
        setupBottomNavigation(R.id.nav_consultas_medico)

        if (idEvento > 0) {
            viewModel.carregarConsulta(idEvento)
        } else {
            showToast("Consulta não encontrada.")
            finish()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is VisualizarConsultaMedicoViewModel.UiState.Idle -> setLoading(false)
                    is VisualizarConsultaMedicoViewModel.UiState.Loading -> setLoading(true)
                    is VisualizarConsultaMedicoViewModel.UiState.Success -> {
                        setLoading(false)
                        preencherDados(state.consulta)
                    }
                    is VisualizarConsultaMedicoViewModel.UiState.Error -> {
                        setLoading(false)
                        handleError(state.error)
                    }
                }
            }
        }
    }

    private fun preencherDados(consulta: ConsultaResponseDto) {
        findViewById<TextView>(R.id.tvConsultaMedicoPaciente).text = consulta.nomePaciente ?: "Paciente não informado"
        findViewById<TextView>(R.id.tvConsultaMedicoNome).text = consulta.nomeMedico ?: "Médico não informado"
        findViewById<TextView>(R.id.tvConsultaMedicoData).text = consulta.data
        findViewById<TextView>(R.id.tvConsultaMedicoHora).text = montarHorario(consulta)
        findViewById<TextView>(R.id.tvConsultaMedicoConvenio).text = consulta.nomeConvenio ?: "Particular"
        findViewById<TextView>(R.id.tvConsultaMedicoStatus).text = consulta.status.name
    }

    private fun montarHorario(consulta: ConsultaResponseDto): String {
        return if (consulta.horaFim.isNullOrBlank()) {
            consulta.horaInicio
        } else {
            "${consulta.horaInicio} - ${consulta.horaFim}"
        }
    }

    private fun setLoading(isLoading: Boolean) {
        findViewById<Button>(R.id.btnRealizarConsultaMedico).isEnabled = !isLoading
        findViewById<Button>(R.id.btnCancelarConsultaMedico).isEnabled = !isLoading
    }
}
