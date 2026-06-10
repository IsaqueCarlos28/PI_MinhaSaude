package com.example.medicoapplication.UI.activities.paciente.medicos

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.activities.paciente.consultas.MinhasConsultasActivity
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.UI.common.formatters.DateFormatter
import com.example.medicoapplication.UI.common.validations.ConsultaValidator
import com.example.medicoapplication.UI.common.validations.ValidationResult
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaResponseDto
import com.example.medicoapplication.data.remote.DTO.convenio.ConvenioResponseDto
import com.example.medicoapplication.viewmodel.paciente.medicos.marcar_consulta.ConfirmacaoConsultaViewModel
import kotlinx.coroutines.launch

/**
 * Confirmation screen before creating the booking.
 *
 * Receives via Intent:
 *   - NOME_MEDICO          (String)
 *   - ESPECIALIDADE        (String)
 *   - DATA_CONSULTA        (String — "yyyy-MM-dd")
 *   - HORA_CONSULTA        (String — "HH:mm")
 *   - ID_CONSULTA_OFERTADA (Long)
 *   - ID_MEDICO            (Long)  ← NEW: needed to load consulta ofertada details
 *
 * FIX: previously idConvenio was always null. Now loads consulta ofertada,
 * renders a radio-group with the accepted convenios (+ Particular when applicable),
 * and passes the selected idConvenio to the API.
 */
class ConfirmacaoConsultaActivity : BaseActivity() {

    private val viewModel: ConfirmacaoConsultaViewModel by viewModels()

    override val menuType = BottomMenuType.PACIENTE

    private var idConsultaOfertada: Long = -1L
    private var idMedico: Long = -1L
    private var dataConsulta: String = ""
    private var horaConsulta: String = ""

    private lateinit var rgConvenio: RadioGroup
    private lateinit var tvConvenioLabel: TextView
    private lateinit var btnConfirmar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmacao_consulta)

        idConsultaOfertada = intent.getLongExtra("ID_CONSULTA_OFERTADA", -1L)
        idMedico           = intent.getLongExtra("ID_MEDICO", -1L)
        dataConsulta       = intent.getStringExtra("DATA_CONSULTA") ?: ""
        horaConsulta       = intent.getStringExtra("HORA_CONSULTA") ?: ""

        val nomeMedico    = intent.getStringExtra("NOME_MEDICO")   ?: "Médico"
        val especialidade = intent.getStringExtra("ESPECIALIDADE") ?: ""

        rgConvenio      = findViewById(R.id.rgConvenioConfirmacao)
        tvConvenioLabel = findViewById(R.id.tvConvenioLabelConfirmacao)
        btnConfirmar    = findViewById(R.id.btnConfirmarAgendamento)

        preencherResumo(nomeMedico, especialidade)
        configurarBotoes()
        observeViewModel()

        // Load consulta ofertada to know which convenios are accepted
        if (idMedico != -1L && idConsultaOfertada != -1L) {
            viewModel.carregarConsultaOfertada(idMedico, idConsultaOfertada)
        }

        setupBottomNavigation(R.id.nav_consultas_paciente)
    }

    private fun preencherResumo(nomeMedico: String, especialidade: String) {
        findViewById<TextView>(R.id.tvConfNomeMedico).text    = nomeMedico
        findViewById<TextView>(R.id.tvConfEspecialidade).text = especialidade
        findViewById<TextView>(R.id.tvConfData).text          = DateFormatter.apiToUiDate(dataConsulta)
        findViewById<TextView>(R.id.tvConfHora).text          = horaConsulta
    }

    private fun configurarBotoes() {
        findViewById<ImageButton>(R.id.btnVoltarConfirmacao).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnVoltarEscolha).setOnClickListener { finish() }

        btnConfirmar.setOnClickListener {
            when (val result = ConsultaValidator.validarConfirmacao(
                idConsultaOfertada, dataConsulta, horaConsulta
            )) {
                is ValidationResult.Success ->
                    viewModel.confirmarConsulta(idConsultaOfertada, dataConsulta, horaConsulta)
                is ValidationResult.Error ->
                    showToast(result.message)
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ConfirmacaoConsultaViewModel.UiState.Idle    -> setLoading(false)
                    is ConfirmacaoConsultaViewModel.UiState.Loading -> setLoading(true)
                    is ConfirmacaoConsultaViewModel.UiState.ConsultaOfertadaCarregada -> {
                        setLoading(false)
                        renderizarConvenios(state.consulta)
                    }
                    is ConfirmacaoConsultaViewModel.UiState.Error   -> {
                        setLoading(false)
                        handleError(state.error)
                    }
                    is ConfirmacaoConsultaViewModel.UiState.Success -> {
                        setLoading(false)
                        showToast("Consulta agendada com sucesso!")
                        startActivity(
                            Intent(this@ConfirmacaoConsultaActivity, MinhasConsultasActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            }
                        )
                        finish()
                    }
                }
            }
        }
    }

    // ─── Convenio selector ───────────────────────────────────────────────────

    /**
     * Builds radio buttons dynamically:
     *   - One per accepted convenio
     *   - "Particular" option if aceitaParticular is true
     *
     * If only one payment option exists it is pre-selected automatically.
     */
    private fun renderizarConvenios(co: ConsultaOfertadaResponseDto) {
        rgConvenio.removeAllViews()

        val opcoes = mutableListOf<Pair<ConvenioResponseDto?, String>>()

        if (co.aceitaParticular) {
            opcoes.add(Pair(null, "Particular"))
        }
        co.conveniosAceitos.forEach { convenio ->
            opcoes.add(Pair(convenio, convenio.nome))
        }

        if (opcoes.isEmpty()) {
            // No valid payment option — this consulta is misconfigured on the backend
            tvConvenioLabel.visibility = View.GONE
            rgConvenio.visibility      = View.GONE
            return
        }

        tvConvenioLabel.visibility = View.VISIBLE
        rgConvenio.visibility      = View.VISIBLE

        opcoes.forEachIndexed { index, (convenio, label) ->
            val rb = RadioButton(this).apply {
                id   = View.generateViewId()
                text = label
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.bottomMargin = dpToPx(4) }
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) viewModel.selecionarConvenio(convenio)
                }
            }
            rgConvenio.addView(rb)

            // Pre-select the first option
            if (index == 0) {
                rb.isChecked = true
                viewModel.selecionarConvenio(convenio)
            }
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private fun setLoading(carregando: Boolean) {
        btnConfirmar.isEnabled = !carregando
        btnConfirmar.text = if (carregando) "Agendando..." else "Confirmar Agendamento"
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density + 0.5f).toInt()
}
