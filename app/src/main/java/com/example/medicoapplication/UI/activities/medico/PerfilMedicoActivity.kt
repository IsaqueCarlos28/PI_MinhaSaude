package com.example.medicoapplication.UI.activities.medico

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.activities.medico.viewmodel.PerfilMedicoViewModel
import com.example.medicoapplication.data.remote.NetworkError
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class PerfilMedicoActivity : AppCompatActivity() {

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
            Toast.makeText(this, "Edição de perfil em breve!", Toast.LENGTH_SHORT).show()
        }

        observeViewModel()

        if (idMedico != -1L) viewModel.carregarPerfil(idMedico)

        configurarBottomNav(R.id.nav_usuario)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is PerfilMedicoViewModel.UiState.Idle    -> Unit
                    is PerfilMedicoViewModel.UiState.Loading -> Unit
                    is PerfilMedicoViewModel.UiState.Error   -> {
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
                        Toast.makeText(this@PerfilMedicoActivity, mensagem, Toast.LENGTH_SHORT).show()
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

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavPerfilMedico)
        bottomNav.selectedItemId = itemSelecionado
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio       -> { startActivity(Intent(this, HomeMedicoActivity::class.java).apply { putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico) }); false }
                R.id.nav_agenda       -> { startActivity(Intent(this, AgendaMedicoActivity::class.java).apply { putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico) }); false }
                R.id.nav_consultas_med-> { startActivity(Intent(this, ConsultasMedicoActivity::class.java).apply { putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico) }); false }
                R.id.nav_usuario      -> true
                R.id.nav_config       -> { startActivity(Intent(this, ConfiguracoesMedicoActivity::class.java).apply { putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico) }); false }
                else -> false
            }
        }
    }
}
