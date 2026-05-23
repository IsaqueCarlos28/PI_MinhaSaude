package com.example.medicoapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.data.remote.DTO.auth.LoginRequestDto
import com.example.medicoapplication.data.remote.RetrofitClient
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private var tipoUsuario = "Paciente"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val tabLayout         = findViewById<TabLayout>(R.id.tabUserType)
        val inputUsuario      = findViewById<EditText>(R.id.etUser)
        val inputSenha        = findViewById<EditText>(R.id.etPassword)
        val botaoLogin        = findViewById<Button>(R.id.btnLogin)
        val btnIrParaCadastro = findViewById<TextView>(R.id.tvIrParaCadastro)
        val tvEsqueciSenha    = findViewById<TextView>(R.id.tvEsqueciSenha)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tipoUsuario = tab?.text.toString()
                inputUsuario.hint =
                    if (tipoUsuario == "Medico") "CRM ou E-mail" else "Usuário ou CPF"
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

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
                        LoginRequestDto(email = email, senha = senha)
                    )

                    if (response.isSuccessful) {
                        val usuario = response.body()
                        Toast.makeText(
                            this@LoginActivity,
                            "Login realizado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()

                        val destino =
                            if (usuario?.role == "MEDICO") HomeMedicoActivity::class.java
                            else HomePacienteActivity::class.java

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
                    Toast.makeText(this@LoginActivity, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        btnIrParaCadastro.setOnClickListener {
            val destino =
                if (tipoUsuario == "Medico") CadastroMedicoActivity::class.java
                else RegisterActivity::class.java
            startActivity(Intent(this, destino))
        }

        tvEsqueciSenha.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
}
