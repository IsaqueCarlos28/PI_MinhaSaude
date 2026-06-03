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
import com.example.medicoapplication.UI.activities.paciente.configuracao.ConfiguracoesPacienteActivity
import com.example.medicoapplication.activities.paciente.viewmodel.PerfilPacienteViewModel
import com.example.medicoapplication.data.remote.NetworkError
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class PerfilPacienteActivity : BaseActivity() {

    private val viewModel: PerfilPacienteViewModel by viewModels()

    private var idPaciente: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_paciente)

        idPaciente = intent.getLongExtra("ID_PACIENTE", -1L)

        observeViewModel()

        if (idPaciente != -1L) viewModel.carregarPerfil(idPaciente)

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

        configurarBottomNav(R.id.nav_perfil)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is PerfilPacienteViewModel.UiState.Idle    -> Unit
                    is PerfilPacienteViewModel.UiState.Loading -> Unit
                    is PerfilPacienteViewModel.UiState.Error   -> {
                        val mensagem = when (state.error) {
                            is NetworkError.NaoAutorizado ->
                                "Email ou senha incorretos. Verifique seus dados."
                            is NetworkError.SemConexao ->
                                "Sem conexão com a internet. Verifique sua rede."
                            is NetworkError.Timeout ->
                                "O servidor demorou para responder. Tente novamente."
                            is NetworkError.ErrroServidor ->
                                "Problema no servidor. Tente mais tarde."
                            is NetworkError.Desconhecido ->
                                "Erro inesperado: ${state.error.mensagem}"
                            else ->
                                "Algo deu errado. Tente novamente."
                        }
                        Toast.makeText(this@PerfilPacienteActivity, mensagem, Toast.LENGTH_SHORT).show()
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

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavPerfil)
        bottomNav.selectedItemId = itemSelecionado
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home      -> { startActivity(Intent(this, HomePacienteActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                R.id.nav_consultas -> { startActivity(Intent(this, MinhasConsultasActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                R.id.nav_medicos   -> { startActivity(Intent(this, BuscaMedicosActivity::class.java)); false }
                R.id.nav_perfil    -> true
                else -> false
            }
        }
    }
}