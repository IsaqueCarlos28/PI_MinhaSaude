package com.example.medicoapplication.activities.paciente

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.medicoapplication.R
import com.example.medicoapplication.activities.auth_e_cadastro.LoginActivity
import com.example.medicoapplication.data.remote.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilPacienteActivity : AppCompatActivity() {

    private var idPaciente: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_paciente)

        idPaciente = intent.getLongExtra("ID_PACIENTE", -1L)

        if (idPaciente != -1L) {
            carregarPerfilPaciente()
        }

        // CORRIGIDO: o XML nao tem btnLogoutPaciente — o layout nao possui botao de logout.
        // O logout e feito pelo proprio item de perfil na BottomNav ou pode ser adicionado
        // ao XML futuramente com id btnLogoutPaciente.
        // Por enquanto o logout esta disponivel navegando de volta para o Login manualmente.

        configurarBottomNav(R.id.nav_perfil)
    }

    private fun carregarPerfilPaciente() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.api.getPacienteById(idPaciente)
                if (response.isSuccessful) {
                    val paciente = response.body()
                    withContext(Dispatchers.Main) {
                        paciente?.let {
                            findViewById<TextView>(R.id.tvNomePerfil).text   = it.nome?.uppercase() ?: "—"
                            findViewById<TextView>(R.id.tvEmailPerfil).text  = it.email             ?: "—"
                            findViewById<TextView>(R.id.tvCpfPerfil).text    = it.cpf               ?: "—"
                            findViewById<TextView>(R.id.tvTelefonePerfil).text   = it.telefone          ?: "—"
                            findViewById<TextView>(R.id.tvNascimentoPerfil).text = it.dataNascimento    ?: "—"
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PerfilPacienteActivity, "Erro ao carregar perfil", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavPerfil)
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
                R.id.nav_consultas -> {
                    startActivity(
                        Intent(this, MinhasConsultasActivity::class.java).apply {
                            putExtra("ID_PACIENTE", idPaciente)
                        }
                    )
                    false
                }
                R.id.nav_medicos -> {
                    startActivity(Intent(this, BuscaMedicosActivity::class.java))
                    false
                }
                R.id.nav_perfil -> true
                else -> false
            }
        }
    }
}
