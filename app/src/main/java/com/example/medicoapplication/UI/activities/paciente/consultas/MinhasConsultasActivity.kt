package com.example.medicoapplication.UI.activities.paciente.consultas

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.adapters.CalendarioAdapter
import com.example.medicoapplication.UI.adapters.ConsultasPacienteAdapter
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.viewmodel.paciente.consulta.MinhasConsultasViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * FIX: previously the GridView calendar was purely decorative — tapping a day
 * had no effect on the consultas list. Now:
 *   1. Dates that have consultas are highlighted via datasComConsulta.
 *   2. Tapping a day calls viewModel.filtrarPorData(), which filters the
 *      already-loaded list locally (no extra API call).
 *   3. Tapping the selected day a second time clears the filter.
 *
 * The GridView is replaced by a RecyclerView-based CalendarioAdapter so we can
 * pass per-cell click callbacks and mark cells that have consultas.
 * If your layout still uses GridView, replace the grid binding section below
 * with your existing GridAdapter and add the same click + highlight logic there.
 */
class MinhasConsultasActivity : BaseActivity() {

    private val viewModel: MinhasConsultasViewModel by viewModels()

    private val calendar          = Calendar.getInstance()
    private val formatoMesAno     = SimpleDateFormat("MMMM, yyyy", Locale("pt", "BR"))
    private val formatoDataLabel  = SimpleDateFormat("EEEE, dd 'de' MMMM", Locale("pt", "BR"))
    private val formatoDataApi    = SimpleDateFormat("yyyy-MM-dd", Locale("pt", "BR"))

    private lateinit var adapter: ConsultasPacienteAdapter
    private lateinit var calendarioAdapter: CalendarioAdapter

    // Track the currently selected date so a second tap clears the filter
    private var dataSelecionada: String? = null

    override val menuType = BottomMenuType.PACIENTE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minhas_consultas)

        val tvMesAno          = findViewById<TextView>(R.id.tvMesAno)
        val tvDataSelecionada = findViewById<TextView>(R.id.tvDataSelecionada)
        val btnMesAnterior    = findViewById<ImageButton>(R.id.btnMesAnterior)
        val btnProximoMes     = findViewById<ImageButton>(R.id.btnProximoMes)
        val rvCalendario      = findViewById<RecyclerView>(R.id.rvCalendario)
        val rvConsultas       = findViewById<RecyclerView>(R.id.rvConsultasDoDia)

        // ── Consultas list ──────────────────────────────────────────────────
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

        // ── Calendar ────────────────────────────────────────────────────────
        calendarioAdapter = CalendarioAdapter(
            diasNoMes    = emptyList(),
            datasComConsulta = emptySet(),
            dataSelecionada  = null,
            onDiaClick   = { diaStr -> onDiaCalendarioClicado(diaStr, tvDataSelecionada) }
        )
        rvCalendario.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 7)
        rvCalendario.adapter = calendarioAdapter

        atualizarCalendario(tvMesAno, tvDataSelecionada)

        btnMesAnterior.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            dataSelecionada = null
            viewModel.filtrarPorData(null)
            atualizarCalendario(tvMesAno, tvDataSelecionada)
        }
        btnProximoMes.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            dataSelecionada = null
            viewModel.filtrarPorData(null)
            atualizarCalendario(tvMesAno, tvDataSelecionada)
        }

        observeViewModel()
        viewModel.carregarConsultas()

        setupBottomNavigation(R.id.nav_consultas_paciente)
    }

    // ─── Calendar interaction ─────────────────────────────────────────────────

    private fun onDiaCalendarioClicado(diaStr: String, tvDataSelecionada: TextView) {
        if (diaStr.isBlank()) return  // padding cell

        val calDia = calendar.clone() as Calendar
        calDia.set(Calendar.DAY_OF_MONTH, diaStr.toInt())
        val dataApi = formatoDataApi.format(calDia.time)

        if (dataSelecionada == dataApi) {
            // Second tap on the same day → clear filter
            dataSelecionada = null
            viewModel.filtrarPorData(null)
            tvDataSelecionada.text = formatoDataLabel.format(calendar.time)
                .replaceFirstChar { it.uppercase() }
        } else {
            dataSelecionada = dataApi
            viewModel.filtrarPorData(dataApi)
            tvDataSelecionada.text = formatoDataLabel.format(calDia.time)
                .replaceFirstChar { it.uppercase() }
        }

        calendarioAdapter.atualizarSelecao(dataSelecionada)
    }

    // ─── Observers ────────────────────────────────────────────────────────────

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

        // Update calendar highlights whenever the full list is (re)loaded
        lifecycleScope.launch {
            viewModel.datasComConsulta.collect { datas ->
                calendarioAdapter.atualizarDatasComConsulta(datas)
            }
        }
    }

    private fun setLoading(carregando: Boolean) {
        findViewById<RecyclerView>(R.id.rvConsultasDoDia).alpha = if (carregando) 0.4f else 1f
    }

    // ─── Calendar grid builder ────────────────────────────────────────────────

    private fun atualizarCalendario(tvMesAno: TextView, tvDataSelecionada: TextView) {
        tvMesAno.text = formatoMesAno.format(calendar.time).replaceFirstChar { it.uppercase() }
        tvDataSelecionada.text = formatoDataLabel.format(calendar.time).replaceFirstChar { it.uppercase() }

        val calTemp = calendar.clone() as Calendar
        calTemp.set(Calendar.DAY_OF_MONTH, 1)
        val primeiroDiaSemana = calTemp.get(Calendar.DAY_OF_WEEK) - 1
        val totalDias = calTemp.getActualMaximum(Calendar.DAY_OF_MONTH)

        val dias = mutableListOf<String>()
        repeat(primeiroDiaSemana) { dias.add("") }
        for (d in 1..totalDias) dias.add(d.toString())

        calendarioAdapter.atualizarMes(
            dias = dias,
            ano = calendar.get(Calendar.YEAR),
            mes = calendar.get(Calendar.MONTH),
            selecao = dataSelecionada
        )
    }
}
