package com.example.medicoapplication

import android.content.Intent // Importação necessária para mudar de tela
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // 1. Referências dos componentes
        val btnVoltar = findViewById<Button>(R.id.btnVoltarEsqueceu)
        val btnEnviar = findViewById<Button>(R.id.btnEnviarLink)
        val etEmail = findViewById<EditText>(R.id.etEmailRecuperacao)

        // 2. Lógica do botão Voltar
        btnVoltar.setOnClickListener {
            finish() // Fecha essa tela e volta para o Login
        }

        // 3. Lógica do botão Enviar Link
        btnEnviar.setOnClickListener {
            val email = etEmail.text.toString()

            if (email.isNotEmpty()) {
                // Mostra o aviso de sucesso
                Toast.makeText(this, "Link enviado para: $email", Toast.LENGTH_SHORT).show()

                // Abre a tela de Redefinir Senha
                val intent = Intent(this, ResetPasswordActivity::class.java)
                startActivity(intent)

                // Fecha a tela de "Esqueci Senha"
                finish()
            } else {
                etEmail.error = "Por favor, digite seu e-mail"
            }
        }
    }
}