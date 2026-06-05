package com.example.medicoapplication.UI.activities.paciente.medicos

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.adapters.MedicoAdapter
import com.example.medicoapplication.viewmodel.paciente.medicos.BuscaMedicosViewModel
import kotlinx.coroutines.launch

class PesquisaMedicosActivity : BaseActivity() {

    private val viewModel: BuscaMedicosViewModel by viewModels()

    private var filtroAtivo = "PRESENCIAL"
    private lateinit var rvResultados: RecyclerView
    private lateinit var adapter: MedicoAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesquisa_medicos)

        val btnVoltar   = findViewById<ImageButton>(R.id.btnVoltarPesquisa)
        val etPesquisa  = findViewById<EditText>(R.id.etPesquisarMedicoOuEspecialidade)
        val btnPresencial = findViewById<Button>(R.id.btnFiltroParticular)
        val btnTele     = findViewById<Button>(R.id.btnFiltroConvenio)
        rvResultados    = findViewById(R.id.rvResultadosPesquisa)
        progressBar     = ProgressBar(this).also { it.visibility = View.GONE }

        // Navigate to doctor's public profile on click
        adapter = MedicoAdapter(emptyList()) { medico ->
            val intent = Intent(this, PerfilMedicoPublicoActivity::class.java).apply {
                putExtra("MEDICO_ID", medico.id)
                putExtra("NOME_MEDICO", medico.usuario?.nome ?: "Médico")
            }
            startActivity(intent)
        }

        rvResultados.layoutManager = LinearLayoutManager(this)
        rvResultados.adapter = adapter

        btnVoltar.setOnClickListener { finish() }

        btnPresencial.setOnClickListener {
            filtroAtivo = "PRESENCIAL"
            atualizarEstiloBotoesFiltro(btnPresencial, btnTele)
            viewModel.filtrar(etPesquisa.text.toString())
        }

        btnTele.setOnClickListener {
            filtroAtivo = "TELEMEDICINA"
            atualizarEstiloBotoesFiltro(btnTele, btnPresencial)
            viewModel.filtrar(etPesquisa.text.toString())
        }

        etPesquisa.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filtrar(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        observeViewModel()
        viewModel.carregarMedicos(1,20)
        setupBottomNavigation(R.id.nav_medicos_paciente)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is BuscaMedicosViewModel.UiState.Idle    -> Unit
                    is BuscaMedicosViewModel.UiState.Loading -> progressBar.visibility = View.VISIBLE
                    is BuscaMedicosViewModel.UiState.Success -> {
                        progressBar.visibility = View.GONE
                        adapter.atualizarLista(state.medicos)
                        if (state.medicos.isEmpty()) {
                            showToast("Nenhum médico encontrado")
                        }
                    }
                    is BuscaMedicosViewModel.UiState.Error -> {
                        progressBar.visibility = View.GONE
                        handleError(state.error)
                    }
                }
            }
        }
    }

    private fun atualizarEstiloBotoesFiltro(btnAtivo: Button, btnInativo: Button) {
        btnAtivo.backgroundTintList  = ColorStateList.valueOf(Color.parseColor("#3B82F6"))
        btnAtivo.setTextColor(Color.WHITE)
        btnInativo.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E2E8F0"))
        btnInativo.setTextColor(Color.parseColor("#64748B"))
    }

}