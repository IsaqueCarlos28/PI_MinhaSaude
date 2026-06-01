package com.example.medicoapplication.activities.medico

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.adapters.ConsultaOfertadaAdapter
import com.example.medicoapplication.viewmodel.medico.consulta.ConsultaOfertadaViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class ConsultasOfertadasActivity : AppCompatActivity() {

    private val viewModel: ConsultaOfertadaViewModel by viewModels()
    private var idMedico = -1L
    private var nomeMedico = "Médico"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultas_ofertadas)

        idMedico = intent.getLongExtra("ID_MEDICO", -1L)
        nomeMedico = intent.getStringExtra("NOME_MEDICO") ?: "Médico"

        val rv = findViewById<RecyclerView>(R.id.rvConsultasOfertadas)
        val tvVazio = findViewById<TextView>(R.id.tvConsultasOfertadasVazio)
        val fab = findViewById<FloatingActionButton>(R.id.fabNovaConsultaOfertada)

        rv.layoutManager = LinearLayoutManager(this)

        val adapter = ConsultaOfertadaAdapter { consulta ->
            AlertDialog.Builder(this)
                .setTitle("Excluir consulta?")
                .setMessage("Deseja remover a consulta de ${consulta.especialidade?.nome ?: "especialidade"}?")
                .setPositiveButton("Excluir") { _, _ ->
                    viewModel.deletarConsultaOfertada(idMedico, consulta.id)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
        rv.adapter = adapter

        lifecycleScope.launch {
            viewModel.consultas.collect { lista ->
                adapter.submitList(lista)
                tvVazio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
                rv.visibility = if (lista.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ConsultaOfertadaViewModel.UiState.Error ->
                        Toast.makeText(this@ConsultasOfertadasActivity, state.message, Toast.LENGTH_LONG).show()
                    else -> {}
                }
            }
        }

        fab.setOnClickListener {
            startActivity(Intent(this, CriarConsultaOfertadaActivity::class.java).apply {
                putExtra("ID_MEDICO", idMedico)
                putExtra("NOME_MEDICO", nomeMedico)
            })
        }

        configurarBottomNav()
        viewModel.carregarConsultas(idMedico)
    }

    override fun onResume() {
        super.onResume()
        viewModel.carregarConsultas(idMedico)
    }

    private fun configurarBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavConsultasOfertadas)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    startActivity(Intent(this, HomeMedicoActivity::class.java).apply {
                        putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico)
                    }); false
                }
                R.id.nav_agenda -> {
                    startActivity(Intent(this, AgendaMedicoActivity::class.java).apply {
                        putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico)
                    }); false
                }
                R.id.nav_consultas_med -> {
                    startActivity(Intent(this, ConsultasMedicoActivity::class.java).apply {
                        putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico)
                    }); false
                }
                R.id.nav_usuario -> {
                    startActivity(Intent(this, PerfilMedicoActivity::class.java).apply {
                        putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico)
                    }); false
                }
                R.id.nav_config -> {
                    startActivity(Intent(this, ConfiguracoesMedicoActivity::class.java).apply {
                        putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico)
                    }); false
                }
                else -> false
            }
        }
    }
}
