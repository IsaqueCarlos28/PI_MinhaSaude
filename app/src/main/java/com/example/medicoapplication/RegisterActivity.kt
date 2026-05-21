package com.example.medicoapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteCreateRequestDto
import com.example.medicoapplication.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    // Declaração dos componentes da UI
    private lateinit var btnVoltar: TextView
    private lateinit var etUsuario: EditText
    private lateinit var etCpf: EditText
    private lateinit var etEmail: EditText
    private lateinit var etGenero: EditText
    private lateinit var etDataNascimento: EditText
    private lateinit var etSenha: EditText
    private lateinit var etConfirmarSenha: EditText
    private lateinit var btnCadastrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 1. Referências dos componentes do XML (Binding)
        bindViews()

        // 2. Configuração dos cliques (Listeners)
        setupListeners()
    }

    private fun bindViews() {
        btnVoltar        = findViewById(R.id.btnVoltar)
        etUsuario        = findViewById(R.id.etUsuario)
        etCpf            = findViewById(R.id.etCpf)
        etEmail          = findViewById(R.id.etEmail)
        etGenero         = findViewById(R.id.etGenero)
        etDataNascimento = findViewById(R.id.etDataNascimento)
        etSenha          = findViewById(R.id.etSenha)
        etConfirmarSenha = findViewById(R.id.etConfirmarSenha)
        btnCadastrar     = findViewById(R.id.btnCadastrar)
    }

    private fun setupListeners() {
        // Lógica do botão Voltar
        btnVoltar.setOnClickListener {
            finish()
        }

        // Lógica do botão Cadastrar
        btnCadastrar.setOnClickListener {
            tentarCadastro()
        }
    }

    // 3. Validação dos campos e preparação dos dados
    private fun tentarCadastro() {
        val usuario        = etUsuario.text.toString().trim()
        val cpf            = etCpf.text.toString().trim()
        val email          = etEmail.text.toString().trim()
        val genero         = etGenero.text.toString().trim()
        val dataNasc       = etDataNascimento.text.toString().trim()
        val senha          = etSenha.text.toString().trim()
        val confirmarSenha = etConfirmarSenha.text.toString().trim()

        // Validação de campos vazios
        if (usuario.isEmpty() || cpf.isEmpty() || email.isEmpty() ||
            genero.isEmpty() || dataNasc.isEmpty() || senha.isEmpty() ||
            confirmarSenha.isEmpty()
        ) {
            mostrarErro("Preencha todos os campos!")
            return
        }

        // Validação de formato de E-mail
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "E-mail inválido"
            etEmail.requestFocus()
            return
        }

        // Validação de CPF (apenas 11 dígitos)
        val cpfLimpo = cpf.replace(Regex("[^0-9]"), "")
        if (cpfLimpo.length != 11) {
            etCpf.error = "CPF deve conter 11 dígitos"
            etCpf.requestFocus()
            return
        }

        // Conversão de Data para padrão da API (YYYY-MM-DD)
        val dataFormatada = converterData(dataNasc)
        if (dataFormatada == null) {
            etDataNascimento.error = "Use o formato DD/MM/AAAA"
            etDataNascimento.requestFocus()
            return
        }

        // Verificação de senha idêntica
        if (senha != confirmarSenha) {
            etConfirmarSenha.error = "As senhas não coincidem"
            etConfirmarSenha.requestFocus()
            return
        }

        // Criação do objeto DTO para envio
        val request = PacienteCreateRequestDto(
            nome           = usuario,
            cpf            = cpfLimpo,
            email          = email,
            telefone       = "",
            genero         = genero,
            dataNascimento = dataFormatada,
            senha          = senha
        )

        // Dentro de tentarCadastro(), antes de criar o request:
        val generoFinal = when (genero.lowercase()) {
            "masculino", "m" -> "MASCULINO"
            "feminino", "f" -> "FEMININO"
            else -> genero.uppercase() // Envia o que o usuário digitou em Caps
        }

        realizarCadastro(request)
    }

    // 4. Comunicação com a API (Retrofit + Coroutines)
    private fun realizarCadastro(request: PacienteCreateRequestDto) {
        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.createPaciente(request)

                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Cadastro realizado! Faça o login.", Toast.LENGTH_LONG).show()

                    // Volta para a tela de Login limpando a pilha
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()

                } else {
                    // Tratamento de erros específicos da API
                    val mensagem = when (response.code()) {
                        400  -> "Dados inválidos. Verifique as informações."
                        409  -> "CPF ou e-mail já cadastrado."
                        else -> "Erro ao cadastrar. Tente novamente."
                    }
                    mostrarErro(mensagem)
                }
            } catch (e: Exception) {
                mostrarErro("Falha de conexão: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }

    private fun converterData(entrada: String): String? {
        return try {
            val parseFmt = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val emitFmt  = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            parseFmt.isLenient = false
            val date = parseFmt.parse(entrada) ?: return null
            emitFmt.format(date)
        } catch (e: Exception) {
            null
        }
    }

    private fun mostrarErro(mensagem: String) {
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
    }

    private fun setLoading(carregando: Boolean) {
        btnCadastrar.isEnabled = !carregando
        btnCadastrar.text      = if (carregando) "Cadastrando..." else "Cadastrar"
    }
}