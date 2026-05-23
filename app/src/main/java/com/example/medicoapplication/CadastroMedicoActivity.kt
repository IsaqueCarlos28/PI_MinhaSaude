package com.example.medicoapplication

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.medicoapplication.data.remote.DTO.medico.MedicoCreateRequestDto
import com.example.medicoapplication.data.remote.RetrofitClient
import kotlinx.coroutines.*

class CadastroMedicoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_medico)

        val etNome: EditText = findViewById(R.id.etNome)
        val etCrm: EditText = findViewById(R.id.etCrm)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etTelefone: EditText = findViewById(R.id.etTelefone)
        val etSenha: EditText = findViewById(R.id.etSenha)
        val etEspecialidade: EditText = findViewById(R.id.etEspecialidade)
        val spUf: Spinner = findViewById(R.id.spUf)
        val btnCadastrar: Button = findViewById(R.id.btnCadastrar)

        btnCadastrar.setOnClickListener {
            val medico = MedicoCreateRequestDto(
                nome = etNome.text.toString(),
                crm = etCrm.text.toString(),
                email = etEmail.text.toString(),
                telefone = etTelefone.text.toString(),
                senha = etSenha.text.toString(),
                especialidade = etEspecialidade.text.toString(),
                uf = spUf.selectedItem.toString()
            )

            cadastrarMedico(medico)
        }
    }

    private fun cadastrarMedico(medico: MedicoCreateRequestDto) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.api.cadastrarMedico(medico)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@CadastroMedicoActivity, "Médico cadastrado com sucesso!", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@CadastroMedicoActivity, "Erro: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CadastroMedicoActivity, "Falha: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
