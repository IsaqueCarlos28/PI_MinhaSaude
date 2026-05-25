package com.example.medicoapplication.activities.auth_e_cadastro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.activities.medico.HomeMedicoActivity
import com.example.medicoapplication.activities.paciente.HomePacienteActivity
import com.example.medicoapplication.activities.auth_e_cadastro.viewmodel.LoginViewModel
import com.example.medicoapplication.data.remote.DTO.login.Role
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    private var tipoUsuario = "Paciente"
    private lateinit var tabLayout: TabLayout
    private lateinit var inputEmail: EditText
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
        observeViewModel()
    }

    private fun bindViews() {
        tabLayout      = findViewById(R.id.tabUserType)
        inputEmail     = findViewById(R.id.etEmail)
        inputSenha     = findViewById(R.id.etPassword)
        botaoLogin     = findViewById(R.id.btnLogin)
        tvIrCadastro   = findViewById(R.id.tvIrParaCadastro)
        tvEsqueciSenha = findViewById(R.id.tvEsqueciSenha)
    }

    private fun setupTabListener() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tipoUsuario = tab?.text.toString()
                inputEmail.text?.clear()
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
        val email = inputEmail.text.toString().trim()
        val senha = inputSenha.text.toString().trim()

        if (email.isEmpty()) { inputEmail.error = "Informe o e-mail"; inputEmail.requestFocus(); return }
        if (senha.isEmpty()) { inputSenha.error = "Informe a senha"; inputSenha.requestFocus(); return }

        viewModel.login(email, senha)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is LoginViewModel.UiState.Idle    -> setLoading(false)
                    is LoginViewModel.UiState.Loading -> setLoading(true)
                    is LoginViewModel.UiState.Error   -> {
                        setLoading(false)
                        Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_LONG).show()
                        viewModel.resetState()
                    }
                    is LoginViewModel.UiState.Success -> {
                        val usuario = state.usuario
                        when (usuario.role) {
                            Role.PACIENTE -> startActivity(
                                Intent(this@LoginActivity, HomePacienteActivity::class.java).apply {
                                    putExtra("ID_PACIENTE", usuario.id)
                                    putExtra("EMAIL_PACIENTE", usuario.email)
                                }
                            )
                            Role.MEDICO -> startActivity(
                                Intent(this@LoginActivity, HomeMedicoActivity::class.java).apply {
                                    putExtra("ID_MEDICO", usuario.id)
                                    putExtra("NOME_MEDICO", usuario.email.substringBefore("@"))
                                }
                            )
                        }
                        finish()
                    }
                }
            }
        }
    }

    private fun setLoading(carregando: Boolean) {
        botaoLogin.isEnabled = !carregando
        botaoLogin.text = if (carregando) "Entrando..." else "Login"
    }
}
