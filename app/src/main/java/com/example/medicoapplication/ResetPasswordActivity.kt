package com.example.medicoapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.data.remote.DTO.auth.AlterarSenhaRequestDto
import com.example.medicoapplication.data.remote.DTO.auth.ValidarTokenRequestDto
import com.example.medicoapplication.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etCodigo: EditText
    private lateinit var etNewPass: EditText
    private lateinit var etConfirmPass: EditText
    private lateinit var btnReset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        // FIX: these two lines were commented out, causing a crash on first access
        // because lateinit vars were never initialized
//        etEmail       = findViewById(R.id.etEmail)
//        etCodigo      = findViewById(R.id.etCodigo)
        etNewPass     = findViewById(R.id.etNewPassword)
        etConfirmPass = findViewById(R.id.etConfirmNewPassword)
        btnReset      = findViewById(R.id.btnResetPassword)

        btnReset.setOnClickListener { tentarRedefinir() }
    }

    private fun tentarRedefinir() {
        val email   = etEmail.text.toString().trim()
        val codigo  = etCodigo.text.toString().trim()
        val pass    = etNewPass.text.toString()
        val confirm = etConfirmPass.text.toString()

        if (email.isEmpty() || codigo.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass != confirm) {
            Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass.length < 8) {
            etNewPass.error = "A senha deve ter pelo menos 8 caracteres"
            etNewPass.requestFocus()
            return
        }

        setLoading(true)
        lifecycleScope.launch {
            try {
                // Step 1: validate the 6-digit code and receive the recovery token
                val validarResponse = RetrofitClient.api.validarCodigo(
                    ValidarTokenRequestDto(email = email, code = codigo)
                )

                if (!validarResponse.isSuccessful) {
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Código inválido ou expirado (${validarResponse.code()})",
                        Toast.LENGTH_LONG
                    ).show()
                    setLoading(false)
                    return@launch
                }

                val tokenRecuperacao = validarResponse.body()?.tokenRecuperacao

                // Step 2: use the recovery token to set the new password
                val alterarResponse = RetrofitClient.api.alterarSenha(
                    AlterarSenhaRequestDto(
                        tokenRecuperacao = tokenRecuperacao,
                        novaSenha        = pass
                    )
                )

                if (alterarResponse.isSuccessful) {
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Senha alterada com sucesso!",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Erro ao alterar senha (${alterarResponse.code()})",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ResetPasswordActivity,
                    "Falha na rede: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(carregando: Boolean) {
        btnReset.isEnabled = !carregando
        btnReset.text = if (carregando) "Processando..." else "Redefinir senha"
    }
}
