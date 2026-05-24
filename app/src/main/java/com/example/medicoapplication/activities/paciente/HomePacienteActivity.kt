package com.example.medicoapplication.activities.paciente

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.activities.auth_e_cadastro.LoginActivity
import com.example.medicoapplication.adapters.ConsultasPacienteAdapter
import com.example.medicoapplication.data.remote.DTO.StatusConsulta
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaStatusRequestDto
import com.example.medicoapplication.data.remote.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class HomePacienteActivity : AppCompatActivity() {

    private var idPaciente: Long = -1L
    private var emailPaciente: String = ""

    private lateinit var tvSaudacao: TextView
    private lateinit var rvProximasConsultas: RecyclerView
    private lateinit var adaptadorConsultas: ConsultasPacienteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_paciente)

        idPaciente    = intent.getLongExtra("ID_PACIENTE", -1L)
        emailPaciente = intent.getStringExtra("EMAIL_PACIENTE") ?: ""

        bindViews()
        configurarRecyclerView()
        configurarAtalhos()
        configurarBottomNav(R.id.nav_home)

        if (idPaciente != -1L) {
            carregarNomePaciente()
            carregarProximasConsultas()
        } else {
            tvSaudacao.text = "Ola, ${emailPaciente.substringBefore("@")}"
            Toast.makeText(this, "Sessao invalida. Faca login novamente.", Toast.LENGTH_LONG).show()
            redirecionarParaLogin()
        }
    }

    private fun bindViews() {
        tvSaudacao          = findViewById(R.id.tvSaudacao)
        rvProximasConsultas = findViewById(R.id.rvProximasConsultas)
    }

    private fun configurarRecyclerView() {
        adaptadorConsultas = ConsultasPacienteAdapter(
            consultas   = emptyList(),
            onReagendar = { consulta -> navegarParaReagendar(consulta) },
            onCancelar  = { consulta -> cancelarConsulta(consulta) }
        )
        rvProximasConsultas.layoutManager = LinearLayoutManager(this)
        rvProximasConsultas.adapter = adaptadorConsultas
    }

    private fun configurarAtalhos() {
        findViewById<LinearLayout>(R.id.btnAcaoConsultas).setOnClickListener {
            startActivity(
                Intent(this, MinhasConsultasActivity::class.java).apply {
                    putExtra("ID_PACIENTE", idPaciente)
                }
            )
        }

        findViewById<LinearLayout>(R.id.btnAcaoMedicos).setOnClickListener {
            startActivity(Intent(this, BuscaMedicosActivity::class.java))
        }

        findViewById<Button>(R.id.btnVerTodasConsultas).setOnClickListener {
            startActivity(
                Intent(this, MinhasConsultasActivity::class.java).apply {
                    putExtra("ID_PACIENTE", idPaciente)
                }
            )
        }
    }

    private fun carregarNomePaciente() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getPacienteById(idPaciente)
                if (response.isSuccessful) {
                    val paciente = response.body()
                    val primeiroNome = paciente?.nome?.split(" ")?.firstOrNull()
                        ?: emailPaciente.substringBefore("@")
                    tvSaudacao.text = "Ola, $primeiroNome"
                } else {
                    tvSaudacao.text = "Ola, ${emailPaciente.substringBefore("@")}"
                }
            } catch (e: Exception) {
                tvSaudacao.text = "Ola, ${emailPaciente.substringBefore("@")}"
            }
        }
    }

    private fun carregarProximasConsultas() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getConsultasByPaciente(idPaciente)

                if (response.isSuccessful) {
                    val todasConsultas = response.body() ?: emptyList()

                    val proximas = todasConsultas
                        .filter { it.status == StatusConsulta.AGENDADA }
                        .sortedWith(compareBy({ it.data }, { it.horaInicio }))

                    adaptadorConsultas.atualizarLista(proximas)
                } else {
                    Toast.makeText(
                        this@HomePacienteActivity,
                        "Nao foi possivel carregar as consultas (${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@HomePacienteActivity,
                    "Erro de conexao ao carregar consultas.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navegarParaReagendar(consulta: ConsultaResponseDto) {
        Toast.makeText(this, "Reagendamento disponivel em breve.", Toast.LENGTH_SHORT).show()
    }

    private fun cancelarConsulta(consulta: ConsultaResponseDto) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.atualizarStatusPeloPaciente(
                    idPaciente = idPaciente,
                    idEvento   = consulta.id,
                    status     = ConsultaStatusRequestDto(status = StatusConsulta.CANCELADA)
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@HomePacienteActivity, "Consulta cancelada.", Toast.LENGTH_SHORT).show()
                    carregarProximasConsultas()
                } else {
                    Toast.makeText(
                        this@HomePacienteActivity,
                        "Nao foi possivel cancelar (${response.code()}).",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@HomePacienteActivity, "Erro de conexao.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun redirecionarParaLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
    }

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavPaciente)
        bottomNav.selectedItemId = itemSelecionado

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home      -> true
                R.id.nav_consultas -> {
                    startActivity(
                        Intent(this, MinhasConsultasActivity::class.java).apply {
                            putExtra("ID_PACIENTE", idPaciente)
                        }
                    )
                    false
                }
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
}
