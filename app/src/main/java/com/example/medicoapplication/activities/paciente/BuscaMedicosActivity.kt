package com.example.medicoapplication.activities.paciente


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class BuscaMedicosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_busca_medicos)

        val etPesquisar = findViewById<EditText>(R.id.etPesquisarMedico)
        val rvMedicos   = findViewById<RecyclerView>(R.id.rvMedicos)

        rvMedicos.layoutManager = LinearLayoutManager(this)
        // TODO: carregar médicos do paciente via API (GET /medicos?pacienteId=...)
        // rvMedicos.adapter = MedicoBuscaAdapter(listaMedicos) { medico ->
        //     val intent = Intent(this, PesquisaMedicosActivity::class.java)
        //     intent.putExtra("MEDICO_ID", medico.id)
        //     startActivity(intent)
        // }

        // Filtro de busca local enquanto digita
        etPesquisar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO: filtrar lista local ou re-buscar na API
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        configurarBottomNav(R.id.nav_medicos)
    }

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavMedicos)
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
