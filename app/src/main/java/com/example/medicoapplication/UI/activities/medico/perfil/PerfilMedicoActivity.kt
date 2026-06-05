package com.example.medicoapplication.UI.activities.medico.perfil

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.viewmodel.medico.perfil.PerfilMedicoViewModel
import kotlinx.coroutines.launch

class PerfilMedicoActivity : BaseActivity() {

    private val viewModel: PerfilMedicoViewModel by viewModels()

    private var idMedico: Long = -1L
    private var nomeMedico: String = "Médico"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_medico)

        nomeMedico = intent.getStringExtra("NOME_MEDICO") ?: "Médico"
        idMedico   = intent.getLongExtra("ID_MEDICO", -1L)

        findViewById<TextView>(R.id.tvSaudacaoPerfil).text = "Olá, $nomeMedico"
        findViewById<TextView>(R.id.tvNomeCompletoMedico).text = nomeMedico.uppercase()

        findViewById<Button>(R.id.btnEditarPerfilMedico).setOnClickListener {
            showToast("Edição de perfil em breve!")
        }

        observeViewModel()

        if (idMedico != -1L) viewModel.carregarPerfil()

        setupBottomNavigation(R.id.nav_perfil_medico)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is PerfilMedicoViewModel.UiState.Idle    -> Unit
                    is PerfilMedicoViewModel.UiState.Loading -> Unit
                    is PerfilMedicoViewModel.UiState.Error   -> {
                        handleError(state.error)
                    }
                    is PerfilMedicoViewModel.UiState.Success -> {
                        val medico = state.medico
                        val nome = medico.usuario?.nome ?: nomeMedico
                        findViewById<TextView>(R.id.tvNomeCompletoMedico).text = nome.uppercase()
                        findViewById<TextView>(R.id.tvSaudacaoPerfil).text = "Olá, ${nome.split(" ").firstOrNull() ?: nome}"

                        val crm = if (!medico.crmDigitos.isNullOrBlank() && !medico.crmUf.isNullOrBlank())
                            "${medico.crmDigitos}/${medico.crmUf}" else "—"
                        findViewById<TextView>(R.id.tvCrm).text = crm
                        findViewById<TextView>(R.id.tvCpfMedico).text          = medico.usuario?.cpf           ?: "—"
                        findViewById<TextView>(R.id.tvEmailMedico).text        = medico.usuario?.email         ?: "—"
                        findViewById<TextView>(R.id.tvTelefoneMedico).text     = medico.usuario?.telefone      ?: "—"
                        findViewById<TextView>(R.id.tvNascimentoMedico).text   = medico.usuario?.dataNascimento?: "—"
                        findViewById<TextView>(R.id.tvEspecialidadeMedico).text =
                            medico.especialidades.firstOrNull()?.especialidade?.nome ?: "—"
                    }
                }
            }
        }
    }
}