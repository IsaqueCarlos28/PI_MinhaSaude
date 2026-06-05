package com.example.medicoapplication.UI.activities.medico.consulta_ofertada

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
import com.example.medicoapplication.UI.common.validations.ConsultaOfertadaValidator
import com.example.medicoapplication.UI.common.validations.ValidationResult
import com.example.medicoapplication.data.remote.DTO.consultaofertada.TipoConsulta
import com.example.medicoapplication.data.remote.DTO.convenio.ConvenioResponseDto
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadeResponseDto
import com.example.medicoapplication.data.remote.DTO.local.LocalResponseDto
import com.example.medicoapplication.viewmodel.medico.consulta.ConsultaOfertadaViewModel
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class CriarConsultaOfertadaActivity : BaseActivity() {

    private val viewModel: ConsultaOfertadaViewModel by viewModels()
    private var idMedico = -1L

    private var especialidades = listOf<EspecialidadeResponseDto>()
    private var locais         = listOf<LocalResponseDto>()
    private var convenios      = listOf<ConvenioResponseDto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_consulta_ofertada)

        idMedico = intent.getLongExtra("ID_MEDICO", -1L)

        val spinnerEspecialidade = findViewById<Spinner>(R.id.spinnerEspecialidade)
        val spinnerLocal         = findViewById<Spinner>(R.id.spinnerLocal)
        val rgTipo               = findViewById<RadioGroup>(R.id.rgTipoConsulta)
        val etValor              = findViewById<TextInputEditText>(R.id.etValorConsulta)
        val etDuracao            = findViewById<TextInputEditText>(R.id.etDuracaoMinutos)
        val switchParticular     = findViewById<SwitchMaterial>(R.id.switchAceitaParticular)
        val llConvenios          = findViewById<LinearLayout>(R.id.llConvenios)
        val btnSalvar            = findViewById<Button>(R.id.btnSalvarConsultaOfertada)
        val progressBar          = findViewById<CircularProgressIndicator>(R.id.progressCriarConsulta)
        val btnVoltar            = findViewById<ImageView>(R.id.btnVoltarCriarConsulta)

        btnVoltar.setOnClickListener { finish() }

        lifecycleScope.launch {
            viewModel.especialidades.collect { lista ->
                especialidades = lista
                val nomes = lista.map { it.nome }.toMutableList().also { it.add(0, "Selecione a especialidade") }
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
                    is ConsultaOfertadaViewModel.UiState.Success -> {
                        progressBar.visibility = View.GONE
                        showToast("Consulta criada com sucesso!")
                        finish()
                    }
                    is ConsultaOfertadaViewModel.UiState.Error -> {
                        progressBar.visibility = View.GONE
                        btnSalvar.isEnabled = true
                        showToast(state.message)
                    }
                    else -> {
                        progressBar.visibility = View.GONE
                        btnSalvar.isEnabled = true
                    }
                }
            }
        }

        btnSalvar.setOnClickListener {
            val espIdx = spinnerEspecialidade.selectedItemPosition
            val valorStr = etValor.text.toString().replace(",", ".")
            val valor    = valorStr.toDoubleOrNull()
            val duracao  = etDuracao.text.toString().toIntOrNull()

            // All validation delegated to ConsultaOfertadaValidator
            when (val result = ConsultaOfertadaValidator.validar(espIdx, valor, duracao)) {
                is ValidationResult.Error   -> showToast(result.message)
                is ValidationResult.Success -> {
                    val idEspecialidade = especialidades[espIdx - 1].id

                    val localIdx = spinnerLocal.selectedItemPosition
                    val idLocal  = if (localIdx == 0) null else locais[localIdx - 1].id

                    val tipo = when (rgTipo.checkedRadioButtonId) {
                        R.id.rbPresencial -> TipoConsulta.PRESENCIAL
                        else              -> TipoConsulta.TELECONSULTA
                    }

                    val conveniosSelecionados = mutableSetOf<Long>()
                    for (i in 0 until llConvenios.childCount) {
                        val cb = llConvenios.getChildAt(i) as? MaterialCheckBox
                        if (cb?.isChecked == true) conveniosSelecionados.add(cb.tag as Long)
                    }

                    viewModel.criarConsultaOfertada(
                        idEspecialidade   = idEspecialidade,
                        idLocal           = idLocal,
                        tipoConsulta      = tipo,
                        valorConsulta     = valor!!,
                        aceitaParticular  = switchParticular.isChecked,
                        duracaoMinutos    = duracao!!,
                        conveniosIds      = conveniosSelecionados
                    )
                }
            }
        }

        viewModel.carregarDadosFormulario()
    }
}