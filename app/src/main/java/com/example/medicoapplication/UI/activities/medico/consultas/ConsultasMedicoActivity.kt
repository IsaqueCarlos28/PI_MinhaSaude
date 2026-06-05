package com.example.medicoapplication.UI.activities.medico.consultas

import ConsultasMedicoAdapter
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.viewmodel.medico.consulta.ConsultasMedicoViewModel
import kotlinx.coroutines.launch

class ConsultasMedicoActivity : BaseActivity() {

    private lateinit var viewModel: ConsultasMedicoViewModel
    private lateinit var adapter: ConsultasMedicoAdapter

    private var idMedico: Long = -1L
    private var nomeMedico: String = "Médico"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultas_medico)

        nomeMedico = intent.getStringExtra("NOME_MEDICO") ?: "Médico"
        idMedico   = intent.getLongExtra("ID_MEDICO", -1L)

        findViewById<TextView>(R.id.tvSaudacaoConsultas).text = "Olá, $nomeMedico"

        // Configurar RecyclerView
        val rv = findViewById<RecyclerView>(R.id.rvConsultasMedico)
        rv.layoutManager = LinearLayoutManager(this)
        //Mandar para uma pagina separada da consulta
        adapter = ConsultasMedicoAdapter(emptyList(),{irParaTelaConsulta()})
        rv.adapter = adapter

        // Botões de filtro
        val btnTodas      = findViewById<Button>(R.id.btnFiltroTodas)
        val btnAgendadas  = findViewById<Button>(R.id.btnFiltroAgendadas)
        val btnRealizadas = findViewById<Button>(R.id.btnFiltroRealizadas)
        val btnCanceladas = findViewById<Button>(R.id.btnFiltroCanceladas)
        val botoes = listOf(btnTodas, btnAgendadas, btnRealizadas, btnCanceladas)

        fun destacar(ativo: Button) {
            botoes.forEach { it.backgroundTintList = ColorStateList.valueOf(0xFF94A3B8.toInt()) }
            ativo.backgroundTintList = ColorStateList.valueOf(0xFF3B82F6.toInt())
        }

        //Arrumar filtro depois
//        btnTodas.setOnClickListener      { destacar(btnTodas);      viewModel.carregarConsultas( null) }
//        btnAgendadas.setOnClickListener  { destacar(btnAgendadas);  viewModel.carregarConsultas( "AGENDADA") }
//        btnRealizadas.setOnClickListener { destacar(btnRealizadas); viewModel.carregarConsultas( "REALIZADA") }
//        btnCanceladas.setOnClickListener { destacar(btnCanceladas); viewModel.carregarConsultas( "CANCELADA") }

        observeViewModel()

        if (idMedico != -1L) {
            destacar(btnTodas)
            viewModel.carregarConsultas()
        }

        setupBottomNavigation(R.id.nav_consultas_medico)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ConsultasMedicoViewModel.UiState.Idle    -> Unit
                    is ConsultasMedicoViewModel.UiState.Loading -> Unit
                    is ConsultasMedicoViewModel.UiState.Success -> {
                        adapter.atualizarLista(state.consultas)
                    }
                    is ConsultasMedicoViewModel.UiState.Error -> {
                       handleError(state.error)
                    }
                }
            }
        }
    }


    fun irParaTelaConsulta(){
        val intent = Intent(
            this@ConsultasMedicoActivity,
            VisualisarConsultaMedico::class.java)
        startActivity(intent)
    }
}
