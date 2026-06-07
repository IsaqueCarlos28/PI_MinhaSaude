package com.example.medicoapplication.UI.activities.medico

import ConsultasMedicoAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.activities.medico.agenda.AgendaMedicoActivity
import com.example.medicoapplication.UI.activities.medico.consultas.ConsultasMedicoActivity
import com.example.medicoapplication.UI.activities.medico.consultas.VisualisarConsultaMedico
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.viewmodel.medico.consulta.ConsultasMedicoViewModel
import kotlinx.coroutines.launch

class HomeMedicoActivity : BaseActivity() {

    private val viewModel: ConsultasMedicoViewModel by viewModels()
    private lateinit var adapter: ConsultasMedicoAdapter

    override val menuType = BottomMenuType.MEDICO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_medico)

        setupRecyclerView()
        configurarAtalhos()
        observeViewModel()
        setupBottomNavigation(R.id.nav_inicio_medico)

        viewModel.carregarConsultas()
    }

    private fun setupRecyclerView() {
        adapter = ConsultasMedicoAdapter(
            consultas   = emptyList(),
            onItemClick = { consulta ->
                startActivity(
                    Intent(this, VisualisarConsultaMedico::class.java).apply {
                        putExtra("ID_EVENTO", consulta.id)
                    }
                )
            }
        )
        findViewById<RecyclerView>(R.id.rvProximasConsultasMedico).apply {
            layoutManager = LinearLayoutManager(this@HomeMedicoActivity)
            adapter       = this@HomeMedicoActivity.adapter
        }
    }

    private fun configurarAtalhos() {
        findViewById<LinearLayout>(R.id.btnAcaoAgenda).setOnClickListener {
            startActivity(Intent(this, AgendaMedicoActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.btnAcaoConsultasMedico).setOnClickListener {
            startActivity(Intent(this, ConsultasMedicoActivity::class.java))
        }
        findViewById<Button>(R.id.btnVerConsultasMedico).setOnClickListener {
            startActivity(Intent(this, ConsultasMedicoActivity::class.java))
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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
    }

    private fun setLoading(isLoading: Boolean) {
        findViewById<RecyclerView>(R.id.rvProximasConsultasMedico).alpha =
            if (isLoading) 0.4f else 1f
    }
}
