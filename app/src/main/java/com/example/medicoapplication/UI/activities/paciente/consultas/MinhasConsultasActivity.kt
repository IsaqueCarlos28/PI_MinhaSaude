package com.example.medicoapplication.UI.activities.paciente.consultas

import android.content.Intent
import android.os.Bundle
import android.widget.GridView
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.adapters.ConsultasPacienteAdapter
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.viewmodel.paciente.consulta.MinhasConsultasViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MinhasConsultasActivity : BaseActivity() {

    private val viewModel: MinhasConsultasViewModel by viewModels()

    private val calendar = Calendar.getInstance()
    private val formatoMesAno    = SimpleDateFormat("MMMM, yyyy", Locale("pt", "BR"))
    private val formatoDataLabel = SimpleDateFormat("EEEE, dd 'de' MMMM", Locale("pt", "BR"))

    private lateinit var adapter: ConsultasPacienteAdapter

    override val menuType = BottomMenuType.PACIENTE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minhas_consultas)

        val tvMesAno         = findViewById<TextView>(R.id.tvMesAno)
        val tvDataSelecionada = findViewById<TextView>(R.id.tvDataSelecionada)
        val btnMesAnterior   = findViewById<ImageButton>(R.id.btnMesAnterior)
        val btnProximoMes    = findViewById<ImageButton>(R.id.btnProximoMes)
        val gridCalendario   = findViewById<GridView>(R.id.gridViewCalendario)
        val rvConsultas      = findViewById<RecyclerView>(R.id.rvConsultasDoDia)

        adapter = ConsultasPacienteAdapter(
            consultas = emptyList(),
            onItemClick = { consulta ->
                startActivity(
                    Intent(this, DetalheConsultaActivity::class.java).apply {
                        putExtra("ID_EVENTO", consulta.id)
                    }
                )
            }
        )
        rvConsultas.layoutManager = LinearLayoutManager(this)
        rvConsultas.adapter = adapter

        atualizarCalendario(tvMesAno, tvDataSelecionada, gridCalendario)

        btnMesAnterior.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            atualizarCalendario(tvMesAno, tvDataSelecionada, gridCalendario)
        }
        btnProximoMes.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            atualizarCalendario(tvMesAno, tvDataSelecionada, gridCalendario)
        }

        observeViewModel()
        viewModel.carregarConsultas()

        setupBottomNavigation(R.id.nav_consultas_paciente)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is MinhasConsultasViewModel.UiState.Idle    -> setLoading(false)
                    is MinhasConsultasViewModel.UiState.Loading -> setLoading(true)
                    is MinhasConsultasViewModel.UiState.Success -> {
                        setLoading(false)
                        adapter.atualizarLista(state.consultas)
                    }
                    is MinhasConsultasViewModel.UiState.Error   -> {
                        setLoading(false)
                        handleError(state.error)
                    }
                }
            }
        }
    }

    private fun setLoading(carregando: Boolean) {
        findViewById<RecyclerView>(R.id.rvConsultasDoDia).alpha = if (carregando) 0.4f else 1f
    }

    private fun atualizarCalendario(
        tvMesAno: TextView,
        tvDataSelecionada: TextView,
        gridCalendario: GridView
    ) {
        tvMesAno.text = formatoMesAno.format(calendar.time).replaceFirstChar { it.uppercase() }
        tvDataSelecionada.text = formatoDataLabel.format(calendar.time).replaceFirstChar { it.uppercase() }

        val calTemp = calendar.clone() as Calendar
        calTemp.set(Calendar.DAY_OF_MONTH, 1)
        val primeiroDiaSemana = calTemp.get(Calendar.DAY_OF_WEEK) - 1
        val totalDias = calTemp.getActualMaximum(Calendar.DAY_OF_MONTH)

        val dias = mutableListOf<String>()
        repeat(primeiroDiaSemana) { dias.add("") }
        for (d in 1..totalDias) dias.add(d.toString())
    }
}
