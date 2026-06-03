package com.example.medicoapplication.UI.activities.paciente

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.auth_e_cadastro.LoginActivity
import com.example.medicoapplication.activities.paciente.viewmodel.HomePacienteViewModel
import com.example.medicoapplication.UI.adapters.ConsultasPacienteAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class HomePacienteActivity : AppCompatActivity() {

    private val viewModel: HomePacienteViewModel by viewModels()

    private var idPaciente: Long = -1L
    private var emailPaciente: String = ""

    private lateinit var tvSaudacao: TextView
    private lateinit var rvProximasConsultas: RecyclerView
    private lateinit var adaptadorConsultas: ConsultasPacienteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_paciente)

        idPaciente    = intent.getLongExtra("ID_PACIENTE", -1L)
        emailPaciente = intent.getStringExtra("EMAIL_PACIENTE") ?: ""

        tvSaudacao          = findViewById(R.id.tvSaudacao)
        rvProximasConsultas = findViewById(R.id.rvProximasConsultas)

        configurarRecyclerView()
        configurarAtalhos()
        configurarBottomNav(R.id.nav_home)
        observeViewModel()

        if (idPaciente != -1L) {
            viewModel.carregarNome(idPaciente, emailPaciente)
            viewModel.carregarConsultas(idPaciente)
        } else {
            tvSaudacao.text = "Olá, ${emailPaciente.substringBefore("@")}"
            Toast.makeText(this, "Sessão inválida. Faça login novamente.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }

    private fun configurarRecyclerView() {
        adaptadorConsultas = ConsultasPacienteAdapter(
            consultas   = emptyList(),
            onItemClick = { consulta ->
                startActivity(
                    Intent(this, DetalheConsultaActivity::class.java).apply {
                        putExtra("ID_PACIENTE", idPaciente)
                        putExtra("ID_EVENTO",   consulta.id)
                    }
                )
            },
            onReagendar = { consulta ->
                startActivity(
                    Intent(this, ReagendarConsultaActivity::class.java).apply {
                        putExtra("ID_PACIENTE", idPaciente)
                        putExtra("ID_EVENTO",   consulta.id)
                        putExtra("ID_MEDICO",   consulta.idMedico)
                        putExtra("NOME_MEDICO", consulta.nomeMedico ?: "")
                    }
                )
            },
            onCancelar  = { consulta ->
                viewModel.cancelarConsulta(idPaciente, consulta) {
                    viewModel.carregarConsultas(idPaciente)
                    Toast.makeText(this, "Consulta cancelada.", Toast.LENGTH_SHORT).show()
                }
            }
        )
        rvProximasConsultas.layoutManager = LinearLayoutManager(this)
        rvProximasConsultas.adapter = adaptadorConsultas
    }

    private fun configurarAtalhos() {
        findViewById<LinearLayout>(R.id.btnAcaoConsultas).setOnClickListener {
            startActivity(Intent(this, MinhasConsultasActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) })
        }
        findViewById<LinearLayout>(R.id.btnAcaoMedicos).setOnClickListener {
            startActivity(Intent(this, BuscaMedicosActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) })
        }
        findViewById<Button>(R.id.btnVerTodasConsultas).setOnClickListener {
            startActivity(Intent(this, MinhasConsultasActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) })
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.nomeState.collect { state ->
                if (state is HomePacienteViewModel.NomeState.Success) {
                    tvSaudacao.text = "Olá, ${state.primeiroNome}"
                }
            }
        }
        lifecycleScope.launch {
            viewModel.consultasState.collect { state ->
                when (state) {
                    is HomePacienteViewModel.ConsultasState.Idle    -> Unit
                    is HomePacienteViewModel.ConsultasState.Loading -> Unit
                    is HomePacienteViewModel.ConsultasState.Success -> adaptadorConsultas.atualizarLista(state.consultas)
                    is HomePacienteViewModel.ConsultasState.Error   -> Toast.makeText(this@HomePacienteActivity, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavPaciente)
        bottomNav.selectedItemId = itemSelecionado
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home      -> true
                R.id.nav_consultas -> { startActivity(Intent(this, MinhasConsultasActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                R.id.nav_medicos   -> { startActivity(Intent(this, BuscaMedicosActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                R.id.nav_perfil    -> { startActivity(Intent(this, PerfilPacienteActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                else -> false
            }
        }
    }
}
