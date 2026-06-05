package com.example.medicoapplication.UI.activities.paciente.perfil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.activities.paciente.configuracao.ConfiguracoesPacienteActivity
import com.example.medicoapplication.viewmodel.paciente.perfil.PerfilPacienteViewModel
import kotlinx.coroutines.launch

class PerfilPacienteActivity : BaseActivity() {

    private val viewModel: PerfilPacienteViewModel by viewModels()

    private var idPaciente: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_paciente)

        idPaciente = intent.getLongExtra("ID_PACIENTE", -1L)

        observeViewModel()

        if (idPaciente != -1L) viewModel.carregarPerfil()

        // ✅ Ícone de configurações no header
        findViewById<ImageButton>(R.id.btnConfiguracoes).setOnClickListener {
            startActivity(
                Intent(this, ConfiguracoesPacienteActivity::class.java).apply {
                    putExtra("ID_PACIENTE", idPaciente)
                }
            )
        }

        // ✅ Botão Editar Perfil
        findViewById<Button>(R.id.btnEditarPerfil).setOnClickListener {
            startActivity(
                Intent(this, EditarPerfilPacienteActivity::class.java).apply {
                    putExtra("ID_PACIENTE", idPaciente)
                }
            )
        }

        setupBottomNavigation(R.id.nav_perfil_paciente)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is PerfilPacienteViewModel.UiState.Idle    -> Unit
                    is PerfilPacienteViewModel.UiState.Loading -> Unit
                    is PerfilPacienteViewModel.UiState.Error   -> {
                        handleError(state.error)
                    }
                    is PerfilPacienteViewModel.UiState.Success -> {
                        val p = state.paciente
                        findViewById<TextView>(R.id.tvNomePerfil).text       = p.nome           ?: "—"
                        findViewById<TextView>(R.id.tvEmailPerfil).text      = p.email          ?: "—"
                        findViewById<TextView>(R.id.tvCpfPerfil).text        = p.cpf            ?: "—"
                        findViewById<TextView>(R.id.tvTelefonePerfil).text   = p.telefone       ?: "—"
                        findViewById<TextView>(R.id.tvNascimentoPerfil).text = p.dataNascimento ?: "—"
                    }
                }
            }
        }
    }
}