package com.example.medicoapplication.UI.activities.medico.perfil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.viewmodel.medico.perfil.PerfilMedicoViewModel
import kotlinx.coroutines.launch

class PerfilMedicoActivity : BaseActivity() {

    private val viewModel: PerfilMedicoViewModel by viewModels()

    override val menuType = BottomMenuType.MEDICO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_medico)

        // NOME_MEDICO como fallback de UX enquanto a requisição ainda não voltou
        val nomeFallback = intent.getStringExtra("NOME_MEDICO") ?: "Médico"
        findViewById<TextView>(R.id.tvSaudacaoPerfil).text     = "Olá, $nomeFallback"
        findViewById<TextView>(R.id.tvNomeCompletoMedico).text = nomeFallback.uppercase()

        findViewById<Button>(R.id.btnEditarPerfilMedico).setOnClickListener {
            startActivity(Intent(this, EditarPerfilMedicoActivity::class.java))
        }

        observeViewModel()
        viewModel.carregarPerfil()
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
                        val nome = medico.usuario?.nome ?: "Médico"
                        findViewById<TextView>(R.id.tvNomeCompletoMedico).text = nome.uppercase()
                        findViewById<TextView>(R.id.tvSaudacaoPerfil).text =
                            "Olá, ${nome.split(" ").firstOrNull() ?: nome}"

                        val crm = if (!medico.crmDigitos.isNullOrBlank() && !medico.crmUf.isNullOrBlank())
                            "${medico.crmDigitos}/${medico.crmUf}" else "—"
                        findViewById<TextView>(R.id.tvCrm).text               = crm
                        findViewById<TextView>(R.id.tvCpfMedico).text          = medico.usuario?.cpf            ?: "—"
                        findViewById<TextView>(R.id.tvEmailMedico).text        = medico.usuario?.email          ?: "—"
                        findViewById<TextView>(R.id.tvTelefoneMedico).text     = medico.usuario?.telefone       ?: "—"
                        findViewById<TextView>(R.id.tvNascimentoMedico).text   = medico.usuario?.dataNascimento ?: "—"
                        findViewById<TextView>(R.id.tvEspecialidadeMedico).text =
                            medico.especialidades.firstOrNull()?.especialidade?.nome ?: "—"
                    }
                }
            }
        }
    }
}
