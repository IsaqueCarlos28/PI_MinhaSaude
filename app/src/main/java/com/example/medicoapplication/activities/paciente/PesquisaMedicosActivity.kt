package com.example.medicoapplication.activities.paciente

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.adapters.MedicoAdapter
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto
import com.google.android.material.bottomnavigation.BottomNavigationView

class PesquisaMedicosActivity : AppCompatActivity() {

    private var filtroAtivo = "PARTICULAR"
    private lateinit var rvResultados: RecyclerView
    private lateinit var adapter: MedicoAdapter
    private lateinit var progressBar: ProgressBar

    private val medicosMock = mutableListOf<MedicoResponseDto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesquisa_medicos)

        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltarPesquisa)
        val etPesquisa = findViewById<EditText>(R.id.etPesquisarMedicoOuEspecialidade)
        val btnParticular = findViewById<Button>(R.id.btnFiltroParticular)
        val btnConvenio = findViewById<Button>(R.id.btnFiltroConvenio)
        rvResultados = findViewById(R.id.rvResultadosPesquisa)

        progressBar = ProgressBar(this)
        progressBar.visibility = View.GONE

        adapter = MedicoAdapter(emptyList()) { medico ->
            val intent = Intent(this, AgendarConsultaActivity::class.java)
            intent.putExtra("MEDICO_ID", medico.id)
            startActivity(intent)
        }

        rvResultados.layoutManager = LinearLayoutManager(this)
        rvResultados.adapter = adapter

        btnVoltar.setOnClickListener { finish() }

        btnParticular.setOnClickListener {
            filtroAtivo = "PARTICULAR"
            atualizarEstiloBotoesFiltro(btnParticular, btnConvenio)
            buscarMedicos(etPesquisa.text.toString())
        }

        btnConvenio.setOnClickListener {
            filtroAtivo = "CONVENIO"
            atualizarEstiloBotoesFiltro(btnConvenio, btnParticular)
            buscarMedicos(etPesquisa.text.toString())
        }

        etPesquisa.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buscarMedicos(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        buscarMedicos("")
        configurarBottomNav(R.id.nav_medicos)
    }

    private fun buscarMedicos(query: String) {
        progressBar.visibility = View.VISIBLE

        try {
            val resultados = medicosMock.filter {
                val nome = it.usuario?.nome ?: ""
                nome.contains(query, ignoreCase = true)
            }

            adapter.atualizarLista(resultados)

            if (resultados.isEmpty()) {
                Toast.makeText(this, "Nenhum médico encontrado", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao carregar médicos", Toast.LENGTH_SHORT).show()
        } finally {
            progressBar.visibility = View.GONE
        }
    }

    private fun atualizarEstiloBotoesFiltro(btnAtivo: Button, btnInativo: Button) {
        btnAtivo.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#3B82F6"))
        btnAtivo.setTextColor(Color.WHITE)

        btnInativo.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#E2E8F0"))
        btnInativo.setTextColor(Color.parseColor("#64748B"))
    }

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavPesquisa)
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
