package com.example.medicoapplication.UI.activities.paciente.medicos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.activities.paciente.medicos.marcar_consulta.AgendarConsultaActivity
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.UI.common.mappers.MedicoMapper
import com.example.medicoapplication.viewmodel.paciente.medicos.PerfilMedicoPublicoViewModel
import kotlinx.coroutines.launch

class PerfilMedicoPublicoActivity : BaseActivity() {

    private val viewModel: PerfilMedicoPublicoViewModel by viewModels()

    private var medicoId: Long = -1L

    override val menuType = BottomMenuType.PACIENTE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_medico_publico)

        medicoId = intent.getLongExtra("MEDICO_ID", -1L)
        val nomeFallback = intent.getStringExtra("NOME_MEDICO") ?: "Médico"

        // Preenche o nome imediatamente com o que veio via Intent,
        // enquanto a chamada de rede não retorna
        findViewById<TextView>(R.id.tvNomeMedicoPublico).text = nomeFallback

        findViewById<ImageButton>(R.id.btnVoltarPerfilPublico).setOnClickListener { finish() }

        observeViewModel()

        if (medicoId != -1L) viewModel.carregarPerfil(medicoId)

        setupBottomNavigation(R.id.nav_medicos_paciente)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is PerfilMedicoPublicoViewModel.UiState.Idle    -> Unit
                    is PerfilMedicoPublicoViewModel.UiState.Loading -> Unit
                    is PerfilMedicoPublicoViewModel.UiState.Error   -> handleError(state.error)
                    is PerfilMedicoPublicoViewModel.UiState.Success -> {
                        val medico = state.medico

                        // Mapping responsibility moved to MedicoMapper
                        val info = MedicoMapper.toAgendamentoInfo(medico)

                        findViewById<TextView>(R.id.tvNomeMedicoPublico).text    = info.nome
                        findViewById<TextView>(R.id.tvEspecialidadePublico).text = info.especialidade
                        findViewById<TextView>(R.id.tvEspecialidadesPublico).text = info.especialidade
                        findViewById<TextView>(R.id.tvCrmPublico).text           = "CRM: ${info.crm}"
                        findViewById<TextView>(R.id.tvEmailPublico).text         = medico.usuario?.email ?: "—"
                        findViewById<TextView>(R.id.tvTelefonePublico).text      = medico.usuario?.telefone ?: "—"

                        val consultasOfertadas = state.consultasOfertadas
                        findViewById<Button>(R.id.btnAgendarComEsteMedico).setOnClickListener {
                            if (consultasOfertadas.isEmpty()) {
                                showToast("Este médico não possui consultas disponíveis no momento.")
                                return@setOnClickListener
                            }
                            startActivity(
                                Intent(this@PerfilMedicoPublicoActivity, AgendarConsultaActivity::class.java).apply {
                                    putExtra("ID_MEDICO",            medicoId)
                                    putExtra("NOME_MEDICO",          info.nome)
                                    putExtra("ESPECIALIDADE",        info.especialidade)
                                    putExtra("ID_CONSULTA_OFERTADA", consultasOfertadas.first().id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
