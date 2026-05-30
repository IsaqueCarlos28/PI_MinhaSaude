package com.example.medicoapplication.activities.paciente

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.data.repository.AuthRepository
import kotlinx.coroutines.launch

/**
 * Tela de alteração de senha do paciente via segurança nas configurações.
 * Usa o endpoint PUT auth/alterar_senha (tokenRecuperacao = null = troca direta).
 */
class AlterarSenhaPacienteActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()
    private var idPaciente: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_seguranca)

        idPaciente = intent.getLongExtra("ID_PACIENTE", -1L)

        findViewById<LinearLayout>(R.id.btnVoltarSeguranca).setOnClickListener {
            finish()
        }

        findViewById<LinearLayout>(R.id.itemSegurancaDetalhe).setOnClickListener {
            mostrarDialogAlterarSenha()
        }
    }

    private fun mostrarDialogAlterarSenha() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_alterar_senha, null)
        val etNovaSenha    = dialogView.findViewById<EditText>(R.id.etNovaSenha)
        val etConfirmarSenha = dialogView.findViewById<EditText>(R.id.etConfirmarSenha)
        val btnCancelar    = dialogView.findViewById<Button>(R.id.btnCancelarSenha)
        val btnSalvar      = dialogView.findViewById<Button>(R.id.btnSalvarSenha)

        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        btnCancelar.setOnClickListener { dialog.dismiss() }

        btnSalvar.setOnClickListener {
            val nova      = etNovaSenha.text.toString().trim()
            val confirmar = etConfirmarSenha.text.toString().trim()

            if (nova.length < 6) {
                etNovaSenha.error = "Mínimo 6 caracteres"
                return@setOnClickListener
            }
            if (nova != confirmar) {
                etConfirmarSenha.error = "As senhas não coincidem"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                authRepository.alterarSenha(null, nova).fold(
                    onSuccess = {
                        dialog.dismiss()
                        Toast.makeText(
                            this@AlterarSenhaPacienteActivity,
                            "Senha alterada com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onFailure = { e ->
                        Toast.makeText(
                            this@AlterarSenhaPacienteActivity,
                            "Erro: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }
        }

        dialog.show()
    }
}
