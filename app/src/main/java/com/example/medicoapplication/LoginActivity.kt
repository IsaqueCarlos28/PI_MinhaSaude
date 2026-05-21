package com.example.medicoapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.data.remote.DTO.login.LoginRequestDto
import com.example.medicoapplication.data.remote.RetrofitClient
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private var tipoUsuario = "Paciente"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Referências do XML
        val tabLayout = findViewById<TabLayout>(R.id.tabUserType)
        val inputUsuario = findViewById<EditText>(R.id.etUser)
        val inputSenha = findViewById<EditText>(R.id.etPassword)
        val botaoLogin = findViewById<Button>(R.id.btnLogin)
        val btnIrParaCadastro = findViewById<TextView>(R.id.tvIrParaCadastro)

        // Alternar entre Paciente e Médico
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tipoUsuario = tab?.text.toString()
                inputUsuario.hint =
                    if (tipoUsuario == "Medico") "CRM ou E-mail" else "Usuário ou CPF"
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Lógica de Login
        botaoLogin.setOnClickListener {

            val email = inputUsuario.text.toString().trim()
            val senha = inputSenha.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {

                try {

                    val response = RetrofitClient.api.login(
                        LoginRequestDto(
                            email = email,
                            senha = senha
                        )
                    )

                    if (response.isSuccessful) {

                        val usuario = response.body()

                        Toast.makeText(
                            this@LoginActivity,
                            "Login realizado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()

                        val destino =
                            if (usuario?.role == "MEDICO")
                                HomeMedicoActivity::class.java
                            else
                                HomePacienteActivity::class.java

                        startActivity(Intent(this@LoginActivity, destino))
                        finish()

                    } else {

                        Toast.makeText(
                            this@LoginActivity,
                            "Usuário ou senha inválidos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: Exception) {

                    Toast.makeText(
                        this@LoginActivity,
                        "Erro: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Navegar para o cadastro ao clicar em "Não tem uma conta?"
        btnIrParaCadastro.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
