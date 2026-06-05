package com.example.medicoapplication.UI.activities.paciente.medicos

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.activities.paciente.medicos.PerfilMedicoPublicoActivity
import com.example.medicoapplication.UI.adapters.MedicoAdapter
import com.example.medicoapplication.viewmodel.paciente.medicos.BuscaMedicosViewModel
import kotlinx.coroutines.launch

class BuscaMedicosActivity : BaseActivity() {

    private val viewModel: BuscaMedicosViewModel by viewModels()
    private lateinit var adapter: MedicoAdapter

    private var idPaciente: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_busca_medicos)

        idPaciente = intent.getLongExtra("ID_PACIENTE", -1L)

        val etPesquisar = findViewById<EditText>(R.id.etPesquisarMedico)
        val rvMedicos   = findViewById<RecyclerView>(R.id.rvMedicos)

        adapter = MedicoAdapter(emptyList()) { medico ->
            startActivity(
                Intent(this, PerfilMedicoPublicoActivity::class.java).apply {
                    putExtra("MEDICO_ID", medico.id)
                    putExtra("NOME_MEDICO", medico.usuario?.nome ?: "Médico")
                    putExtra("ID_PACIENTE", idPaciente)
                }
            )
        }
        rvMedicos.layoutManager = LinearLayoutManager(this)
        rvMedicos.adapter = adapter

        etPesquisar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filtrar(s?.toString() ?: "")
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
                    is BuscaMedicosViewModel.UiState.Loading -> Unit
                    is BuscaMedicosViewModel.UiState.Success -> adapter.atualizarLista(state.medicos)
                    is BuscaMedicosViewModel.UiState.Error   -> {
                        handleError(state.error)}
                }
            }
        }
    }

}