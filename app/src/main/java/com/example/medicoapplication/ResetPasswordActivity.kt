package com.example.medicoapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val etNewPass = findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPass = findViewById<EditText>(R.id.etConfirmNewPassword)
        val btnReset = findViewById<Button>(R.id.btnResetPassword)

        btnReset.setOnClickListener {
            val pass = etNewPass.text.toString()
            val confirm = etConfirmPass.text.toString()

            if (pass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Preencha os campos!", Toast.LENGTH_SHORT).show()
            } else if (pass != confirm) {
                Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show()
            } else {
                // Sucesso na redefinição
                Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_LONG).show()

                // Volta para o Login
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Limpa a pilha de telas
                startActivity(intent)
                finish()
            }
        }
    }
}