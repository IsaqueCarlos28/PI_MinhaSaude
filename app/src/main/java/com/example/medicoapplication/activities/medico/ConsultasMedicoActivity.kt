package com.example.medicoapplication.activities.medico

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.data.remote.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConsultasMedicoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultas_medico)

        val nomeMedico = intent.getStringExtra("NOME_MEDICO") ?: "Médico"
        val idMedico = intent.getLongExtra("ID_MEDICO", -1L)

        findViewById<TextView>(R.id.tvSaudacaoConsultas).text = "Olá, $nomeMedico"

        val rv = findViewById<RecyclerView>(R.id.rvConsultasMedico)
        rv.layoutManager = LinearLayoutManager(this)

        // Filtros de status
        val btnTodas = findViewById<Button>(R.id.btnFiltroTodas)
        val btnAgendadas = findViewById<Button>(R.id.btnFiltroAgendadas)
        val btnRealizadas = findViewById<Button>(R.id.btnFiltroRealizadas)
        val btnCanceladas = findViewById<Button>(R.id.btnFiltroCanceladas)

        fun resetarBotoes() {
            listOf(btnTodas, btnAgendadas, btnRealizadas, btnCanceladas).forEach {
                it.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF94A3B8.toInt())
            }
        }
        btnTodas.setOnClickListener {
            resetarBotoes()
            btnTodas.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF3B82F6.toInt())
            carregarConsultas(idMedico, null)
        }
        btnAgendadas.setOnClickListener {
            resetarBotoes()
            btnAgendadas.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF3B82F6.toInt())
            carregarConsultas(idMedico, "AGENDADA")
        }
        btnRealizadas.setOnClickListener {
            resetarBotoes()
            btnRealizadas.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF3B82F6.toInt())
            carregarConsultas(idMedico, "REALIZADA")
        }
        btnCanceladas.setOnClickListener {
            resetarBotoes()
            btnCanceladas.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF3B82F6.toInt())
            carregarConsultas(idMedico, "CANCELADA")
        }

        // Carregar todas por padrão
        if (idMedico != -1L) carregarConsultas(idMedico, null)

        configurarBottomNav(R.id.nav_consultas_med, idMedico, nomeMedico)
    }

    private fun carregarConsultas(idMedico: Long, filtroStatus: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // CORRIGIDO: Agora aponta para '.api' que está no seu RetrofitClient
                val response = RetrofitClient.api.getConsultasByMedico(idMedico)
                if (response.isSuccessful) {
                    val consultas = response.body() ?: emptyList()
                    val filtradas = if (filtroStatus != null)
                        consultas.filter { it.status?.name == filtroStatus }
                    else consultas
                    withContext(Dispatchers.Main) {
                        // TODO: Configurar adapter com a lista filtrada
                        // rvConsultasMedico.adapter = ConsultasMedicoAdapter(filtradas) { ... }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ConsultasMedicoActivity, "Erro ao carregar consultas", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun configurarBottomNav(itemSelecionado: Int, idMedico: Long, nomeMedico: String) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavConsultasMedico)
        bottomNav.selectedItemId = itemSelecionado

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    startActivity(Intent(this, HomeMedicoActivity::class.java).apply {
                        putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico)
                    }); false
                }
                R.id.nav_agenda -> {
                    startActivity(Intent(this, AgendaMedicoActivity::class.java).apply {
                        putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico)
                    }); false
                }
                R.id.nav_consultas_med -> true
                R.id.nav_usuario -> {
                    startActivity(Intent(this, PerfilMedicoActivity::class.java).apply {
                        putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico)
                    }); false
                }
                R.id.nav_config -> {
                    startActivity(Intent(this, ConfiguracoesMedicoActivity::class.java).apply {
                        putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico)
                    }); false
                }
                else -> false
            }
        }
    }
}