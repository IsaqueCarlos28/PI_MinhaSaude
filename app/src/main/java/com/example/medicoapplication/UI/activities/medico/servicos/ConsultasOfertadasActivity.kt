package com.example.medicoapplication.UI.activities.medico.servicos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.adapters.ConsultaOfertadaAdapter
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.viewmodel.medico.consulta.ConsultaOfertadaViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class ConsultasOfertadasActivity : BaseActivity() {

    private val viewModel: ConsultaOfertadaViewModel by viewModels()

    override val menuType = BottomMenuType.MEDICO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultas_ofertadas)

        val rv = findViewById<RecyclerView>(R.id.rvConsultasOfertadas)
        val tvVazio = findViewById<TextView>(R.id.tvConsultasOfertadasVazio)
        val fab = findViewById<FloatingActionButton>(R.id.fabNovaConsultaOfertada)

        rv.layoutManager = LinearLayoutManager(this)

        val adapter = ConsultaOfertadaAdapter { consulta ->
            AlertDialog.Builder(this)
                .setTitle("Excluir consulta?")
                .setMessage("Deseja remover a consulta de ${consulta.especialidade?.nome ?: "especialidade"}?")
                .setPositiveButton("Excluir") { _, _ ->
                    viewModel.deletarConsultaOfertada(consulta.id)
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
                    is ConsultaOfertadaViewModel.UiState.Error -> handleError(state.error)
                    else -> {}
                }
            }
        }

        fab.setOnClickListener {
            startActivity(Intent(this, CriarConsultaOfertadaActivity::class.java))
        }

        setupBottomNavigation(R.id.nav_servicos_medico)
        viewModel.carregarConsultasOfertadas()
    }

    override fun onResume() {
        super.onResume()
        viewModel.carregarConsultasOfertadas()
    }
}
