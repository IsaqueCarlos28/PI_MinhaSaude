package com.example.medicoapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomePacienteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // O nome do arquivo deve ser activity_home_paciente.xml
        setContentView(R.layout.activity_home_paciente)

        // Referenciando o botão de sair pelo ID do seu XML
        val btnSair = findViewById<Button>(R.id.btnLogoutPaciente)

        btnSair.setOnClickListener {
            // Volta para a LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Encerra esta activity
        }
    }
}