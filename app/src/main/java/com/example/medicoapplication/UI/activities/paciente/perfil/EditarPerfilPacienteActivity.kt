package com.example.medicoapplication.UI.activities.paciente.perfil

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.common.mappers.GeneroMapper
import com.example.medicoapplication.UI.common.mappers.PerfilMapper
import com.example.medicoapplication.UI.common.validations.PerfilValidator
import com.example.medicoapplication.UI.common.validations.ValidationField
import com.example.medicoapplication.UI.common.validations.ValidationResult
import com.example.medicoapplication.viewmodel.paciente.perfil.EditarPerfilPacienteViewModel
import kotlinx.coroutines.launch

/**
 * Tela de edição do perfil do paciente.
 * Recebe via Intent:
 *   - ID_PACIENTE (Long)
 */
class EditarPerfilPacienteActivity : BaseActivity() {

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

        if (idPaciente != -1L) viewModel.carregarPerfil()
        setupBottomNavigation(R.id.nav_perfil_paciente)
    }

    private fun bindViews() {
        etNome        = findViewById(R.id.etEditNome)
        etEmail       = findViewById(R.id.etEditEmail)
        etCpf         = findViewById(R.id.etEditCpf)
        etTelefone    = findViewById(R.id.etEditTelefone)
        etNascimento  = findViewById(R.id.etEditNascimento)
        spinnerGenero = findViewById(R.id.spinnerEditGenero)
    }

    private fun configurarSpinnerGenero() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, GeneroMapper.spinnerOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGenero.adapter = adapter
    }

    private fun configurarBotoes() {
        findViewById<ImageButton>(R.id.btnVoltarEditarPerfil).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnSalvarPerfil).setOnClickListener {
            val dto = PerfilMapper.uiToApi(
                nome           = etNome.text.toString(),
                email          = etEmail.text.toString(),
                cpf            = etCpf.text.toString(),
                telefone       = etTelefone.text.toString(),
                dataNascimento = etNascimento.text.toString(),
                genero         = spinnerGenero.selectedItem.toString()
            )

            when (val result = PerfilValidator.validarEdicaoPaciente(dto)) {
                is ValidationResult.Success -> viewModel.salvarPerfil(dto)
                is ValidationResult.Error   -> handleValidationError(result)
            }
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
                        handleError(state.error)
                    }
                    is EditarPerfilPacienteViewModel.UiState.Carregado -> {
                        setLoading(false)
                        // Pre-fill the form using the mapper instead of inline logic
                        val prefill = PerfilMapper.apiToFormPrefill(state.paciente)
                        etNome.setText(prefill.nome)
                        etEmail.setText(prefill.email)
                        etCpf.setText(prefill.cpf)
                        etTelefone.setText(prefill.telefone)
                        etNascimento.setText(prefill.dataNascimento)

                        val generoDisplay = GeneroMapper.toDisplayString(prefill.genero)
                        val adapter = spinnerGenero.adapter
                        for (i in 0 until adapter.count) {
                            if (adapter.getItem(i) == generoDisplay) {
                                spinnerGenero.setSelection(i)
                                break
                            }
                        }
                    }
                    is EditarPerfilPacienteViewModel.UiState.Salvo -> {
                        setLoading(false)
                        showToast("Perfil atualizado com sucesso!")
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

    private fun handleValidationError(error: ValidationResult.Error) {
        when (error.field) {
            ValidationField.NOME            -> showValidationError(etNome, error.message)
            ValidationField.EMAIL           -> showValidationError(etEmail, error.message)
            ValidationField.CPF             -> showValidationError(etCpf, error.message)
            ValidationField.TELEFONE        -> showValidationError(etTelefone, error.message)
            ValidationField.DATA_NASCIMENTO -> showValidationError(etNascimento, error.message)
            else                            -> showToast(error.message)
        }
    }

}