package com.example.medicoapplication.UI.activities.medico.servicos

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.adapters.AgendaAdapter
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.data.remote.DTO.agenda.AgendaResponseDto
import com.example.medicoapplication.data.remote.DTO.agenda.DiaSemana
import com.example.medicoapplication.viewmodel.medico.servicos.SetarAgendaViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class SetarAgendaConsultaOfertadaActivity : BaseActivity() {

    companion object {
        const val EXTRA_ID_CONSULTA = "EXTRA_ID_CONSULTA_OFERTADA"
    }

    private val viewModel: SetarAgendaViewModel by viewModels()
    override val menuType = BottomMenuType.MEDICO

    private var idConsulta: Long = -1L
    private var agendaEmEdicao: AgendaResponseDto? = null

    // Views do formulário inline
    private lateinit var spinnerDia: Spinner
    private lateinit var etHoraInicio: TextInputEditText
    private lateinit var etHoraFim: TextInputEditText
    private lateinit var btnSalvarAgenda: Button
    private lateinit var btnCancelarEdicao: Button
    private lateinit var tvTituloFormulario: TextView
    private lateinit var progress: CircularProgressIndicator
    private lateinit var rvAgendas: RecyclerView
    private lateinit var tvVazio: TextView
    private lateinit var adapter: AgendaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setar_agenda_consulta_ofertada)

        idConsulta = intent.getLongExtra(EXTRA_ID_CONSULTA, -1L)

        bindViews()
        setupRecyclerView()
        setupTimePickers()
        setupBotoes()
        observeViewModel()

        viewModel.carregarAgendas(idConsulta)
    }

    private fun bindViews() {
        spinnerDia        = findViewById(R.id.spinnerDiaSemana)
        etHoraInicio      = findViewById(R.id.etHoraInicio)
        etHoraFim         = findViewById(R.id.etHoraFim)
        btnSalvarAgenda   = findViewById(R.id.btnSalvarAgenda)
        btnCancelarEdicao = findViewById(R.id.btnCancelarEdicaoAgenda)
        tvTituloFormulario = findViewById(R.id.tvTituloFormularioAgenda)
        progress          = findViewById(R.id.progressAgenda)
        rvAgendas         = findViewById(R.id.rvAgendas)
        tvVazio           = findViewById(R.id.tvAgendasVazio)

        val diasNomes = DiaSemana.values().map { dia ->
            when (dia) {
                DiaSemana.DOMINGO  -> "Domingo"
                DiaSemana.SEGUNDA  -> "Segunda-feira"
                DiaSemana.TERCA    -> "Terça-feira"
                DiaSemana.QUARTA   -> "Quarta-feira"
                DiaSemana.QUINTA   -> "Quinta-feira"
                DiaSemana.SEXTA    -> "Sexta-feira"
                DiaSemana.SABADO   -> "Sábado"
            }
        }
        spinnerDia.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, diasNomes
        )

        findViewById<ImageView>(R.id.btnVoltarSetarAgenda).setOnClickListener { finish() }
        // "Concluir" navega de volta sem forçar nada — basta fechar
        findViewById<Button>(R.id.btnConcluirAgenda).setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        rvAgendas.layoutManager = LinearLayoutManager(this)
        adapter = AgendaAdapter(
            agendas   = emptyList(),
            onEditar  = { agenda -> iniciarEdicao(agenda) },
            onExcluir = { agenda ->
                AlertDialog.Builder(this)
                    .setTitle("Remover horário?")
                    .setMessage("Deseja remover ${agenda.diaSemana.name} ${agenda.horaInicio} - ${agenda.horaFim}?")
                    .setPositiveButton("Remover") { _, _ ->
                        viewModel.excluirAgenda(idConsulta, agenda.id)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )
        rvAgendas.adapter = adapter
    }

    private fun setupTimePickers() {
        // Abre TimePickerDialog ao clicar no campo de hora
        etHoraInicio.setOnClickListener { abrirTimePicker { hora -> etHoraInicio.setText(hora) } }
        etHoraFim.setOnClickListener   { abrirTimePicker { hora -> etHoraFim.setText(hora) } }
    }

    private fun setupBotoes() {
        btnSalvarAgenda.setOnClickListener {
            val diaSemana  = DiaSemana.values()[spinnerDia.selectedItemPosition]
            val horaInicio = etHoraInicio.text.toString().trim()
            val horaFim    = etHoraFim.text.toString().trim()

            if (!validarHorario(horaInicio, horaFim)) return@setOnClickListener

            val emEdicao = agendaEmEdicao
            if (emEdicao != null) {
                viewModel.editarAgenda(idConsulta, emEdicao.id, diaSemana, horaInicio, horaFim)
            } else {
                viewModel.adicionarAgenda(idConsulta, diaSemana, horaInicio, horaFim)
            }
        }

        btnCancelarEdicao.setOnClickListener { cancelarEdicao() }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.agendas.collect { lista ->
                adapter.atualizarLista(lista)
                tvVazio.visibility   = if (lista.isEmpty()) View.VISIBLE else View.GONE
                rvAgendas.visibility = if (lista.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is SetarAgendaViewModel.UiState.Loading -> {
                        progress.visibility     = View.VISIBLE
                        btnSalvarAgenda.isEnabled = false
                    }
                    is SetarAgendaViewModel.UiState.Salvo -> {
                        progress.visibility     = View.GONE
                        btnSalvarAgenda.isEnabled = true
                        showToast("Horário salvo!")
                        cancelarEdicao()   // limpa formulário após salvar
                        viewModel.resetState()
                    }
                    is SetarAgendaViewModel.UiState.Excluido -> {
                        progress.visibility     = View.GONE
                        btnSalvarAgenda.isEnabled = true
                        showToast("Horário removido.")
                        viewModel.resetState()
                    }
                    is SetarAgendaViewModel.UiState.Error -> {
                        progress.visibility     = View.GONE
                        btnSalvarAgenda.isEnabled = true
                        handleError(state.error)
                        viewModel.resetState()
                    }
                    else -> {
                        progress.visibility     = View.GONE
                        btnSalvarAgenda.isEnabled = true
                    }
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Auxiliares
    // -----------------------------------------------------------------------

    private fun iniciarEdicao(agenda: AgendaResponseDto) {
        agendaEmEdicao = agenda
        tvTituloFormulario.text = "Editar horário"
        btnCancelarEdicao.visibility = View.VISIBLE

        // Preenche o formulário com os dados da agenda selecionada
        spinnerDia.setSelection(agenda.diaSemana.ordinal)
        etHoraInicio.setText(agenda.horaInicio)
        etHoraFim.setText(agenda.horaFim)
        btnSalvarAgenda.text = "Salvar alteração"
    }

    private fun cancelarEdicao() {
        agendaEmEdicao = null
        tvTituloFormulario.text = "Adicionar horário"
        btnCancelarEdicao.visibility = View.GONE
        spinnerDia.setSelection(0)
        etHoraInicio.setText("")
        etHoraFim.setText("")
        btnSalvarAgenda.text = "Adicionar"
    }

    private fun abrirTimePicker(onHoraSelecionada: (String) -> Unit) {
        TimePickerDialog(this, { _, hour, minute ->
            onHoraSelecionada("%02d:%02d".format(hour, minute))
        }, 8, 0, true).show()
    }

    private fun validarHorario(horaInicio: String, horaFim: String): Boolean {
        val regex = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")
        if (!horaInicio.matches(regex)) {
            showToast("Hora de início inválida. Use o formato HH:mm.")
            return false
        }
        if (!horaFim.matches(regex)) {
            showToast("Hora de fim inválida. Use o formato HH:mm.")
            return false
        }
        if (horaInicio >= horaFim) {
            showToast("A hora de início deve ser anterior à hora de fim.")
            return false
        }
        return true
    }
}
