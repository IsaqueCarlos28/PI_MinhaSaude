package com.example.medicoapplication.UI.activities.medico.consultas

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.adapters.ConsultasMedicoAdapter
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.data.remote.DTO.StatusConsulta
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.viewmodel.medico.consulta.ConsultasMedicoViewModel
import kotlinx.coroutines.launch

class ConsultasMedicoActivity : BaseActivity() {

    private val viewModel: ConsultasMedicoViewModel by viewModels()
    private lateinit var adapter: ConsultasMedicoAdapter

    override val menuType = BottomMenuType.DISABLED

    private lateinit var btnTodas: Button
    private lateinit var btnAgendadas: Button
    private lateinit var btnRealizadas: Button
    private lateinit var btnCanceladas: Button
    private lateinit var tvVazio: TextView
    private lateinit var rvConsultas: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultas_medico)

        bindViews()
        configurarRecyclerView()
        configurarFiltros()
        observeViewModel()

        viewModel.carregarConsultas()
    }

    private fun bindViews() {
        btnTodas      = findViewById(R.id.btnFiltroTodas)
        btnAgendadas  = findViewById(R.id.btnFiltroAgendadas)
        btnRealizadas = findViewById(R.id.btnFiltroRealizadas)
        btnCanceladas = findViewById(R.id.btnFiltroCanceladas)
        tvVazio       = findViewById(R.id.tvConsultasMedicoVazio)
        rvConsultas   = findViewById(R.id.rvConsultasMedico)
    }

    private fun configurarRecyclerView() {
        rvConsultas.layoutManager = LinearLayoutManager(this)
        adapter = ConsultasMedicoAdapter(emptyList()) { consulta ->
            abrirDetalheConsulta(consulta)
        }
        rvConsultas.adapter = adapter
    }

    private fun configurarFiltros() {
        val botoes = listOf(btnTodas, btnAgendadas, btnRealizadas, btnCanceladas)

        fun destacar(ativo: Button) {
            botoes.forEach {
                it.backgroundTintList = ColorStateList.valueOf(0xFF94A3B8.toInt())
            }
            ativo.backgroundTintList = ColorStateList.valueOf(0xFF3B82F6.toInt())
        }
//
//        btnTodas.setOnClickListener {
//            destacar(btnTodas)
//            viewModel.filtrar(null)
//        }
//        btnAgendadas.setOnClickListener {
//            destacar(btnAgendadas)
//            viewModel.filtrar(StatusConsulta.AGENDADA)
//        }
//        btnRealizadas.setOnClickListener {
//            destacar(btnRealizadas)
//            viewModel.filtrar(StatusConsulta.REALIZADA)
//        }
//        btnCanceladas.setOnClickListener {
//            destacar(btnCanceladas)
//            viewModel.filtrar(StatusConsulta.CANCELADA)
//        }

        // Inicia com "Todas" selecionado
        destacar(btnTodas)
        setupBottomNavigation(R.id.nav_agenda_medico)
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
                        tvVazio.visibility =
                            if (state.consultas.isEmpty()) View.VISIBLE else View.GONE
                    }
                    is ConsultasMedicoViewModel.UiState.Error -> {
                        setLoading(false)
                        handleError(state.error)
                    }
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        rvConsultas.alpha = if (isLoading) 0.4f else 1f
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
