package com.example.medicoapplication.activities.paciente


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class PesquisaMedicosActivity : AppCompatActivity() {

    private var filtroAtivo = "PARTICULAR" // "PARTICULAR" ou "CONVENIO"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesquisa_medicos)

        val btnVoltar           = findViewById<ImageButton>(R.id.btnVoltarPesquisa)
        val etPesquisa          = findViewById<EditText>(R.id.etPesquisarMedicoOuEspecialidade)
        val btnParticular       = findViewById<Button>(R.id.btnFiltroParticular)
        val btnConvenio         = findViewById<Button>(R.id.btnFiltroConvenio)
        val rvResultados        = findViewById<RecyclerView>(R.id.rvResultadosPesquisa)

        rvResultados.layoutManager = LinearLayoutManager(this)

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

        // Receber médico pré-selecionado se vier de BuscaMedicos
        val medicoIdPreSelecionado = intent.getLongExtra("MEDICO_ID", -1L)
        if (medicoIdPreSelecionado != -1L) {
            // TODO: navegar direto para AgendarConsultaActivity com medicoId
        }

        buscarMedicos("")
        configurarBottomNav(R.id.nav_medicos)
    }

    private fun buscarMedicos(query: String) {
        lifecycleScope.launch {
            try {
                // TODO: chamar API na semana que vem
                // val response = RetrofitClient.api.getMedicos()
                // filtrar por query e filtroAtivo
                // rvResultados.adapter = MedicoBuscaAdapter(resultados) { medico ->
                //     val intent = Intent(this@PesquisaMedicosActivity, AgendarConsultaActivity::class.java)
                //     intent.putExtra("MEDICO_ID", medico.id)
                //     startActivity(intent)
                // }
            } catch (e: Exception) {
                // TODO: mostrar erro
            }
        }
    }

    private fun atualizarEstiloBotoesFiltro(btnAtivo: Button, btnInativo: Button) {
        btnAtivo.backgroundTintList =
            android.content.res.ColorStateList.valueOf(Color.parseColor("#3B82F6"))
        btnAtivo.setTextColor(Color.WHITE)

        btnInativo.backgroundTintList =
            android.content.res.ColorStateList.valueOf(Color.parseColor("#E2E8F0"))
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
