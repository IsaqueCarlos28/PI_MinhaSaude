package com.example.medicoapplication.activities.auth_e_cadastro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.viewmodel.auth.CadastroViewModel
import com.example.medicoapplication.data.remote.DTO.Genero
import com.example.medicoapplication.data.remote.DTO.medico.MedicoCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteCreateRequestDto
import kotlinx.coroutines.launch

class CadastroMedicoActivity : AppCompatActivity() {

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
        val nome      = etNome.text.toString().trim()
        val email     = etEmail.text.toString().trim()
        val cpf       = etCpf.text.toString().trim()
        val telefone  = etTelefone.text.toString().trim()
        val dataNasc  = etDataNascimento.text.toString().trim()
        val genero    = spGenero.selectedItem.toString()  // FIX: read from Spinner
        val senha     = etSenha.text.toString().trim()
        val crmDigits = etCrmDigitos.text.toString().trim()
        val uf        = spUf.selectedItem.toString()

        if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty() || telefone.isEmpty() ||
            dataNasc.isEmpty() || senha.isEmpty() || crmDigits.isEmpty()
        ) { Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show(); return }

        if (crmDigits.length != 6 || !crmDigits.all { it.isDigit() }) {
            etCrmDigitos.error = "O CRM deve conter exatamente 6 dígitos numéricos"
            etCrmDigitos.requestFocus(); return
        }

        val dataFormatada = converterDataParaApi(dataNasc)
            ?: run { etDataNascimento.error = "Use o formato DD/MM/AAAA"; etDataNascimento.requestFocus(); return }

        val cpfLimpo = cpf.replace(Regex("[^0-9]"), "")
        if (cpfLimpo.length != 11) { etCpf.error = "CPF deve conter 11 dígitos"; etCpf.requestFocus(); return }

        val generoEnum = when (genero.uppercase()) {
            "MASCULINO" -> Genero.MASCULINO
            "FEMININO"  -> Genero.FEMININO
            else        -> Genero.OUTRO
        }

        viewModel.cadastrarMedico(
            MedicoCreateRequestDto(
                usuarioBase = PacienteCreateRequestDto(
                    nome           = nome,
                    cpf            = cpfLimpo,
                    email          = email,
                    telefone       = telefone,
                    genero         = generoEnum,
                    dataNascimento = dataFormatada,
                    senha          = senha
                ),
                crmUf     = uf,
                crmDigits = crmDigits
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
                        Toast.makeText(this@CadastroMedicoActivity, state.message, Toast.LENGTH_LONG).show()
                        viewModel.resetState()
                    }
                    is CadastroViewModel.UiState.Success -> {
                        Toast.makeText(this@CadastroMedicoActivity, "Médico cadastrado com sucesso!", Toast.LENGTH_LONG).show()
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

    private fun converterDataParaApi(entrada: String): String? {
        return try {
            val clean = entrada.trim().replace(".", "/").replace("-", "/")
            if (clean.length != 10 || !clean.contains("/")) return null
            val p = clean.split("/")
            if (p.size == 3) "${p[2]}-${p[1]}-${p[0]}" else null
        } catch (e: Exception) { null }
    }
}
