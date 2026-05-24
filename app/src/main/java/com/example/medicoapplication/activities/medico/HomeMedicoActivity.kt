package com.example.medicoapplication.activities.medico

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeMedicoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_medico)

        // Saudação com nome do médico (virá do login)
        val tvSaudacao = findViewById<TextView>(R.id.tvSaudacaoMedico)
        val nomeMedico = intent.getStringExtra("NOME_MEDICO") ?: "Médico"
        tvSaudacao.text = "Olá, $nomeMedico"

        // Atalhos rápidos
        findViewById<LinearLayout>(R.id.btnAcaoAgenda).setOnClickListener {
            startActivity(Intent(this, AgendaMedicoActivity::class.java).apply {
                putExtra("NOME_MEDICO", nomeMedico)
                putExtra("ID_MEDICO", intent.getLongExtra("ID_MEDICO", -1L))
            })
        }

        findViewById<LinearLayout>(R.id.btnAcaoConsultasMedico).setOnClickListener {
            startActivity(Intent(this, ConsultasMedicoActivity::class.java).apply {
                putExtra("NOME_MEDICO", nomeMedico)
                putExtra("ID_MEDICO", intent.getLongExtra("ID_MEDICO", -1L))
            })
        }

        findViewById<LinearLayout>(R.id.btnAcaoUsuarioMedico).setOnClickListener {
            startActivity(Intent(this, PerfilMedicoActivity::class.java).apply {
                putExtra("NOME_MEDICO", nomeMedico)
                putExtra("ID_MEDICO", intent.getLongExtra("ID_MEDICO", -1L))
            })
        }

        findViewById<LinearLayout>(R.id.btnAcaoConfigMedico).setOnClickListener {
            startActivity(Intent(this, ConfiguracoesMedicoActivity::class.java).apply {
                putExtra("NOME_MEDICO", nomeMedico)
                putExtra("ID_MEDICO", intent.getLongExtra("ID_MEDICO", -1L))
            })
        }

        // Botão ver todas consultas
        findViewById<android.widget.Button>(R.id.btnVerConsultasMedico).setOnClickListener {
            startActivity(Intent(this, ConsultasMedicoActivity::class.java).apply {
                putExtra("NOME_MEDICO", nomeMedico)
                putExtra("ID_MEDICO", intent.getLongExtra("ID_MEDICO", -1L))
            })
        }

        // RecyclerView próximas consultas
        val rvProximas = findViewById<RecyclerView>(R.id.rvProximasConsultasMedico)
        rvProximas.layoutManager = LinearLayoutManager(this)
        // TODO: carregar consultas via API — GET medicos/{idMedico}/consultas-agendadas

        configurarBottomNav(R.id.nav_inicio)
    }

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavMedico)
        bottomNav.selectedItemId = itemSelecionado
        val idMedico = intent.getLongExtra("ID_MEDICO", -1L)
        val nomeMedico = intent.getStringExtra("NOME_MEDICO") ?: "Médico"

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> true
                R.id.nav_agenda -> {
                    startActivity(Intent(this, AgendaMedicoActivity::class.java).apply {
                        putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico)
                    })
                    false
                }
                R.id.nav_consultas_med -> {
                    startActivity(Intent(this, ConsultasMedicoActivity::class.java).apply {
                        putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico)
                    })
                    false
                }
                R.id.nav_usuario -> {
                    startActivity(Intent(this, PerfilMedicoActivity::class.java).apply {
                        putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico)
                    })
                    false
                }
                R.id.nav_config -> {
                    startActivity(Intent(this, ConfiguracoesMedicoActivity::class.java).apply {
                        putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico)
                    })
                    false
                }
                else -> false
            }
        }
    }
}
