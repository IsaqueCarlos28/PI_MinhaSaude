package com.example.medicoapplication.UI.activities.paciente

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.activities.paciente.consultas.DetalheConsultaActivity
import com.example.medicoapplication.UI.activities.paciente.consultas.MinhasConsultasActivity
import com.example.medicoapplication.UI.activities.paciente.medicos.BuscaMedicosActivity
import com.example.medicoapplication.viewmodel.paciente.HomePacienteViewModel
import com.example.medicoapplication.UI.adapters.ConsultasPacienteAdapter
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import kotlinx.coroutines.launch

class HomePacienteActivity : BaseActivity() {

    private val viewModel: HomePacienteViewModel by viewModels()

    private lateinit var tvSaudacao: TextView
    private lateinit var rvProximasConsultas: RecyclerView
    private lateinit var adaptadorConsultas: ConsultasPacienteAdapter

    override val menuType = BottomMenuType.PACIENTE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_paciente)

        tvSaudacao          = findViewById(R.id.tvSaudacao)
        rvProximasConsultas = findViewById(R.id.rvProximasConsultas)

        configurarRecyclerView()
        configurarAtalhos()
        setupBottomNavigation(R.id.nav_inicio_paciente)

        observeViewModel()

        viewModel.carregarNome()
        viewModel.carregarConsultas()
    }

    private fun configurarRecyclerView() {
        adaptadorConsultas = ConsultasPacienteAdapter(
            consultas   = emptyList(),
            onItemClick = { consulta ->
                startActivity(
                    Intent(this, DetalheConsultaActivity::class.java)
                )
            }
        )
        rvProximasConsultas.layoutManager = LinearLayoutManager(this)
        rvProximasConsultas.adapter = adaptadorConsultas
    }

    private fun configurarAtalhos() {
        findViewById<LinearLayout>(R.id.btnAcaoConsultas).setOnClickListener {
            startActivity(Intent(this, MinhasConsultasActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.btnAcaoMedicos).setOnClickListener {
            startActivity(Intent(this, BuscaMedicosActivity::class.java))
        }
        findViewById<Button>(R.id.btnVerTodasConsultas).setOnClickListener {
            startActivity(Intent(this, MinhasConsultasActivity::class.java))
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.nomeState.collect { state ->
                when (state) {
                    is HomePacienteViewModel.NomeState.Idle    -> tvSaudacao.text = "Olá,..."
                    is HomePacienteViewModel.NomeState.Success -> tvSaudacao.text = "Olá, ${state.primeiroNome}"
                    is HomePacienteViewModel.NomeState.Error   -> tvSaudacao.text = "Olá, Usuario Não encontrado"
                }
            }
        }

        lifecycleScope.launch {
            viewModel.consultasState.collect { state ->
                when (state) {
                    is HomePacienteViewModel.ConsultasState.Idle    -> Unit
                    is HomePacienteViewModel.ConsultasState.Loading -> Unit
                    is HomePacienteViewModel.ConsultasState.Success -> adaptadorConsultas.atualizarLista(state.consultas)
                    is HomePacienteViewModel.ConsultasState.Error   -> handleError(state.error)
                }
            }
        }
    }
}
