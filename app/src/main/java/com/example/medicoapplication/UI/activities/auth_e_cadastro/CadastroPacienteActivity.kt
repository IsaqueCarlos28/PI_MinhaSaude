package com.example.medicoapplication.UI.activities.auth_e_cadastro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.common.mappers.CadastroMapper
import com.example.medicoapplication.UI.common.mappers.GeneroMapper
import com.example.medicoapplication.UI.common.validations.CadastroValidator
import com.example.medicoapplication.UI.common.validations.ValidationField
import com.example.medicoapplication.UI.common.validations.ValidationResult
import com.example.medicoapplication.viewmodel.auth.CadastroViewModel
import com.example.medicoapplication.data.remote.DTO.Genero
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteCreateRequestDto
import com.example.medicoapplication.data.remote.NetworkError
import kotlinx.coroutines.launch

class CadastroPacienteActivity : BaseActivity() {

    private val viewModel: CadastroViewModel by viewModels()

    private lateinit var btnVoltar: TextView
    private lateinit var etNome: EditText
    private lateinit var etEmail: EditText
    private lateinit var etCpf: EditText
    private lateinit var etTelefone: EditText
    private lateinit var etDataNascimento: EditText
    private lateinit var etGenero: EditText
    private lateinit var etSenha: EditText
    private lateinit var etConfirmarSenha: EditText
    private lateinit var btnCadastrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_paciente)

        btnVoltar        = findViewById(R.id.btnVoltar)
        etNome           = findViewById(R.id.etUsuario)
        etEmail          = findViewById(R.id.etEmail)
        etCpf            = findViewById(R.id.etCpf)
        etTelefone       = findViewById(R.id.etTelefone)
        etDataNascimento = findViewById(R.id.etDataNascimento)
        etGenero         = findViewById(R.id.etGenero)
        etSenha          = findViewById(R.id.etSenha)
        etConfirmarSenha = findViewById(R.id.etConfirmarSenha)
        btnCadastrar     = findViewById(R.id.btnCadastrar)

        btnVoltar.setOnClickListener { finish() }
        btnCadastrar.setOnClickListener { tentarCadastro() }
        observeViewModel()
    }

    private fun tentarCadastro() {
        // Build a raw DTO from form values — mapper will clean/format it before sending
        val rawDto = PacienteCreateRequestDto(
            nome           = etNome.text.toString().trim(),
            email          = etEmail.text.toString().trim(),
            cpf            = etCpf.text.toString().trim(),
            telefone       = etTelefone.text.toString().trim(),
            genero         = GeneroMapper.fromString(etGenero.text.toString().trim()),
            dataNascimento = etDataNascimento.text.toString().trim(),
            senha          = etSenha.text.toString().trim()
        )
        val confirmacaoSenha = etConfirmarSenha.text.toString().trim()

        when (val result = CadastroValidator.validarPaciente(rawDto, confirmacaoSenha)) {
            is ValidationResult.Success -> {
                val apiDto = CadastroMapper.cadastroPacienteToApi(rawDto)
                viewModel.cadastrarPaciente(apiDto)
            }
            is ValidationResult.Error -> handleValidationError(result)
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
                        handleError(state.error)
                        viewModel.resetState()
                    }
                    is CadastroViewModel.UiState.Success -> {
                        showToast("Cadastro realizado com sucesso!")
                        startActivity(Intent(this@CadastroPacienteActivity, LoginActivity::class.java))
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

    private fun handleValidationError(error: ValidationResult.Error) {
        when (error.field) {
            ValidationField.NOME             -> showValidationError(etNome, error.message)
            ValidationField.EMAIL            -> showValidationError(etEmail, error.message)
            ValidationField.CPF              -> showValidationError(etCpf, error.message)
            ValidationField.TELEFONE         -> showValidationError(etTelefone, error.message)
            ValidationField.DATA_NASCIMENTO  -> showValidationError(etDataNascimento, error.message)
            ValidationField.SENHA            -> showValidationError(etSenha, error.message)
            ValidationField.SENHA_CONFIRMACAO -> showValidationError(etConfirmarSenha, error.message)
            else                             -> showToast(error.message)
        }
    }
}