package com.example.medicoapplication.activities.auth_e_cadastro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.activities.medico.HomeMedicoActivity
import com.example.medicoapplication.activities.paciente.HomePacienteActivity
import com.example.medicoapplication.data.remote.DTO.login.LoginRequestDto
import com.example.medicoapplication.data.remote.DTO.login.Role
import com.example.medicoapplication.data.remote.RetrofitClient
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private var tipoUsuario = "Paciente"

    private lateinit var tabLayout: TabLayout
    private lateinit var inputUsuario: EditText
    private lateinit var inputSenha: EditText
    private lateinit var botaoLogin: Button
    private lateinit var tvIrCadastro: TextView
    private lateinit var tvEsqueciSenha: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        bindViews()
        setupTabListener()
        setupClickListeners()
    }

    private fun bindViews() {
        tabLayout      = findViewById(R.id.tabUserType)
        inputUsuario   = findViewById(R.id.etUser)
        inputSenha     = findViewById(R.id.etPassword)
        botaoLogin     = findViewById(R.id.btnLogin)
        tvIrCadastro   = findViewById(R.id.tvIrParaCadastro)
        tvEsqueciSenha = findViewById(R.id.tvEsqueciSenha)
    }

    private fun setupTabListener() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tipoUsuario = tab?.text.toString()
                inputUsuario.hint = if (tipoUsuario == "Medico") "CRM ou E-mail" else "E-mail ou CPF"
                inputUsuario.text?.clear()
                inputSenha.text?.clear()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupClickListeners() {
        botaoLogin.setOnClickListener { tentarLogin() }

        tvIrCadastro.setOnClickListener {
            val destino = if (tipoUsuario == "Medico")
                CadastroMedicoActivity::class.java
            else
                CadastroPacienteActivity::class.java
            startActivity(Intent(this, destino))
        }

        tvEsqueciSenha.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun tentarLogin() {
        val email = inputUsuario.text.toString().trim()
        val senha = inputSenha.text.toString().trim()

        if (email.isEmpty()) {
            inputUsuario.error = "Informe o e-mail"
            inputUsuario.requestFocus()
            return
        }
        if (senha.isEmpty()) {
            inputSenha.error = "Informe a senha"
            inputSenha.requestFocus()
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.login(
                    LoginRequestDto(email = email, senha = senha)
                )

                if (response.isSuccessful) {
                    val usuario = response.body()

                    if (usuario == null) {
                        Toast.makeText(this@LoginActivity, "Resposta inesperada do servidor.", Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    when (usuario.role) {
                        Role.PACIENTE -> {
                            startActivity(
                                Intent(this@LoginActivity, HomePacienteActivity::class.java).apply {
                                    putExtra("ID_PACIENTE", usuario.id)
                                    putExtra("EMAIL_PACIENTE", usuario.email)
                                }
                            )
                            finish()
                        }
                        Role.MEDICO -> {
                            startActivity(
                                Intent(this@LoginActivity, HomeMedicoActivity::class.java).apply {
                                    putExtra("ID_MEDICO", usuario.id)
                                    putExtra("NOME_MEDICO", usuario.email.substringBefore("@"))
                                }
                            )
                            finish()
                        }
                    }

                } else {
                    val mensagem = when (response.code()) {
                        401  -> "E-mail ou senha incorretos."
                        403  -> "Acesso negado. Verifique seu tipo de conta."
                        else -> "Erro ${response.code()}. Tente novamente."
                    }
                    Toast.makeText(this@LoginActivity, mensagem, Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Sem conexao com o servidor. Verifique sua internet.", Toast.LENGTH_LONG).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(carregando: Boolean) {
        botaoLogin.isEnabled = !carregando
        botaoLogin.text      = if (carregando) "Entrando..." else "Login"
    }
}
