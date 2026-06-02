package com.example.medicoapplication.activities.paciente

import android.content.Intent
import android.os.Bundle
import android.widget.GridView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.adapters.ConsultasPacienteAdapter
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.viewmodel.paciente.consulta.MinhasConsultasViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MinhasConsultasActivity : AppCompatActivity() {

    private val viewModel: MinhasConsultasViewModel by viewModels()

    private val calendar = Calendar.getInstance()
    private val formatoMesAno    = SimpleDateFormat("MMMM, yyyy", Locale("pt", "BR"))
    private val formatoDataLabel = SimpleDateFormat("EEEE, dd 'de' MMMM", Locale("pt", "BR"))

    private var idPaciente: Long = -1L
    private lateinit var adapter: ConsultasPacienteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minhas_consultas)

        idPaciente = intent.getLongExtra("ID_PACIENTE", -1L)

        val tvMesAno         = findViewById<TextView>(R.id.tvMesAno)
        val tvDataSelecionada = findViewById<TextView>(R.id.tvDataSelecionada)
        val btnMesAnterior   = findViewById<ImageButton>(R.id.btnMesAnterior)
        val btnProximoMes    = findViewById<ImageButton>(R.id.btnProximoMes)
        val gridCalendario   = findViewById<GridView>(R.id.gridViewCalendario)
        val rvConsultas      = findViewById<RecyclerView>(R.id.rvConsultasDoDia)

        // Adapter com clique → abre DetalheConsultaActivity
        adapter = ConsultasPacienteAdapter(
            consultas   = emptyList(),
            onItemClick = { consulta ->
                startActivity(
                    Intent(this, DetalheConsultaActivity::class.java).apply {
                        putExtra("ID_PACIENTE", idPaciente)
                        putExtra("ID_EVENTO",   consulta.id)
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
        if (idPaciente != -1L) viewModel.carregarConsultas(idPaciente)

        configurarBottomNav(R.id.nav_consultas)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is MinhasConsultasViewModel.UiState.Idle    -> Unit
                    is MinhasConsultasViewModel.UiState.Loading -> Unit
                    is MinhasConsultasViewModel.UiState.Success -> adapter.atualizarLista(state.consultas)
                    is MinhasConsultasViewModel.UiState.Error   -> {
                        val mensagem = when (state.error) {
                            is NetworkError.NaoAutorizado ->
                                "Email ou senha incorretos. Verifique seus dados."
                            is NetworkError.SemConexao ->
                                "Sem conexão com a internet. Verifique sua rede."
                            is NetworkError.Timeout ->
                                "O servidor demorou para responder. Tente novamente."
                            is NetworkError.ErrroServidor ->
                                "Problema no servidor. Tente mais tarde."
                            is NetworkError.Desconhecido ->
                                "Erro inesperado: ${state.error.mensagem}"
                            else ->
                                "Algo deu errado. Tente novamente."
                        }
                        Toast.makeText(this@MinhasConsultasActivity, mensagem, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
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

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavConsultas)
        bottomNav.selectedItemId = itemSelecionado
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home      -> { startActivity(Intent(this, HomePacienteActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                R.id.nav_consultas -> true
                R.id.nav_medicos   -> { startActivity(Intent(this, BuscaMedicosActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                R.id.nav_perfil    -> { startActivity(Intent(this, PerfilPacienteActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                else -> false
            }
        }
    }
}
