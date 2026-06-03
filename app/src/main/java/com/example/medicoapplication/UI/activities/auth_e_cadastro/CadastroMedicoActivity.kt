package com.example.medicoapplication.UI.activities.auth_e_cadastro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.common.mappers.CadastroMapper
import com.example.medicoapplication.UI.common.validations.CadastroValidator
import com.example.medicoapplication.UI.common.validations.ValidationField
import com.example.medicoapplication.UI.common.validations.ValidationResult
import com.example.medicoapplication.viewmodel.auth.CadastroViewModel
import com.example.medicoapplication.data.remote.DTO.Genero
import com.example.medicoapplication.data.remote.DTO.medico.MedicoCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteCreateRequestDto
import kotlinx.coroutines.launch

class CadastroMedicoActivity : BaseActivity() {

    private val viewModel: CadastroViewModel by viewModels()

    private lateinit var etNome: EditText
    private lateinit var etEmail: EditText
    private lateinit var etCpf: EditText
    private lateinit var etTelefone: EditText
    private lateinit var etDataNascimento: EditText
    private lateinit var spGenero: Spinner   // FIX: was EditText — the XML uses a Spinner here
    private lateinit var etSenha: EditText
    private lateinit var etCrmDigitos: EditText
    private lateinit var spUf: Spinner
    private lateinit var btnCadastrar: Button
    private lateinit var btnVoltar: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_medico)

        etNome           = findViewById(R.id.etNome)
        etEmail          = findViewById(R.id.etEmail)
        etCpf            = findViewById(R.id.etCpf)
        etTelefone       = findViewById(R.id.etTelefone)
        etDataNascimento = findViewById(R.id.etDataNascimento)
        spGenero         = findViewById(R.id.etGenero)  // Spinner in the XML, id kept as etGenero
        etSenha          = findViewById(R.id.etSenha)
        etCrmDigitos     = findViewById(R.id.etCrm)
        spUf             = findViewById(R.id.spUf)
        btnCadastrar     = findViewById(R.id.btnCadastrar)
        btnVoltar        = findViewById(R.id.btnVoltar)

        btnVoltar.setOnClickListener { finish() }
        btnCadastrar.setOnClickListener { tentarCadastro() }
        observeViewModel()
    }

    private fun tentarCadastro() {

        val nome = etNome.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val cpf = etCpf.text.toString().trim()
        val telefone = etTelefone.text.toString().trim()
        val dataNasc = etDataNascimento.text.toString().trim()
        val genero = spGenero.selectedItem.toString()
        val senha = etSenha.text.toString().trim()
        val crmDigits = etCrmDigitos.text.toString().trim()
        val uf = spUf.selectedItem.toString()


        val dto = MedicoCreateRequestDto(
            usuarioBase = PacienteCreateRequestDto(
                nome = nome,
                cpf = cpf,
                email = email,
                telefone = telefone,
                genero = Genero.valueOf(genero),
                dataNascimento = dataNasc,
                senha = senha
            ),
            crmUf = uf,
            crmDigits = crmDigits
        )
        when (
            val result =
                CadastroValidator.validarMedico(dto)
        ) {
            is ValidationResult.Success -> {
                viewModel.cadastrarMedico(
                    dto
                )
            }

            is ValidationResult.Error -> {
                handleValidationError(result)
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is CadastroViewModel.UiState.Idle    -> setLoading(false)
                    is CadastroViewModel.UiState.Loading -> setLoading(true)
                    is CadastroViewModel.UiState.Error   -> {
                        setLoading(false)
                        viewModel.resetState()
                    }
                    is CadastroViewModel.UiState.Success -> {
                        showToast("Médico cadastrado com sucesso!")
                        startActivity(Intent(this@CadastroMedicoActivity, LoginActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    private fun setLoading(carregando: Boolean) {
        btnCadastrar.isEnabled = !carregando
        btnCadastrar.text = if (carregando) "Processando..." else "Cadastrar"
    }

    private fun handleValidationError(
        error: ValidationResult.Error
    ) {
        when (error.field) {
            ValidationField.NOME ->
                showValidationError(
                    etNome,
                    error.message
                )

            ValidationField.EMAIL ->
                showValidationError(
                    etEmail,
                    error.message
                )

            ValidationField.CPF ->
                showValidationError(
                    etCpf,
                    error.message
                )

            ValidationField.TELEFONE ->
                showValidationError(
                    etTelefone,
                    error.message
                )

            ValidationField.DATA_NASCIMENTO ->
                showValidationError(
                    etDataNascimento,
                    error.message
                )

            ValidationField.SENHA ->
                showValidationError(
                    etSenha,
                    error.message
                )

            ValidationField.CRM ->
                showValidationError(
                    etCrmDigitos,
                    error.message
                )

            else ->
                showToast(error.message)
        }
    }


}
