package com.example.medicoapplication.UI.activities.medico.perfil

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.common.mappers.GeneroMapper
import com.example.medicoapplication.UI.common.mappers.PerfilMapper
import com.example.medicoapplication.UI.common.validations.PerfilValidator
import com.example.medicoapplication.UI.common.validations.ValidationField
import com.example.medicoapplication.UI.common.validations.ValidationResult
import com.example.medicoapplication.UI.common.validations.campos.CrmValidator
import com.example.medicoapplication.data.remote.DTO.medico.MedicoEditRequestDto
import com.example.medicoapplication.viewmodel.medico.perfil.EditarPerfilMedicoViewModel
import kotlinx.coroutines.launch

class EditarPerfilMedicoActivity : BaseActivity() {

    private val viewModel: EditarPerfilMedicoViewModel by viewModels()

    private lateinit var etNome: EditText
    private lateinit var etEmail: EditText
    private lateinit var etCpf: EditText
    private lateinit var etTelefone: EditText
    private lateinit var etNascimento: EditText
    private lateinit var spinnerGenero: Spinner
    private lateinit var etCrmUf: EditText
    private lateinit var etCrmDigitos: EditText
    private lateinit var btnSalvar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        montarTela()
        configurarSpinnerGenero()
        configurarBotoes()
        observeViewModel()
        viewModel.carregarPerfil()
    }

    private fun montarTela() {
        val scroll = ScrollView(this)
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 48, 32, 32)
        }
        scroll.addView(container)

        fun label(texto: String) = TextView(this).apply {
            text = texto
            textSize = 13f
            setPadding(0, 18, 0, 6)
        }
        fun campo(hint: String) = EditText(this).apply {
            this.hint = hint
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        container.addView(TextView(this).apply {
            text = "Editar Perfil Médico"
            textSize = 22f
            setPadding(0, 0, 0, 16)
        })

        etNome = campo("Nome completo"); container.addView(label("Nome")); container.addView(etNome)
        etEmail = campo("email@exemplo.com"); container.addView(label("E-mail")); container.addView(etEmail)
        etCpf = campo("CPF"); container.addView(label("CPF")); container.addView(etCpf)
        etTelefone = campo("Telefone"); container.addView(label("Telefone")); container.addView(etTelefone)
        etNascimento = campo("DD/MM/AAAA"); container.addView(label("Data de nascimento")); container.addView(etNascimento)

        spinnerGenero = Spinner(this)
        container.addView(label("Gênero")); container.addView(spinnerGenero)

        etCrmUf = campo("Ex.: SP"); container.addView(label("UF do CRM")); container.addView(etCrmUf)
        etCrmDigitos = campo("6 dígitos"); container.addView(label("Número do CRM")); container.addView(etCrmDigitos)

        btnSalvar = Button(this).apply { text = "Salvar Alterações" }
        container.addView(btnSalvar)

        val btnCancelar = Button(this).apply { text = "Cancelar" }
        btnCancelar.setOnClickListener { finish() }
        container.addView(btnCancelar)

        setContentView(scroll)
    }

    private fun configurarSpinnerGenero() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, GeneroMapper.spinnerOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGenero.adapter = adapter
    }

    private fun configurarBotoes() {
        btnSalvar.setOnClickListener {
            val usuarioBase = PerfilMapper.uiToApi(
                nome = etNome.text.toString(),
                email = etEmail.text.toString(),
                cpf = etCpf.text.toString(),
                telefone = etTelefone.text.toString(),
                dataNascimento = etNascimento.text.toString(),
                genero = spinnerGenero.selectedItem.toString()
            )

            val crmUf = etCrmUf.text.toString().trim().uppercase()
            val crmDigitos = etCrmDigitos.text.toString().trim()

            when (val result = PerfilValidator.validarEdicaoPaciente(usuarioBase)) {
                is ValidationResult.Error -> handleValidationError(result)
                is ValidationResult.Success -> {
                    if (crmUf.length != 2) {
                        showValidationError(etCrmUf, "Informe a UF com 2 letras. Ex.: SP")
                        return@setOnClickListener
                    }
                    if (!CrmValidator.isValid(crmDigitos)) {
                        showValidationError(etCrmDigitos, "CRM inválido. Informe 6 dígitos numéricos.")
                        return@setOnClickListener
                    }
                    viewModel.salvarPerfil(
                        MedicoEditRequestDto(
                            usuarioBase = usuarioBase,
                            uf = crmUf,
                            digits = crmDigitos
                        )
                    )
                }
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is EditarPerfilMedicoViewModel.UiState.Idle -> setLoading(false)
                    is EditarPerfilMedicoViewModel.UiState.Loading -> setLoading(true)
                    is EditarPerfilMedicoViewModel.UiState.Error -> {
                        setLoading(false)
                        handleError(state.error)
                    }
                    is EditarPerfilMedicoViewModel.UiState.Carregado -> {
                        setLoading(false)
                        val medico = state.medico
                        medico.usuario?.let { usuario ->
                            val prefill = PerfilMapper.apiToFormPrefill(usuario)
                            etNome.setText(prefill.nome)
                            etEmail.setText(prefill.email)
                            etCpf.setText(prefill.cpf)
                            etTelefone.setText(prefill.telefone)
                            etNascimento.setText(prefill.dataNascimento)
                            val generoDisplay = GeneroMapper.toDisplayString(prefill.genero)
                            for (i in 0 until spinnerGenero.adapter.count) {
                                if (spinnerGenero.adapter.getItem(i) == generoDisplay) {
                                    spinnerGenero.setSelection(i)
                                    break
                                }
                            }
                        }
                        etCrmUf.setText(medico.crmUf ?: "")
                        etCrmDigitos.setText(medico.crmDigitos ?: "")
                    }
                    is EditarPerfilMedicoViewModel.UiState.Salvo -> {
                        setLoading(false)
                        showToast("Perfil médico atualizado com sucesso!")
                        finish()
                    }
                }
            }
        }
    }

    private fun setLoading(carregando: Boolean) {
        btnSalvar.isEnabled = !carregando
        btnSalvar.text = if (carregando) "Salvando..." else "Salvar Alterações"
    }

    private fun handleValidationError(error: ValidationResult.Error) {
        when (error.field) {
            ValidationField.NOME -> showValidationError(etNome, error.message)
            ValidationField.EMAIL -> showValidationError(etEmail, error.message)
            ValidationField.CPF -> showValidationError(etCpf, error.message)
            ValidationField.TELEFONE -> showValidationError(etTelefone, error.message)
            ValidationField.DATA_NASCIMENTO -> showValidationError(etNascimento, error.message)
            else -> showToast(error.message)
        }
    }
}
