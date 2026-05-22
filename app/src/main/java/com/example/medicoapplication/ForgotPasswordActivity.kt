package com.example.medicoapplication

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {

    // ── Views ─────────────────────────────────────────────────────────────────
    private lateinit var btnVoltar: TextView
    private lateinit var etEmail: EditText
    private lateinit var btnEnviarLink: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        bindViews()
        setupListeners()
    }

    // ── Bind ──────────────────────────────────────────────────────────────────
    private fun bindViews() {
        btnVoltar     = findViewById(R.id.btnVoltar)
        etEmail       = findViewById(R.id.etEmail)
        btnEnviarLink = findViewById(R.id.btnEnviarLink)
    }

    // ── Listeners ─────────────────────────────────────────────────────────────
    private fun setupListeners() {
        btnVoltar.setOnClickListener { finish() }
        btnEnviarLink.setOnClickListener { tentarEnviarLink() }
    }

    // ── Lógica principal ──────────────────────────────────────────────────────
    private fun tentarEnviarLink() {
        val email = etEmail.text.toString().trim()

        // Validação: campo vazio
        if (email.isEmpty()) {
            etEmail.error = "Informe o e-mail cadastrado"
            etEmail.requestFocus()
            return
        }

        // Validação: formato de e-mail
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "E-mail inválido"
            etEmail.requestFocus()
            return
        }

        // TODO: integrar com o endpoint da API quando disponível.
        // Exemplo:
        //   lifecycleScope.launch {
        //       val response = RetrofitClient.api.solicitarRecuperacaoSenha(email)
        //       if (response.isSuccessful) { ... } else { ... }
        //   }

        // Por enquanto exibe confirmação e volta para o Login
        Toast.makeText(
            this,
            "Link de recuperação enviado para $email",
            Toast.LENGTH_LONG
        ).show()

        finish() // volta para LoginActivity
    }
}