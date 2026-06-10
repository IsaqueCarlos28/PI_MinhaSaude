package com.example.medicoapplication.UI.activities.medico.servicos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.UI.common.validations.ConsultaOfertadaValidator
import com.example.medicoapplication.UI.common.validations.ValidationResult
import com.example.medicoapplication.data.remote.DTO.consultaofertada.TipoConsulta
import com.example.medicoapplication.data.remote.DTO.convenio.ConvenioResponseDto
import com.example.medicoapplication.data.remote.DTO.local.LocalResponseDto
import com.example.medicoapplication.data.remote.DTO.medicoespecialidade.MedicoEspecialidadeResponseDto
import com.example.medicoapplication.viewmodel.medico.consulta.ConsultaOfertadaViewModel
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class CriarConsultaOfertadaActivity : BaseActivity() {

    private val viewModel: ConsultaOfertadaViewModel by viewModels()

    override val menuType = BottomMenuType.MEDICO

    private var minhasEspecialidades = listOf<MedicoEspecialidadeResponseDto>()
    private var locais               = listOf<LocalResponseDto>()
    private var convenios            = listOf<ConvenioResponseDto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_consulta_ofertada)

        val spinnerEspecialidade = findViewById<Spinner>(R.id.spinnerEspecialidade)
        val spinnerLocal         = findViewById<Spinner>(R.id.spinnerLocal)
        val rgTipo               = findViewById<RadioGroup>(R.id.rgTipoConsulta)
        val tilValor             = findViewById<TextInputLayout>(R.id.tilValorConsulta)
        val etValor              = findViewById<TextInputEditText>(R.id.etValorConsulta)
        val etDuracao            = findViewById<TextInputEditText>(R.id.etDuracaoMinutos)
        val switchParticular     = findViewById<SwitchMaterial>(R.id.switchAceitaParticular)
        val llConvenios          = findViewById<LinearLayout>(R.id.llConvenios)
        val btnSalvar            = findViewById<Button>(R.id.btnSalvarConsultaOfertada)
        val progressBar          = findViewById<CircularProgressIndicator>(R.id.progressCriarConsulta)
        val btnVoltar            = findViewById<ImageView>(R.id.btnVoltarCriarConsulta)

        btnVoltar.setOnClickListener { finish() }

        // --- Regra: tipo de consulta controla o spinner de local ---
        rgTipo.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbPresencial -> {
                    // Presencial: libera o spinner de local
                    spinnerLocal.isEnabled = true
                    spinnerLocal.alpha = 1.0f
                }
                R.id.rbTeleconsulta -> {
                    // Teleconsulta: força "Sem local" e bloqueia o spinner
                    spinnerLocal.setSelection(0)
                    spinnerLocal.isEnabled = false
                    spinnerLocal.alpha = 0.4f
                }
            }
        }

        // --- Regra: aceita particular controla o campo de valor ---
        switchParticular.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Aceita particular: libera o campo de valor
                tilValor.isEnabled = true
                tilValor.alpha = 1.0f
                tilValor.hint = "Ex: 150,00"
            } else {
                // Não aceita particular: limpa e bloqueia o campo de valor
                etValor.setText("")
                tilValor.isEnabled = false
                tilValor.alpha = 0.4f
                tilValor.hint = "Não aplicável"
            }
        }

        // --- Observadores ---

        lifecycleScope.launch {
            viewModel.minhasEspecialidades.collect { lista ->
                minhasEspecialidades = lista
                val nomes = lista.mapNotNull { it.especialidade?.nome }
                    .toMutableList()
                    .also { it.add(0, "Selecione a especialidade") }
                spinnerEspecialidade.adapter = ArrayAdapter(
                    this@CriarConsultaOfertadaActivity,
                    android.R.layout.simple_spinner_dropdown_item, nomes
                )
            }
        }

        lifecycleScope.launch {
            viewModel.locais.collect { lista ->
                locais = lista
                val nomes = lista.map { it.nome ?: "Local sem nome" }.toMutableList()
                    .also { it.add(0, "Sem local (TELECONSULTA)") }
                spinnerLocal.adapter = ArrayAdapter(
                    this@CriarConsultaOfertadaActivity,
                    android.R.layout.simple_spinner_dropdown_item, nomes
                )
            }
        }

        lifecycleScope.launch {
            viewModel.convenios.collect { lista ->
                convenios = lista
                llConvenios.removeAllViews()
                lista.forEach { convenio ->
                    val cb = MaterialCheckBox(this@CriarConsultaOfertadaActivity)
                    cb.text = convenio.nome
                    cb.tag  = convenio.id
                    llConvenios.addView(cb)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ConsultaOfertadaViewModel.UiState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        btnSalvar.isEnabled = false
                    }
                    is ConsultaOfertadaViewModel.UiState.SuccessComId -> {
                        progressBar.visibility = View.GONE
                        val intent = Intent(
                            this@CriarConsultaOfertadaActivity,
                            SetarAgendaConsultaOfertadaActivity::class.java
                        ).apply {
                            putExtra(SetarAgendaConsultaOfertadaActivity.EXTRA_ID_CONSULTA, state.idConsultaOfertada)
                        }
                        startActivity(intent)
                        finish()
                    }
                    is ConsultaOfertadaViewModel.UiState.Error -> {
                        progressBar.visibility = View.GONE
                        btnSalvar.isEnabled = true
                        handleError(state.error)
                    }
                    else -> {
                        progressBar.visibility = View.GONE
                        btnSalvar.isEnabled = true
                    }
                }
            }
        }

        // --- Botão salvar ---

        btnSalvar.setOnClickListener {
            val espIdx   = spinnerEspecialidade.selectedItemPosition
            val localIdx = spinnerLocal.selectedItemPosition

            val tipo = when (rgTipo.checkedRadioButtonId) {
                R.id.rbPresencial -> TipoConsulta.PRESENCIAL
                else              -> TipoConsulta.TELECONSULTA
            }

            val aceitaParticular = switchParticular.isChecked
            val valorStr = etValor.text.toString().replace(",", ".")
            val valor    = if (aceitaParticular) valorStr.toDoubleOrNull() else 0.0
            val duracao  = etDuracao.text.toString().toIntOrNull()

            val conveniosSelecionados = mutableSetOf<Long>()
            for (i in 0 until llConvenios.childCount) {
                val cb = llConvenios.getChildAt(i) as? MaterialCheckBox
                if (cb?.isChecked == true) conveniosSelecionados.add(cb.tag as Long)
            }

            when (val result = ConsultaOfertadaValidator.validar(
                especialidadeIdx      = espIdx,
                tipoConsulta          = tipo,
                localIdx              = localIdx,
                valor                 = valor,
                duracao               = duracao,
                aceitaParticular      = aceitaParticular,
                conveniosSelecionados = conveniosSelecionados
            )) {
                is ValidationResult.Error   -> showToast(result.message)
                is ValidationResult.Success -> {
                    // espIdx - 1 porque o índice 0 é o placeholder
                    val medicoEsp = minhasEspecialidades[espIdx - 1]
                    val idEspecialidade = medicoEsp.especialidade!!.id
                    val idLocal = if (localIdx == 0) null else locais[localIdx - 1].id

                    viewModel.criarConsultaOfertada(
                        idEspecialidade  = idEspecialidade,
                        idLocal          = idLocal,
                        tipoConsulta     = tipo,
                        valorConsulta    = valor!!,
                        aceitaParticular = aceitaParticular,
                        duracaoMinutos   = duracao!!,
                        conveniosIds     = conveniosSelecionados
                    )
                }
            }
        }

        viewModel.carregarDadosFormulario()
    }
}
