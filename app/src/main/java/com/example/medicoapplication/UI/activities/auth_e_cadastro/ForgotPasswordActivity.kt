package com.example.medicoapplication.UI.activities.auth_e_cadastro

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.common.mappers.ErrorMapper
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.viewmodel.auth.AuthViewModel
import kotlinx.coroutines.launch

class ForgotPasswordActivity : BaseActivity() {

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var btnVoltar: TextView
    private lateinit var etEmail: EditText
    private lateinit var btnEnviarLink: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        btnVoltar     = findViewById(R.id.btnVoltar)
        etEmail       = findViewById(R.id.etEmail)
        btnEnviarLink = findViewById(R.id.btnEnviarLink)

        btnVoltar.setOnClickListener { finish() }
        btnEnviarLink.setOnClickListener { tentarEnviarLink() }
        observeViewModel()
    }

    private fun tentarEnviarLink() {
        val email = etEmail.text.toString().trim()
        if (email.isEmpty()) { etEmail.error = "Informe o e-mail cadastrado"; etEmail.requestFocus(); return }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { etEmail.error = "E-mail inválido"; etEmail.requestFocus(); return }

        viewModel.esqueceuSenha(email)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is AuthViewModel.UiState.Idle    -> setLoading(false)
                    is AuthViewModel.UiState.Loading -> setLoading(true)
                    is AuthViewModel.UiState.Error   -> {
                        setLoading(false)
                        handleError(state.error)
                        viewModel.resetState()
                    }
                    is AuthViewModel.UiState.Success -> {
                        showToast("Se o e-mail estiver cadastrado, você receberá o código em breve.")
                        val email = etEmail.text.toString().trim()
                        startActivity(
                            Intent(
                                this@ForgotPasswordActivity,
                                ValidarCodigoResetPasswordActivity::class.java
                            ).putExtra("Email", email)
                        )
                        finish()
                    }
                }
            }
        }
    }

    private fun setLoading(carregando: Boolean) {
        btnEnviarLink.isEnabled = !carregando
        btnEnviarLink.text = if (carregando) "Enviando..." else "Enviar link"
    }
}
