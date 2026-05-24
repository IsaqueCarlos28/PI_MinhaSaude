package com.example.medicoapplication.activities.paciente

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.activities.auth_e_cadastro.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomePacienteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_paciente)

        // Saudação com nome do paciente (virá do login futuramente)
        val tvSaudacao = findViewById<TextView>(R.id.tvSaudacao)
        val nomeRecebido = intent.getStringExtra("NOME_PACIENTE") ?: "Paciente"
        tvSaudacao.text = "Olá, $nomeRecebido"

        // Atalhos rápidos
        val btnAcaoConsultas = findViewById<LinearLayout>(R.id.btnAcaoConsultas)
        val btnAcaoMedicos = findViewById<LinearLayout>(R.id.btnAcaoMedicos)

        btnAcaoConsultas.setOnClickListener {
            startActivity(Intent(this, MinhasConsultasActivity::class.java))
        }

        btnAcaoMedicos.setOnClickListener {
            startActivity(Intent(this, BuscaMedicosActivity::class.java))
        }

        // Botão "Visualizar Consultas"
        findViewById<android.widget.Button>(R.id.btnVerTodasConsultas).setOnClickListener {
            startActivity(Intent(this, MinhasConsultasActivity::class.java))
        }

        // RecyclerView de próximas consultas (dados reais serão carregados na semana que vem)
        val rvProximas = findViewById<RecyclerView>(R.id.rvProximasConsultas)
        rvProximas.layoutManager = LinearLayoutManager(this)
        // TODO: rvProximas.adapter = ConsultasPacienteAdapter(listaConsultas) { acao, consulta -> ... }

        // Bottom Navigation
        configurarBottomNav(R.id.nav_home)
    }

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavPaciente)
        bottomNav.selectedItemId = itemSelecionado

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true // já estamos aqui
                R.id.nav_consultas -> {
                    startActivity(Intent(this, MinhasConsultasActivity::class.java))
                    false
                }
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
