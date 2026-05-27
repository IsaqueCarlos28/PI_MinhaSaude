package com.example.medicoapplication.activities.paciente

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.activities.medico.PerfilMedicoActivity
import com.example.medicoapplication.activities.paciente.viewmodel.AgendarConsultaViewModel
import com.example.medicoapplication.data.repository.MedicoRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

/**
 * Shows a public read-only profile of a doctor, visible to patients.
 * Receives MEDICO_ID and NOME_MEDICO via Intent extras.
 * Has a "Agendar Consulta" button that navigates to AgendarConsultaActivity.
 */
class PerfilMedicoPublicoActivity : AppCompatActivity() {

    private var medicoId: Long = -1L
    private var nomeMedico: String = "Médico"

    private val repository = MedicoRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_medico_publico)

        medicoId   = intent.getLongExtra("MEDICO_ID", -1L)
        nomeMedico = intent.getStringExtra("NOME_MEDICO") ?: "Médico"

        // Back button
        findViewById<ImageButton>(R.id.btnVoltarPerfilPublico).setOnClickListener {
            finish()
        }

        // Populate name immediately from intent (fast feedback)
        findViewById<TextView>(R.id.tvNomeMedicoPublico).text = nomeMedico

        // Load full data from API
        if (medicoId != -1L) carregarMedico()

        // Schedule button
        findViewById<Button>(R.id.btnAgendarComEsteMedico).setOnClickListener {
            val intent = Intent(this, AgendarConsultaActivity::class.java).apply {
                putExtra("ID_MEDICO", medicoId)
                putExtra("NOME_MEDICO", nomeMedico)
            }
            startActivity(intent)
        }

        configurarBottomNav(R.id.nav_medicos)
    }

    private fun carregarMedico() {
        lifecycleScope.launch {
            repository.getMedico(medicoId)
                .onSuccess { medico ->
                    val nome = medico.usuario?.nome ?: nomeMedico
                    nomeMedico = nome

                    findViewById<TextView>(R.id.tvNomeMedicoPublico).text = nome

                    val esp = medico.especialidades
                        .mapNotNull { it.especialidade?.nome }
                        .joinToString(", ")
                        .ifBlank { "Clínico Geral" }

                    findViewById<TextView>(R.id.tvEspecialidadePublico).text = esp
                    findViewById<TextView>(R.id.tvEspecialidadesPublico).text = esp

                    val crm = buildString {
                        if (!medico.crmDigitos.isNullOrBlank()) append(medico.crmDigitos)
                        if (!medico.crmUf.isNullOrBlank()) append("/${medico.crmUf}")
                        if (isEmpty()) append("—")
                    }
                    findViewById<TextView>(R.id.tvCrmPublico).text = "CRM: $crm"

                    findViewById<TextView>(R.id.tvEmailPublico).text =
                        medico.usuario?.email ?: "—"

                    findViewById<TextView>(R.id.tvTelefonePublico).text =
                        medico.usuario?.telefone ?: "—"
                }
                .onFailure {
                    Toast.makeText(
                        this@PerfilMedicoPublicoActivity,
                        "Erro ao carregar dados do médico",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavPerfilPublico)
        bottomNav.selectedItemId = itemSelecionado
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomePacienteActivity::class.java))
                    false
                }
                R.id.nav_consultas -> {
                    startActivity(Intent(this, MinhasConsultasActivity::class.java))
                    false
                }
                R.id.nav_medicos -> true
                R.id.nav_perfil -> {
                    startActivity(Intent(this, PerfilPacienteActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }
}
