package com.example.medicoapplication.activities.auth_e_cadastro

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
import com.example.medicoapplication.viewmodel.auth.CadastroViewModel
import com.example.medicoapplication.data.remote.DTO.Genero
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteCreateRequestDto
import kotlinx.coroutines.launch

class CadastroPacienteActivity : AppCompatActivity() {

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
        val nome     = etNome.text.toString().trim()
        val email    = etEmail.text.toString().trim()
        val cpf      = etCpf.text.toString().trim()
        val telefone = etTelefone.text.toString().trim()
        val dataNasc = etDataNascimento.text.toString().trim()
        val genero   = etGenero.text.toString().trim()
        val senha    = etSenha.text.toString().trim()
        val confirm  = etConfirmarSenha.text.toString().trim()

        if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty() || telefone.isEmpty() ||
            dataNasc.isEmpty() || genero.isEmpty() || senha.isEmpty() || confirm.isEmpty()
        ) { Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show(); return }

        if (senha != confirm) { etConfirmarSenha.error = "As senhas não coincidem"; etConfirmarSenha.requestFocus(); return }
        if (senha.length < 8) { etSenha.error = "A senha deve ter pelo menos 8 caracteres"; etSenha.requestFocus(); return }

        val dataFormatada = converterDataParaApi(dataNasc)
            ?: run { etDataNascimento.error = "Use o formato DD/MM/AAAA"; etDataNascimento.requestFocus(); return }

        val cpfLimpo = cpf.replace(Regex("[^0-9]"), "")
        if (cpfLimpo.length != 11) { etCpf.error = "CPF deve conter 11 dígitos"; etCpf.requestFocus(); return }

        val generoEnum = when (genero.lowercase()) {
            "masculino", "m" -> Genero.MASCULINO
            "feminino", "f"  -> Genero.FEMININO
            else             -> Genero.OUTRO
        }

        viewModel.cadastrarPaciente(
            PacienteCreateRequestDto(
                nome           = nome,
                cpf            = cpfLimpo,
                email          = email,
                telefone       = telefone,
                genero         = generoEnum,
                dataNascimento = dataFormatada,
                senha          = senha
            )
        )
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is CadastroViewModel.UiState.Idle    -> setLoading(false)
                    is CadastroViewModel.UiState.Loading -> setLoading(true)
                    is CadastroViewModel.UiState.Error   -> {
                        setLoading(false)
                        Toast.makeText(this@CadastroPacienteActivity, state.message, Toast.LENGTH_LONG).show()
                        viewModel.resetState()
                    }
                    is CadastroViewModel.UiState.Success -> {
                        Toast.makeText(this@CadastroPacienteActivity, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show()
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

    private fun converterDataParaApi(entrada: String): String? {
        return try {
            val clean = entrada.trim().replace(".", "/").replace("-", "/")
            if (clean.length != 10 || !clean.contains("/")) return null
            val p = clean.split("/")
            if (p.size == 3) "${p[2]}-${p[1]}-${p[0]}" else null
        } catch (e: Exception) { null }
    }
}
