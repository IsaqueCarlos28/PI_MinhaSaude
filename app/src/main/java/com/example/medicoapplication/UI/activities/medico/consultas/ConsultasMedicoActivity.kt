package com.example.medicoapplication.UI.activities.medico.consultas

import ConsultasMedicoAdapter
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.viewmodel.medico.consulta.ConsultasMedicoViewModel
import kotlinx.coroutines.launch

class ConsultasMedicoActivity : BaseActivity() {

    private val viewModel: ConsultasMedicoViewModel by viewModels()
    private lateinit var adapter: ConsultasMedicoAdapter

    override val menuType = BottomMenuType.MEDICO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultas_medico)

        val rv = findViewById<RecyclerView>(R.id.rvConsultasMedico)
        rv.layoutManager = LinearLayoutManager(this)

        adapter = ConsultasMedicoAdapter(emptyList()) { consulta ->
            abrirDetalheConsulta(consulta)
        }
        rv.adapter = adapter

        val btnTodas      = findViewById<Button>(R.id.btnFiltroTodas)
        val btnAgendadas  = findViewById<Button>(R.id.btnFiltroAgendadas)
        val btnRealizadas = findViewById<Button>(R.id.btnFiltroRealizadas)
        val btnCanceladas = findViewById<Button>(R.id.btnFiltroCanceladas)
        val botoes = listOf(btnTodas, btnAgendadas, btnRealizadas, btnCanceladas)

        fun destacar(ativo: Button) {
            botoes.forEach { it.backgroundTintList = ColorStateList.valueOf(0xFF94A3B8.toInt()) }
            ativo.backgroundTintList = ColorStateList.valueOf(0xFF3B82F6.toInt())
        }

        // TODO: implementar filtros por status quando a feature estiver pronta
        //btnTodas.setOnClickListener      { destacar(btnTodas);      viewModel.carregarConsultas(null) }
        //btnAgendadas.setOnClickListener  { destacar(btnAgendadas);  viewModel.carregarConsultas("AGENDADA") }
        //btnRealizadas.setOnClickListener { destacar(btnRealizadas); viewModel.carregarConsultas("REALIZADA") }
        //btnCanceladas.setOnClickListener { destacar(btnCanceladas); viewModel.carregarConsultas("CANCELADA") }

        observeViewModel()

        destacar(btnTodas)
        viewModel.carregarConsultas()

        setupBottomNavigation(R.id.nav_consultas_medico)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ConsultasMedicoViewModel.UiState.Idle    -> setLoading(false)
                    is ConsultasMedicoViewModel.UiState.Loading -> setLoading(true)
                    is ConsultasMedicoViewModel.UiState.Success -> {
                        setLoading(false)
                        adapter.atualizarLista(state.consultas)
                    }
                    is ConsultasMedicoViewModel.UiState.Error   -> {
                        setLoading(false)
                        handleError(state.error)
                    }
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        findViewById<RecyclerView>(R.id.rvConsultasMedico).alpha =
            if (isLoading) 0.4f else 1f
    }

    private fun abrirDetalheConsulta(consulta: ConsultaResponseDto) {
        startActivity(
            Intent(this, VisualisarConsultaMedico::class.java).apply {
                putExtra("ID_EVENTO",     consulta.id)
                putExtra("NOME_PACIENTE", consulta.nomePaciente ?: "Paciente")
            }
        )
    }
}
