package com.example.medicoapplication.UI.activities.medico

import ConsultasMedicoAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.activities.auth_e_cadastro.LoginActivity
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.viewmodel.medico.consulta.ConsultasMedicoViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class HomeMedicoActivity : BaseActivity() {

    // (1) dados recebidos do login
    private var idMedico: Long = -1L

    // (2) ViewModel e Adapter declarados mas não inicializados ainda
    private lateinit var viewModel: ConsultasMedicoViewModel
    private lateinit var adapter: ConsultasMedicoAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_medico)

        idMedico = intent.getLongExtra("ID_MEDICO", -1L)

        if (idMedico == -1L) {                               // (3)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setupViewModel()
        setupRecyclerView()
        observeViewModel()

        viewModel.carregarConsultas(idMedico)                // (4)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[ConsultasMedicoViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = ConsultasMedicoAdapter(
            consultas   = emptyList(),
            onItemClick = { consulta ->                      // (5)
                // por enquanto só loga — implementar detalhes depois
                Toast.makeText(
                    this,
                    "Consulta: ${consulta.nomePaciente}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        findViewById<RecyclerView>(R.id.rv_consultas_medico).apply {
            layoutManager = LinearLayoutManager(this@HomeMedicoActivity)
            adapter       = this@HomeMedicoActivity.adapter  // (6)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ConsultasMedicoViewModel.UiState.Idle    -> Unit
                        is ConsultasMedicoViewModel.UiState.Loading -> setLoading(true)
                        is ConsultasMedicoViewModel.UiState.Success -> {
                            setLoading(false)
                            adapter.atualizarLista(state.consultas)  // (7)
                        }
                        is ConsultasMedicoViewModel.UiState.Error   -> {
                            setLoading(false)
                            val mensagem = when (state.error) {
                                is NetworkError.SemConexao  -> "Sem internet"
                                is NetworkError.Timeout     -> "Tempo esgotado"
                                is NetworkError.ErrroServidor -> "Erro no servidor"
                                else -> "Erro ao carregar consultas"
                            }
                            Toast.makeText(this@HomeMedicoActivity, mensagem, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        findViewById<ProgressBar>(R.id.progress_bar).visibility =
            if (isLoading) View.VISIBLE else View.GONE
        findViewById<RecyclerView>(R.id.rv_consultas_medico).visibility =
            if (isLoading) View.GONE else View.VISIBLE
    }

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavMedico)
        bottomNav.selectedItemId = itemSelecionado
        val idMedico = intent.getLongExtra("ID_MEDICO", -1L)
        val nomeMedico = intent.getStringExtra("NOME_MEDICO") ?: "Médico"

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> true
                R.id.nav_agenda -> {
                    startActivity(Intent(this, AgendaMedicoActivity::class.java).apply {
                        putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico)
                    })
                    false
                }
                R.id.nav_consultas_med -> {
                    startActivity(Intent(this, ConsultasMedicoActivity::class.java).apply {
                        putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico)
                    })
                    false
                }
                R.id.nav_usuario -> {
                    startActivity(Intent(this, PerfilMedicoActivity::class.java).apply {
                        putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico)
                    })
                    false
                }
                R.id.nav_config -> {
                    startActivity(Intent(this, ConfiguracoesMedicoActivity::class.java).apply {
                        putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico)
                    })
                    false
                }
                else -> false
            }
        }
    }
}
