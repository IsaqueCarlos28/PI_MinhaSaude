package com.example.medicoapplication.UI.activities.auth_e_cadastro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.data.remote.NetworkError
import com.example.medicoapplication.viewmodel.auth.AuthViewModel

import kotlinx.coroutines.launch

class ResetPasswordActivity : BaseActivity() {

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var etNewPass: EditText
    private lateinit var etConfirmPass: EditText
    private lateinit var btnReset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        etNewPass     = findViewById(R.id.etNewPassword)
        etConfirmPass = findViewById(R.id.etConfirmNewPassword)
        btnReset      = findViewById(R.id.btnResetPassword)

        // Receive the token that ValidarCodigoResetPasswordActivity put in the Intent
        val token = this.intent.getStringExtra("Token")

        btnReset.setOnClickListener { tentarRedefinir(token) }
        observeViewModel()
    }

    private fun tentarRedefinir(token: String?) {
        val pass    = etNewPass.text.toString()
        val confirm = etConfirmPass.text.toString()

        if (pass != confirm) { Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show(); return }
        if (pass.length < 8) { etNewPass.error = "A senha deve ter pelo menos 8 caracteres"; etNewPass.requestFocus(); return }

        // Inject the token into the ViewModel so alterarSenha() can use it
        viewModel.setToken(token)
        viewModel.alterarSenha(pass)
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
                        Toast.makeText(this@ResetPasswordActivity, mensagem, Toast.LENGTH_LONG).show()
                        viewModel.resetState()
                    }
                    is AuthViewModel.UiState.Success -> {
                        Toast.makeText(this@ResetPasswordActivity, "Senha alterada com sucesso!", Toast.LENGTH_LONG).show()
                        startActivity(
                            Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                                .apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP }
                        )
                        finish()
                    }
                }
            }
        }
    }

    private fun setLoading(carregando: Boolean) {
        btnReset.isEnabled = !carregando
        btnReset.text = if (carregando) "Processando..." else "Redefinir senha"
    }
}
