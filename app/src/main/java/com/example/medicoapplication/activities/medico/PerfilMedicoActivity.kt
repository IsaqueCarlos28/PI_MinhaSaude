package com.example.medicoapplication.activities.medico

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.medicoapplication.R
import com.example.medicoapplication.data.remote.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilMedicoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_medico)

        val nomeMedico = intent.getStringExtra("NOME_MEDICO") ?: "Médico"
        val idMedico = intent.getLongExtra("ID_MEDICO", -1L)

        findViewById<TextView>(R.id.tvSaudacaoPerfil).text = "Olá, $nomeMedico"
        findViewById<TextView>(R.id.tvNomeCompletoMedico).text = nomeMedico.uppercase()

        // Carregar dados do médico via API
        if (idMedico != -1L) {
            carregarPerfilMedico(idMedico)
        }

        // Botão editar
        findViewById<Button>(R.id.btnEditarPerfilMedico).setOnClickListener {
            Toast.makeText(this, "Edição de perfil em breve!", Toast.LENGTH_SHORT).show()
        }

        configurarBottomNav(R.id.nav_usuario, idMedico, nomeMedico)
    }

    private fun carregarPerfilMedico(idMedico: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getMedicoById(idMedico)
                if (response.isSuccessful) {
                    val medico = response.body()
                    withContext(Dispatchers.Main) {
                        medico?.let {
                            findViewById<TextView>(R.id.tvNomeCompletoMedico).text = it.nome.uppercase()
                            findViewById<TextView>(R.id.tvCrm).text = it.crm ?: "-"
                            findViewById<TextView>(R.id.tvCpfMedico).text = it.cpf ?: "-"
                            findViewById<TextView>(R.id.tvEmailMedico).text = it.email ?: "-"
                            // especialidade, telefone e nascimento dependem da resposta do DTO
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PerfilMedicoActivity, "Erro ao carregar perfil", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun configurarBottomNav(itemSelecionado: Int, idMedico: Long, nomeMedico: String) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavPerfilMedico)
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
                R.id.nav_consultas_med -> {
                    startActivity(Intent(this, ConsultasMedicoActivity::class.java).apply {
                        putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico)
                    }); false
                }
                R.id.nav_usuario -> true
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
