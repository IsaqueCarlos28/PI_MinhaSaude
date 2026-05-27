package com.example.medicoapplication.activities.medico

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.activities.medico.viewmodel.ConsultasMedicoViewModel
import com.example.medicoapplication.adapters.ConsultasMedicoAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class ConsultasMedicoActivity : AppCompatActivity() {

    private val viewModel: ConsultasMedicoViewModel by viewModels()
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
        adapter = ConsultasMedicoAdapter(emptyList())
        rv.adapter = adapter

        // Botões de filtro
        val btnTodas      = findViewById<Button>(R.id.btnFiltroTodas)
        val btnAgendadas  = findViewById<Button>(R.id.btnFiltroAgendadas)
        val btnRealizadas = findViewById<Button>(R.id.btnFiltroRealizadas)
        val btnCanceladas = findViewById<Button>(R.id.btnFiltroCanceladas)
        val botoes = listOf(btnTodas, btnAgendadas, btnRealizadas, btnCanceladas)

        fun destacar(ativo: Button) {
            botoes.forEach { it.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF94A3B8.toInt()) }
            ativo.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF3B82F6.toInt())
        }

        btnTodas.setOnClickListener      { destacar(btnTodas);      viewModel.carregarConsultas(idMedico, null) }
        btnAgendadas.setOnClickListener  { destacar(btnAgendadas);  viewModel.carregarConsultas(idMedico, "AGENDADA") }
        btnRealizadas.setOnClickListener { destacar(btnRealizadas); viewModel.carregarConsultas(idMedico, "REALIZADA") }
        btnCanceladas.setOnClickListener { destacar(btnCanceladas); viewModel.carregarConsultas(idMedico, "CANCELADA") }

        observeViewModel()

        if (idMedico != -1L) {
            destacar(btnTodas)
            viewModel.carregarConsultas(idMedico, null)
        }

        configurarBottomNav(R.id.nav_consultas_med)
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
                        Toast.makeText(this@ConsultasMedicoActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavConsultasMedico)
        bottomNav.selectedItemId = itemSelecionado
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio        -> { startActivity(Intent(this, HomeMedicoActivity::class.java).apply { putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico) }); false }
                R.id.nav_agenda        -> { startActivity(Intent(this, AgendaMedicoActivity::class.java).apply { putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico) }); false }
                R.id.nav_consultas_med -> true
                R.id.nav_usuario       -> { startActivity(Intent(this, PerfilMedicoActivity::class.java).apply { putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico) }); false }
                R.id.nav_config        -> { startActivity(Intent(this, ConfiguracoesMedicoActivity::class.java).apply { putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico) }); false }
                else -> false
            }
        }
    }
}
