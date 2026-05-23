package com.example.medicoapplication.activities.auth_e_cadastro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.data.remote.DTO.Genero
import com.example.medicoapplication.data.remote.DTO.medico.MedicoCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteCreateRequestDto
import com.example.medicoapplication.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class CadastroPacienteActivity : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var etEmail: EditText
    private lateinit var etCpf: EditText
    private lateinit var etTelefone: EditText
    private lateinit var etDataNascimento: EditText
    private lateinit var etGenero: EditText
    private lateinit var etSenha: EditText
    private lateinit var etCrmDigitos: EditText
    private lateinit var spUf: Spinner
    private lateinit var btnCadastrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_paciente)

        etNome           = findViewById(R.id.etUsuario)
        etEmail          = findViewById(R.id.etEmail)
        etCpf            = findViewById(R.id.etCpf)
        etTelefone       = findViewById(R.id.etTelefone)
        etDataNascimento = findViewById(R.id.etDataNascimento)
        etGenero         = findViewById(R.id.etGenero)
        etSenha          = findViewById(R.id.etSenha)
        btnCadastrar     = findViewById(R.id.btnCadastrar)

        btnCadastrar.setOnClickListener { tentarCadastro() }
    }

    private fun tentarCadastro() {
        val nome      = etNome.text.toString().trim()
        val email     = etEmail.text.toString().trim()
        val cpf       = etCpf.text.toString().trim()
        val telefone  = etTelefone.text.toString().trim()
        val dataNasc  = etDataNascimento.text.toString().trim()
        val genero    = etGenero.text.toString().trim()
        val senha     = etSenha.text.toString().trim()
        val crmDigits = etCrmDigitos.text.toString().trim()
        val uf        = spUf.selectedItem.toString()

        if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty() || telefone.isEmpty() ||
            dataNasc.isEmpty() || genero.isEmpty() || senha.isEmpty() || crmDigits.isEmpty()
        ) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }

        // FIX: CRM digits must be exactly 6
        if (crmDigits.length != 6 || !crmDigits.all { it.isDigit() }) {
            etCrmDigitos.error = "O CRM deve conter exatamente 6 dígitos numéricos"
            etCrmDigitos.requestFocus()
            return
        }

        val dataFormatada = converterDataParaApi(dataNasc)
        if (dataFormatada == null) {
            etDataNascimento.error = "Use o formato DD/MM/AAAA"
            etDataNascimento.requestFocus()
            return
        }

        val cpfLimpo = cpf.replace(Regex("[^0-9]"), "")
        if (cpfLimpo.length != 11) {
            etCpf.error = "CPF deve conter 11 dígitos"
            etCpf.requestFocus()
            return
        }

        // FIX: map text to typed Genero enum
        val generoEnum: Genero = when (genero.lowercase()) {
            "masculino", "m" -> Genero.MASCULINO
            "feminino", "f"  -> Genero.FEMININO
            else             -> Genero.OUTRO
        }

        // FIX: MedicoCreateRequestDto wraps a PacienteCreateRequestDto in usuarioBase,
        // plus crmUf and crmDigits — not flat fields like nome/crm/especialidade
        val request = MedicoCreateRequestDto(
            usuarioBase = PacienteCreateRequestDto(
                nome           = nome,
                cpf            = cpfLimpo,
                email          = email,
                telefone       = telefone,
                genero         = generoEnum,
                dataNascimento = dataFormatada,
                senha          = senha
            ),
            crmUf     = uf,
            crmDigits = crmDigits
        )

        cadastrarMedico(request)
    }

    private fun cadastrarMedico(medico: MedicoCreateRequestDto) {
        btnCadastrar.isEnabled = false
        btnCadastrar.text = "Processando..."
        lifecycleScope.launch {
            try {
                // FIX: was cadastrarMedico() which doesn't exist — correct method is createMedico()
                val response = RetrofitClient.api.createMedico(medico)
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@CadastroPacienteActivity,
                        "Médico cadastrado com sucesso!",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this@CadastroPacienteActivity, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@CadastroPacienteActivity,
                        "Erro ${response.code()}: Verifique os dados.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@CadastroPacienteActivity,
                    "Falha na rede: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                btnCadastrar.isEnabled = true
                btnCadastrar.text = "Cadastrar"
            }
        }
    }

    private fun converterDataParaApi(entrada: String): String? {
        return try {
            val dataLimpa = entrada.trim().replace(".", "/").replace("-", "/")
            if (dataLimpa.length != 10 || !dataLimpa.contains("/")) return null
            val partes = dataLimpa.split("/")
            if (partes.size == 3) "${partes[2]}-${partes[1]}-${partes[0]}" else null
        } catch (e: Exception) {
            null
        }
    }
}
