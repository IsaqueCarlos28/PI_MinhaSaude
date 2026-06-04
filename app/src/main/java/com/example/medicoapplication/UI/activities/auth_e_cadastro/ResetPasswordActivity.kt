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
import com.example.medicoapplication.UI.common.validations.ResetPasswordValidator
import com.example.medicoapplication.UI.common.validations.ValidationField
import com.example.medicoapplication.UI.common.validations.ValidationResult
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

        val token = intent.getStringExtra("Token")

        btnReset.setOnClickListener { tentarRedefinir(token) }
        observeViewModel()
    }

    private fun tentarRedefinir(token: String?) {
        val nova      = etNewPass.text.toString()
        val confirmacao = etConfirmPass.text.toString()

        when (val result = ResetPasswordValidator.validar(nova, confirmacao)) {
            is ValidationResult.Success -> {
                viewModel.setToken(token)
                viewModel.alterarSenha(nova)
            }
            is ValidationResult.Error -> when (result.field) {
                ValidationField.SENHA             -> showValidationError(etNewPass, result.message)
                ValidationField.SENHA_CONFIRMACAO -> showValidationError(etConfirmPass, result.message)
                else                              -> showToast(result.message)
            }
        }
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
                        showToast("Senha alterada com sucesso!")
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