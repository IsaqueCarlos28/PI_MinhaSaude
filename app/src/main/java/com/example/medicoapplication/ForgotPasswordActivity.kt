package com.example.medicoapplication

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.data.remote.DTO.auth.EsqueceuSenhaRequestDto
import com.example.medicoapplication.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var btnVoltar: TextView
    private lateinit var etEmail: EditText
    private lateinit var btnEnviarLink: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        bindViews()
        setupListeners()
    }

    private fun bindViews() {
        btnVoltar     = findViewById(R.id.btnVoltar)
        etEmail       = findViewById(R.id.etEmail)
        btnEnviarLink = findViewById(R.id.btnEnviarLink)
    }

    private fun setupListeners() {
        btnVoltar.setOnClickListener { finish() }
        btnEnviarLink.setOnClickListener { tentarEnviarLink() }
    }

    private fun tentarEnviarLink() {
        val email = etEmail.text.toString().trim()

        if (email.isEmpty()) {
            etEmail.error = "Informe o e-mail cadastrado"
            etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "E-mail inválido"
            etEmail.requestFocus()
            return
        }

        setLoading(true)
        // FIX: was a TODO — now calls POST auth/esqueceu-a-senha with EsqueceuSenhaRequestDto
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.esqueceuSenha(
                    EsqueceuSenhaRequestDto(email = email)
                )
                // API always returns 200 regardless of whether the e-mail exists (security)
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Se o e-mail estiver cadastrado, você receberá o código em breve.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Erro ao enviar solicitação (${response.code()})",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Falha na rede: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(carregando: Boolean) {
        btnEnviarLink.isEnabled = !carregando
        btnEnviarLink.text = if (carregando) "Enviando..." else "Enviar link"
    }
}
