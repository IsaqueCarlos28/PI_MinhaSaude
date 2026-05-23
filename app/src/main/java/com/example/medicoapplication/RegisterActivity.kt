package com.example.medicoapplication

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
<<<<<<< HEAD
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteCreateRequestDto
import com.example.medicoapplication.data.remote.RetrofitClient
import kotlinx.coroutines.*

class RegisterActivity : AppCompatActivity() {

=======
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.data.remote.DTO.Genero
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteCreateRequestDto
import com.example.medicoapplication.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var btnVoltar: TextView
    private lateinit var etUsuario: EditText
    private lateinit var etCpf: EditText
    private lateinit var etEmail: EditText
    private lateinit var etTelefone: EditText
    private lateinit var etGenero: EditText
    private lateinit var etDataNascimento: EditText
    private lateinit var etSenha: EditText
    private lateinit var etConfirmarSenha: EditText
    private lateinit var btnCadastrar: Button

>>>>>>> 2ace8766d38c78a681c89f28b8086b9ad78212c2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_paciente)

        val etNome: EditText = findViewById(R.id.etNome)
        val etCpf: EditText = findViewById(R.id.etCpf)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etTelefone: EditText = findViewById(R.id.etTelefone)
        val etSenha: EditText = findViewById(R.id.etSenha)
        val etGenero: EditText = findViewById(R.id.etGenero)
        val etDataNascimento: EditText = findViewById(R.id.etDataNascimento)
        val spUf: Spinner = findViewById(R.id.spUf)
        val btnCadastrar: Button = findViewById(R.id.btnCadastrar)

        btnCadastrar.setOnClickListener {
            val paciente = PacienteCreateRequestDto(
                nome = etNome.text.toString(),
                cpf = etCpf.text.toString(),
                email = etEmail.text.toString(),
                telefone = etTelefone.text.toString(),
                senha = etSenha.text.toString(),
                genero = etGenero.text.toString(),
                dataNascimento = etDataNascimento.text.toString(),
                uf = spUf.selectedItem.toString()
            )

            cadastrarPaciente(paciente)
        }
    }

<<<<<<< HEAD
    private fun cadastrarPaciente(paciente: PacienteCreateRequestDto) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.api.cadastrarPaciente(paciente)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "Paciente cadastrado com sucesso!", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Erro: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
=======
    private fun bindViews() {
        btnVoltar        = findViewById(R.id.btnVoltar)
        etUsuario        = findViewById(R.id.etUsuario)
        etCpf            = findViewById(R.id.etCpf)
        etEmail          = findViewById(R.id.etEmail)
        etTelefone       = findViewById(R.id.etTelefone)
        etGenero         = findViewById(R.id.etGenero)
        etDataNascimento = findViewById(R.id.etDataNascimento)
        etSenha          = findViewById(R.id.etSenha)
        etConfirmarSenha = findViewById(R.id.etConfirmarSenha)
        btnCadastrar     = findViewById(R.id.btnCadastrar)
    }

    private fun setupListeners() {
        btnVoltar.setOnClickListener { finish() }
        btnCadastrar.setOnClickListener { tentarCadastro() }
    }

    private fun tentarCadastro() {
        val usuario        = etUsuario.text.toString().trim()
        val cpf            = etCpf.text.toString().trim()
        val email          = etEmail.text.toString().trim()
        val telefone       = etTelefone.text.toString().trim()
        val genero         = etGenero.text.toString().trim()
        val dataNasc       = etDataNascimento.text.toString().trim()
        val senha          = etSenha.text.toString().trim()
        val confirmarSenha = etConfirmarSenha.text.toString().trim()

        // 1. Campos vazios
        if (usuario.isEmpty() || cpf.isEmpty() || email.isEmpty() || telefone.isEmpty() ||
            genero.isEmpty() || dataNasc.isEmpty() || senha.isEmpty() ||
            confirmarSenha.isEmpty()
        ) {
            mostrarErro("Preencha todos os campos!")
            return
        }

        // 2. Senhas coincidem
        if (senha != confirmarSenha) {
            etConfirmarSenha.error = "As senhas não coincidem"
            etConfirmarSenha.requestFocus()
            return
        }

        // 3. Formata a data (DD/MM/AAAA -> YYYY-MM-DD)
        val dataFormatada = converterDataParaApi(dataNasc)
        if (dataFormatada == null) {
            etDataNascimento.error = "Use o formato DD/MM/AAAA"
            etDataNascimento.requestFocus()
            return
        }

        // 4. Limpa o CPF
        val cpfLimpo = cpf.replace(Regex("[^0-9]"), "")
        if (cpfLimpo.length != 11) {
            etCpf.error = "CPF deve conter 11 dígitos"
            etCpf.requestFocus()
            return
        }

        // 5. Converte gênero para enum
        // FIX: map the text input to the typed Genero enum
        val generoEnum: Genero = when (genero.lowercase()) {
            "masculino", "m" -> Genero.MASCULINO
            "feminino", "f"  -> Genero.FEMININO
            else             -> Genero.OUTRO
        }

        // 6. Monta o DTO
        // FIX: import path is now DTO.paciente, not usuario.paciente
        // FIX: genero field is now typed as Genero enum, not String
        val request = PacienteCreateRequestDto(
            nome           = usuario,
            cpf            = cpfLimpo,
            email          = email,
            telefone       = telefone,
            genero         = generoEnum,
            dataNascimento = dataFormatada,
            senha          = senha
        )

        realizarCadastro(request)
    }

    private fun realizarCadastro(request: PacienteCreateRequestDto) {
        setLoading(true)
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.createPaciente(request)
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Cadastro realizado!",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    mostrarErro("Erro ${response.code()}: Verifique os dados e tente novamente.")
>>>>>>> 2ace8766d38c78a681c89f28b8086b9ad78212c2
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Falha: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
<<<<<<< HEAD
=======

    private fun converterDataParaApi(entrada: String): String? {
        return try {
            val dataLimpa = entrada.trim().replace(".", "/").replace("-", "/")
            if (dataLimpa.length != 10 || !dataLimpa.contains("/")) return null
            val partes = dataLimpa.split("/")
            if (partes.size == 3) "${partes[2]}-${partes[1]}-${partes[0]}" else null
        } catch (e: Exception) {
            null
        }
    }

    private fun mostrarErro(mensagem: String) {
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
    }

    private fun setLoading(carregando: Boolean) {
        btnCadastrar.isEnabled = !carregando
        btnCadastrar.text = if (carregando) "Processando..." else "Cadastrar"
    }
>>>>>>> 2ace8766d38c78a681c89f28b8086b9ad78212c2
}
