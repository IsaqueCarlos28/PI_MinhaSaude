package com.example.medicoapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 1. Referências dos componentes do XML
        val btnVoltar = findViewById<TextView>(R.id.btnVoltar)
        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)

        // 2. Lógica do botão Voltar
        btnVoltar.setOnClickListener {
            finish() // Apenas fecha esta tela e volta para o Login
        }

        // 3. Lógica do botão Cadastrar (Ir para a Home do Paciente)
        btnCadastrar.setOnClickListener {
            Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()

            // Cria a intenção para ir para a Home do Paciente
            val intent = Intent(this, HomePacienteActivity::class.java)
            startActivity(intent)

            // Finaliza todas as telas anteriores para o usuário não voltar ao cadastro
            finishAffinity()
        }
    }
}