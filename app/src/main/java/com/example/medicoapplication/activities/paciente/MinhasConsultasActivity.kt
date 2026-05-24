package com.example.medicoapplication.activities.paciente


import android.content.Intent
import android.os.Bundle
import android.widget.GridView
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MinhasConsultasActivity : AppCompatActivity() {

    private val calendar = Calendar.getInstance()
    private val formatoMesAno = SimpleDateFormat("MMMM, yyyy", Locale("pt", "BR"))
    private val formatoDataLabel = SimpleDateFormat("EEEE, dd 'de' MMMM", Locale("pt", "BR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minhas_consultas)

        val tvMesAno           = findViewById<TextView>(R.id.tvMesAno)
        val tvDataSelecionada  = findViewById<TextView>(R.id.tvDataSelecionada)
        val btnMesAnterior     = findViewById<ImageButton>(R.id.btnMesAnterior)
        val btnProximoMes      = findViewById<ImageButton>(R.id.btnProximoMes)
        val gridCalendario     = findViewById<GridView>(R.id.gridViewCalendario)
        val btnVoltar          = findViewById<ImageButton>(R.id.btnVoltarConsultas)
        val rvConsultas        = findViewById<RecyclerView>(R.id.rvConsultasDoDia)

        rvConsultas.layoutManager = LinearLayoutManager(this)
        // TODO: carregar consultas do dia via API na semana que vem
        // rvConsultas.adapter = ConsultasPacienteAdapter(listaFiltradaPorDia) { ... }

        btnVoltar.setOnClickListener { finish() }

        atualizarCalendario(tvMesAno, tvDataSelecionada, gridCalendario)

        btnMesAnterior.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            atualizarCalendario(tvMesAno, tvDataSelecionada, gridCalendario)
        }

        btnProximoMes.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            atualizarCalendario(tvMesAno, tvDataSelecionada, gridCalendario)
        }

        configurarBottomNav(R.id.nav_consultas)
    }

    private fun atualizarCalendario(
        tvMesAno: TextView,
        tvDataSelecionada: TextView,
        gridCalendario: GridView
    ) {
        tvMesAno.text = formatoMesAno.format(calendar.time)
            .replaceFirstChar { it.uppercase() }

        tvDataSelecionada.text = formatoDataLabel.format(calendar.time)
            .replaceFirstChar { it.uppercase() }

        // Calcular dias do mês para popular o grid
        val calTemp = calendar.clone() as Calendar
        calTemp.set(Calendar.DAY_OF_MONTH, 1)
        val primeiroDiaSemana = calTemp.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Dom
        val totalDias = calTemp.getActualMaximum(Calendar.DAY_OF_MONTH)

        val dias = mutableListOf<String>()
        repeat(primeiroDiaSemana) { dias.add("") } // células vazias antes do dia 1
        for (d in 1..totalDias) dias.add(d.toString())

        // TODO: passar lista de datas com consultas para o adapter marcar pontos azuis
        // gridCalendario.adapter = CalendarioAdapter(this, dias, datasComConsulta) { dia -> ... }
    }

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavConsultas)
        bottomNav.selectedItemId = itemSelecionado

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomePacienteActivity::class.java))
                    false
                }
                R.id.nav_consultas -> true
                R.id.nav_medicos -> {
                    startActivity(Intent(this, BuscaMedicosActivity::class.java))
                    false
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, PerfilPacienteActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }
}
