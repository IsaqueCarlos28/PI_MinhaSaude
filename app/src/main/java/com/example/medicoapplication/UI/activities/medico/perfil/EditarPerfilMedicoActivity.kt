package com.example.medicoapplication.UI.activities.medico.perfil

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.UI.common.formatters.CpfFormatter
import com.example.medicoapplication.UI.common.formatters.DateFormatter
import com.example.medicoapplication.UI.common.formatters.NameFormatter
import com.example.medicoapplication.UI.common.formatters.PhoneFormatter
import com.example.medicoapplication.UI.common.mappers.GeneroMapper
import com.example.medicoapplication.UI.common.validations.ValidationField
import com.example.medicoapplication.UI.common.validations.ValidationResult
import com.example.medicoapplication.data.remote.DTO.UnidadeFederativa
import com.example.medicoapplication.data.remote.DTO.medico.MedicoEditRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteEditRequestDto
import com.example.medicoapplication.viewmodel.medico.perfil.EditarPerfilMedicoViewModel
import kotlinx.coroutines.launch

class EditarPerfilMedicoActivity : BaseActivity() {

    private val viewModel: EditarPerfilMedicoViewModel by viewModels()

    override val menuType = BottomMenuType.MEDICO

    private lateinit var etNome: EditText
    private lateinit var etEmail: EditText
    private lateinit var etCpf: EditText
    private lateinit var etTelefone: EditText
    private lateinit var etNascimento: EditText
    private lateinit var etCrmDigitos: EditText
    private lateinit var spinnerGenero: Spinner
    private lateinit var spinnerUf: Spinner
    private lateinit var progressBar: ProgressBar
    private lateinit var btnSalvar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil_medico)

        bindViews()
        configurarSpinners()
        configurarBotoes()
        observeViewModel()

        viewModel.carregarPerfil()
        setupBottomNavigation(R.id.nav_config_medico)
    }

    private fun bindViews() {
        etNome        = findViewById(R.id.etMedicoEditNome)
        etEmail       = findViewById(R.id.etMedicoEditEmail)
        etCpf         = findViewById(R.id.etMedicoEditCpf)
        etTelefone    = findViewById(R.id.etMedicoEditTelefone)
        etNascimento  = findViewById(R.id.etMedicoEditNascimento)
        etCrmDigitos  = findViewById(R.id.etMedicoEditCrmDigitos)
        spinnerGenero = findViewById(R.id.spinnerMedicoEditGenero)
        spinnerUf     = findViewById(R.id.spinnerMedicoEditUf)
        progressBar   = findViewById(R.id.progressBarEditarMedico)
        btnSalvar     = findViewById(R.id.btnSalvarPerfilMedico)
    }

    private fun configurarSpinners() {
        // Gênero
        val generoAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            GeneroMapper.spinnerOptions
        )
        generoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGenero.adapter = generoAdapter

        // UF (CRM)
        val ufOptions = UnidadeFederativa.entries.map { it.name }
        val ufAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            ufOptions
        )
        ufAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUf.adapter = ufAdapter
    }

    private fun configurarBotoes() {
        findViewById<ImageButton>(R.id.btnVoltarEditarPerfilMedico).setOnClickListener { finish() }

        btnSalvar.setOnClickListener {
            val dto = buildDto()
            when (val result = validar(dto)) {
                is ValidationResult.Success -> viewModel.salvarPerfil(dto)
                is ValidationResult.Error   -> handleValidationError(result)
            }
        }
    }

    private fun buildDto(): MedicoEditRequestDto {
        val usuarioBase = PacienteEditRequestDto(
            nome           = NameFormatter.normalize(etNome.text.toString()),
            email          = etEmail.text.toString().trim(),
            cpf            = CpfFormatter.uiToApiCpf(etCpf.text.toString()),
            telefone       = PhoneFormatter.uiToApiPhone(etTelefone.text.toString()),
            dataNascimento = DateFormatter.uiToApiDate(etNascimento.text.toString())
                ?: etNascimento.text.toString(),
            genero         = GeneroMapper.fromString(spinnerGenero.selectedItem.toString())
        )
        return MedicoEditRequestDto(
            usuarioBase = usuarioBase,
            uf          = spinnerUf.selectedItem.toString(),
            digits      = etCrmDigitos.text.toString().trim()
        )
    }

    private fun validar(dto: MedicoEditRequestDto): ValidationResult<Unit> {
        if (dto.usuarioBase.nome.isBlank()) {
            return ValidationResult.Error(ValidationField.NOME, "Informe o nome completo.")
        }
        if (dto.usuarioBase.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(dto.usuarioBase.email).matches()) {
            return ValidationResult.Error(ValidationField.EMAIL, "E-mail inválido.")
        }
        if (dto.digits.length != 6 || !dto.digits.all(Char::isDigit)) {
            return ValidationResult.Error(ValidationField.CRM, "CRM deve ter exatamente 6 dígitos numéricos.")
        }
        return ValidationResult.Success(Unit)
    }

    private fun handleValidationError(result: ValidationResult.Error) {
        val field = when (result.field) {
            ValidationField.NOME     -> etNome
            ValidationField.EMAIL    -> etEmail
            ValidationField.CPF      -> etCpf
            ValidationField.TELEFONE -> etTelefone
            ValidationField.CRM      -> etCrmDigitos
            ValidationField.DATA_NASCIMENTO -> etNascimento
            else                     -> null
        }
        if (field != null) {
            showValidationError(field, result.message)
        } else {
            showToast(result.message)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is EditarPerfilMedicoViewModel.UiState.Idle -> Unit

                    is EditarPerfilMedicoViewModel.UiState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        btnSalvar.isEnabled = false
                    }

                    is EditarPerfilMedicoViewModel.UiState.Carregado -> {
                        progressBar.visibility = View.GONE
                        btnSalvar.isEnabled = true
                        preencherFormulario(state.medico)
                    }

                    is EditarPerfilMedicoViewModel.UiState.Salvo -> {
                        progressBar.visibility = View.GONE
                        btnSalvar.isEnabled = true
                        showToast("Perfil atualizado com sucesso!")
                        finish()
                    }

                    is EditarPerfilMedicoViewModel.UiState.Error -> {
                        progressBar.visibility = View.GONE
                        btnSalvar.isEnabled = true
                        handleError(state.error)
                    }
                }
            }
        }
    }

    private fun preencherFormulario(medico: com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto) {
        val u = medico.usuario
        etNome.setText(u?.nome ?: "")
        etEmail.setText(u?.email ?: "")
        etCpf.setText(u?.cpf?.let { CpfFormatter.apiToUiCpf(it) } ?: "")
        etTelefone.setText(u?.telefone?.let { PhoneFormatter.apiToUiPhone(it) } ?: "")
        etNascimento.setText(u?.dataNascimento?.let { DateFormatter.apiToUiDate(it) } ?: "")
        etCrmDigitos.setText(medico.crmDigitos ?: "")

        // Seleciona o gênero no spinner
        u?.genero?.let { genero ->
            val label = GeneroMapper.toDisplayString(genero)
            val pos = GeneroMapper.spinnerOptions.indexOf(label)
            if (pos >= 0) spinnerGenero.setSelection(pos)
        }

        // Seleciona a UF no spinner
        medico.crmUf?.let { uf ->
            val ufList = UnidadeFederativa.entries.map { it.name }
            val pos = ufList.indexOf(uf)
            if (pos >= 0) spinnerUf.setSelection(pos)
        }
    }
}
