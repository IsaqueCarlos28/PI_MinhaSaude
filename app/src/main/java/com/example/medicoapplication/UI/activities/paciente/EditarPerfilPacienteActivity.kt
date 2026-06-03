package com.example.medicoapplication.UI.activities.paciente

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.activities.paciente.viewmodel.EditarPerfilPacienteViewModel
import com.example.medicoapplication.data.remote.DTO.Genero
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

/**
 * Tela de edição do perfil do paciente.
 * Recebe via Intent:
 *   - ID_PACIENTE (Long)
 */
class EditarPerfilPacienteActivity : AppCompatActivity() {

    private val viewModel: EditarPerfilPacienteViewModel by viewModels()

    private var idPaciente: Long = -1L

    private lateinit var etNome: EditText
    private lateinit var etEmail: EditText
    private lateinit var etCpf: EditText
    private lateinit var etTelefone: EditText
    private lateinit var etNascimento: EditText
    private lateinit var spinnerGenero: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil_paciente)

        idPaciente = intent.getLongExtra("ID_PACIENTE", -1L)

        bindViews()
        configurarSpinnerGenero()
        configurarBotoes()
        observeViewModel()

        if (idPaciente != -1L) viewModel.carregarPerfil(idPaciente)
        configurarBottomNav(R.id.nav_perfil)
    }

    private fun bindViews() {
        etNome       = findViewById(R.id.etEditNome)
        etEmail      = findViewById(R.id.etEditEmail)
        etCpf        = findViewById(R.id.etEditCpf)
        etTelefone   = findViewById(R.id.etEditTelefone)
        etNascimento = findViewById(R.id.etEditNascimento)
        spinnerGenero = findViewById(R.id.spinnerEditGenero)
    }

    private fun configurarSpinnerGenero() {
        val opcoes = listOf("MASCULINO", "FEMININO", "OUTRO")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcoes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGenero.adapter = adapter
    }

    private fun configurarBotoes() {
        findViewById<ImageButton>(R.id.btnVoltarEditarPerfil).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnSalvarPerfil).setOnClickListener {
            val nome       = etNome.text.toString().trim()
            val email      = etEmail.text.toString().trim()
            val cpf        = etCpf.text.toString().trim()
            val telefone   = etTelefone.text.toString().trim()
            val nascimento = etNascimento.text.toString().trim()

            if (nome.isEmpty())  { etNome.error = "Informe o nome"; return@setOnClickListener }
            if (email.isEmpty()) { etEmail.error = "Informe o e-mail"; return@setOnClickListener }

            val genero = try {
                Genero.valueOf(spinnerGenero.selectedItem.toString())
            } catch (e: Exception) { Genero.OUTRO }

            viewModel.salvarPerfil(idPaciente, nome, cpf, email, telefone, genero, nascimento)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is EditarPerfilPacienteViewModel.UiState.Idle    -> setLoading(false)
                    is EditarPerfilPacienteViewModel.UiState.Loading -> setLoading(true)
                    is EditarPerfilPacienteViewModel.UiState.Error   -> {
                        setLoading(false)
                        Toast.makeText(this@EditarPerfilPacienteActivity, state.error, Toast.LENGTH_LONG).show()
                    }
                    is EditarPerfilPacienteViewModel.UiState.Carregado -> {
                        setLoading(false)
                        val p = state.paciente
                        etNome.setText(p.nome ?: "")
                        etEmail.setText(p.email ?: "")
                        etCpf.setText(p.cpf ?: "")
                        etTelefone.setText(p.telefone ?: "")
                        etNascimento.setText(p.dataNascimento ?: "")

                        val generoStr = p.genero?.name ?: "OUTRO"
                        val adapter = spinnerGenero.adapter
                        for (i in 0 until adapter.count) {
                            if (adapter.getItem(i) == generoStr) {
                                spinnerGenero.setSelection(i)
                                break
                            }
                        }
                    }
                    is EditarPerfilPacienteViewModel.UiState.Salvo -> {
                        setLoading(false)
                        Toast.makeText(this@EditarPerfilPacienteActivity, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }

    private fun setLoading(carregando: Boolean) {
        val btn = findViewById<Button>(R.id.btnSalvarPerfil)
        btn.isEnabled = !carregando
        btn.text = if (carregando) "Salvando..." else "Salvar Alterações"
    }

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavEditarPerfil)
        bottomNav.selectedItemId = itemSelecionado
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home      -> { startActivity(Intent(this, HomePacienteActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                R.id.nav_consultas -> { startActivity(Intent(this, MinhasConsultasActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                R.id.nav_medicos   -> { startActivity(Intent(this, BuscaMedicosActivity::class.java)); false }
                R.id.nav_perfil    -> true
                else -> false
            }
        }
    }
}
