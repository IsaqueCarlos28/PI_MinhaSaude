package com.example.medicoapplication.activities.paciente

import android.content.Intent
import android.os.Bundle
import android.widget.GridView
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.adapters.ConsultasPacienteAdapter
import com.example.medicoapplication.viewmodel.ConsultaViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MinhasConsultasActivity : AppCompatActivity() {

    private val viewModel: ConsultaViewModel by viewModels()

    private lateinit var adapter : ConsultasPacienteAdapter
    private val calendar = Calendar.getInstance()
    private val formatoMesAno    = SimpleDateFormat("MMMM, yyyy", Locale("pt", "BR"))
    private val formatoDataLabel = SimpleDateFormat("EEEE, dd 'de' MMMM", Locale("pt", "BR"))

    private lateinit var tvMesAno: TextView
    private lateinit var tvDataSelecionada:TextView
    private lateinit var btnMesAnterior: ImageButton
    private lateinit var btnProximoMes: ImageButton
    private lateinit var gridCalendario : GridView
    private lateinit var rvConsultas: RecyclerView
    // ID do paciente recebido pelo Intent
    private var idPaciente: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minhas_consultas)

        idPaciente = intent.getLongExtra("ID_PACIENTE", -1L)

        bindViews()
        rvConsultas.layoutManager = LinearLayoutManager(this)
        // TODO: criar ConsultasPacienteAdapter e conectar aqui filtrando por dia selecionado
        adapter = ConsultasPacienteAdapter(
            emptyList(),
            {reagendarConsulta()},
            {cancelarConsulta()}
        )

        atualizarCalendario()

        btnMesAnterior.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            atualizarCalendario()
        }

        btnProximoMes.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            atualizarCalendario()
        }

        configurarBottomNav(R.id.nav_consultas)
    }

    fun bindViews(){
        tvMesAno          = findViewById(R.id.tvMesAno)
        tvDataSelecionada  = findViewById(R.id.tvDataSelecionada)
        // CORRIGIDO: ID real no XML e btnMesAnterior e btnProximoMes (nao existe btnVoltarConsultas)
        btnMesAnterior     = findViewById(R.id.btnMesAnterior)
        btnProximoMes      = findViewById(R.id.btnProximoMes)
        gridCalendario     = findViewById(R.id.gridViewCalendario)
        rvConsultas        = findViewById(R.id.rvConsultasDoDia)
    }

    private fun atualizarCalendario(
    ) {
        tvMesAno.text = formatoMesAno.format(calendar.time)
            .replaceFirstChar { it.uppercase() }

        tvDataSelecionada.text = formatoDataLabel.format(calendar.time)
            .replaceFirstChar { it.uppercase() }

        val calTemp = calendar.clone() as Calendar
        calTemp.set(Calendar.DAY_OF_MONTH, 1)
        val primeiroDiaSemana = calTemp.get(Calendar.DAY_OF_WEEK) - 1
        val totalDias = calTemp.getActualMaximum(Calendar.DAY_OF_MONTH)

        val dias = mutableListOf<String>()
        repeat(primeiroDiaSemana) { dias.add("") }
        for (d in 1..totalDias) dias.add(d.toString())

        // TODO: passar lista de datas com consultas para o adapter marcar pontos
        // gridCalendario.adapter = CalendarioAdapter(this, dias, datasComConsulta) { dia -> ... }
    }

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavConsultas)
        bottomNav.selectedItemId = itemSelecionado

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(
                        Intent(this, HomePacienteActivity::class.java).apply {
                            putExtra("ID_PACIENTE", idPaciente)
                        }
                    )
                    false
                }
                R.id.nav_consultas -> true
                R.id.nav_medicos   -> {
                    startActivity(Intent(this, BuscaMedicosActivity::class.java))
                    false
                }
                R.id.nav_perfil    -> {
                    startActivity(
                        Intent(this, PerfilPacienteActivity::class.java).apply {
                            putExtra("ID_PACIENTE", idPaciente)
                        }
                    )
                    false
                }
                else -> false
            }
        }
    }

    private fun cancelarConsulta(){
        //TODO: função cancelar consulta
    }

    private fun reagendarConsulta(){
        //TODO: função reagendar consulta
    }

}
