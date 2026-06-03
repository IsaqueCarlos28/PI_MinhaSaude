package com.example.medicoapplication.UI.activities.auth_e_cadastro

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
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.viewmodel.auth.AuthViewModel

import kotlinx.coroutines.launch

class ValidarCodigoResetPasswordActivity : BaseActivity() {

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var btnVoltar: TextView
    private lateinit var etCodigo: EditText
    private lateinit var btnEnviarCodigo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validar_code_reset_password)

        btnVoltar       = findViewById(R.id.btnVoltar)
        etCodigo        = findViewById(R.id.etCodigo)
        btnEnviarCodigo = findViewById(R.id.btnEnviarLink)

        btnVoltar.setOnClickListener { finish() }
        btnEnviarCodigo.setOnClickListener { validarCodigo() }
        observeViewModel()
    }

    private fun validarCodigo() {
        val email  = this.intent.getStringExtra("Email")
        val codigo = etCodigo.text.toString().trim()

        if (codigo.isEmpty()) { etCodigo.error = "Informe o código enviado para o e-mail"; etCodigo.requestFocus(); return }

        viewModel.validarCodigo(email, codigo)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is AuthViewModel.UiState.Idle    -> setLoading(false)
                    is AuthViewModel.UiState.Loading -> setLoading(true)
                    is AuthViewModel.UiState.Error   -> {
                        setLoading(false)
                        val mensagem = when (state.error) {
                            is NetworkError.NaoAutorizado ->
                                "Email ou senha incorretos. Verifique seus dados."
                            is NetworkError.SemConexao ->
                                "Sem conexão com a internet. Verifique sua rede."
                            is NetworkError.Timeout ->
                                "O servidor demorou para responder. Tente novamente."
                            is NetworkError.ErrroServidor ->
                                "Problema no servidor. Tente mais tarde."
                            is NetworkError.Desconhecido ->
                                "Erro inesperado: ${state.error.mensagem}"
                            else ->
                                "Algo deu errado. Tente novamente."
                        }
                        Toast.makeText(this@ValidarCodigoResetPasswordActivity, mensagem, Toast.LENGTH_LONG).show()
                        viewModel.resetState()
                    }
                    is AuthViewModel.UiState.Success -> {
                        // token is stored in viewModel.tokenRecuperacao;
                        // ResetPasswordActivity will receive its own AuthViewModel instance
                        // via viewModels(), so we pass the token via Intent.
                        startActivity(
                            Intent(this@ValidarCodigoResetPasswordActivity, ResetPasswordActivity::class.java)
                                .putExtra("Token", viewModel.tokenRecuperacao)
                        )
                        finish()
                    }
                }
            }
        }
    }

    private fun setLoading(carregando: Boolean) {
        btnEnviarCodigo.isEnabled = !carregando
        btnEnviarCodigo.text = if (carregando) "Validando..." else "Validar código"
    }
}
