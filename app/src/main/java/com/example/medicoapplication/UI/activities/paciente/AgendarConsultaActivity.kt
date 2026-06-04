package com.example.medicoapplication.UI.activities.paciente

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.common.mappers.MedicoMapper
import com.example.medicoapplication.UI.common.validations.ConsultaValidator
import com.example.medicoapplication.UI.common.validations.ValidationResult
import com.example.medicoapplication.activities.paciente.viewmodel.AgendarConsultaViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AgendarConsultaActivity : BaseActivity() {

    private val viewModel: AgendarConsultaViewModel by viewModels()

    private val calendar = Calendar.getInstance()
    private val formatoApi       = SimpleDateFormat("yyyy-MM-dd", Locale("pt", "BR"))
    private val formatoExibicao  = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    private val formatoDiaSemana = SimpleDateFormat("EEE", Locale("pt", "BR"))

    private var horarioSelecionado: String? = null
    private var medicoId: Long = -1L
    private var idPaciente: Long = -1L
    private var idConsultaOfertada: Long = -1L

    private val idsBlocoDia = listOf(
        R.id.diaBloco0, R.id.diaBloco1, R.id.diaBloco2,
        R.id.diaBloco3, R.id.diaBloco4, R.id.diaBloco5, R.id.diaBloco6
    )
    private var diaOffset = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendar_consulta)

        idPaciente         = intent.getLongExtra("ID_PACIENTE", -1L)
        medicoId           = intent.getLongExtra("ID_MEDICO", intent.getLongExtra("MEDICO_ID", -1L))
        idConsultaOfertada = intent.getLongExtra("ID_CONSULTA_OFERTADA", -1L)

        val nomeExtra = intent.getStringExtra("NOME_MEDICO")
        if (!nomeExtra.isNullOrBlank()) {
            findViewById<TextView>(R.id.tvNomeMedicoAgendar).text = nomeExtra
        }

        renderizarDias()

        findViewById<ImageButton>(R.id.btnDataAnterior).setOnClickListener {
            if (diaOffset > 0) { diaOffset--; calendar.add(Calendar.DAY_OF_MONTH, -1); renderizarDias() }
        }
        findViewById<ImageButton>(R.id.btnProximaData).setOnClickListener {
            diaOffset++; calendar.add(Calendar.DAY_OF_MONTH, 1); renderizarDias()
        }

        //Mudar para os horarios da API
        val botoesHorario = listOf(
            Pair(R.id.btnHorario0900, "09:00"), Pair(R.id.btnHorario0930, "09:30"),
            Pair(R.id.btnHorario1000, "10:00"), Pair(R.id.btnHorario1030, "10:30"),
            Pair(R.id.btnHorario1100, "11:00"), Pair(R.id.btnHorario1130, "11:30"),
            Pair(R.id.btnHorario1200, "12:00"), Pair(R.id.btnHorario1230, "12:30")
        )
        botoesHorario.forEach { (idBotao, horario) ->
            findViewById<Button>(idBotao).setOnClickListener {
                selecionarHorario(idBotao, horario, botoesHorario.map { it.first })
            }
        }

        findViewById<Button>(R.id.btnConfirmarConsulta).setOnClickListener { confirmarConsulta() }
        findViewById<TextView>(R.id.tvVerMaisHorarios).setOnClickListener { showToast("Mais horários em breve") }

        observeViewModel()
        if (medicoId != -1L) viewModel.carregarMedico(medicoId)
        configurarBottomNav(R.id.nav_consultas)
    }

    private fun renderizarDias() {
        val base = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, diaOffset - 3) }
        idsBlocoDia.forEachIndexed { index, idBloco ->
            val bloco = try { findViewById<LinearLayout>(idBloco) } catch (e: Exception) { null }
                ?: return@forEachIndexed
            val cal = base.clone() as Calendar
            cal.add(Calendar.DAY_OF_MONTH, index)
            val tvSemana = bloco.getChildAt(0) as? TextView
            val tvDia    = bloco.getChildAt(1) as? TextView
            tvSemana?.text = formatoDiaSemana.format(cal.time).uppercase()
            tvDia?.text    = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH))
            val isSelecionado = index == 3
            if (isSelecionado) {
                bloco.setBackgroundColor(Color.parseColor("#3B82F6"))
                tvSemana?.setTextColor(Color.WHITE); tvDia?.setTextColor(Color.WHITE)
            } else {
                bloco.setBackgroundColor(Color.TRANSPARENT)
                tvSemana?.setTextColor(Color.parseColor("#64748B"))
                tvDia?.setTextColor(Color.parseColor("#64748B"))
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is AgendarConsultaViewModel.UiState.Idle    -> Unit
                    is AgendarConsultaViewModel.UiState.Loading -> Unit
                    is AgendarConsultaViewModel.UiState.Error   -> Unit
                    is AgendarConsultaViewModel.UiState.MedicoCarregado -> {
                        // Mapping responsibility moved to MedicoMapper
                        val info = MedicoMapper.toAgendamentoInfo(state.medico)
                        findViewById<TextView>(R.id.tvNomeMedicoAgendar).text      = info.nome
                        findViewById<TextView>(R.id.tvEspecialidadeAgendar).text   = info.especialidade
                        findViewById<TextView>(R.id.tvCrmAgendar).text             = info.crm
                    }
                }
            }
        }
    }

    private fun selecionarHorario(idSelecionado: Int, horario: String, idsHorarios: List<Int>) {
        horarioSelecionado = horario
        idsHorarios.forEach { id ->
            findViewById<Button>(id).backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E2E8F0"))
            findViewById<Button>(id).setTextColor(Color.parseColor("#1E293B"))
        }
        findViewById<Button>(idSelecionado).backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3B82F6"))
        findViewById<Button>(idSelecionado).setTextColor(Color.WHITE)
    }

    private fun confirmarConsulta() {
        val dataApi = formatoApi.format(calendar.time)

        when (val result = ConsultaValidator.validarAgendamento(dataApi, horarioSelecionado)) {
            is ValidationResult.Success -> {
                startActivity(
                    Intent(this, ConfirmacaoConsultaActivity::class.java).apply {
                        putExtra("ID_PACIENTE",          idPaciente)
                        putExtra("ID_MEDICO",            medicoId)
                        putExtra("NOME_MEDICO",          findViewById<TextView>(R.id.tvNomeMedicoAgendar).text.toString())
                        putExtra("ESPECIALIDADE",        findViewById<TextView>(R.id.tvEspecialidadeAgendar).text.toString())
                        putExtra("DATA_CONSULTA",        dataApi)
                        putExtra("HORA_CONSULTA",        horarioSelecionado!!)
                        putExtra("ID_CONSULTA_OFERTADA", idConsultaOfertada)
                    }
                )
            }
            is ValidationResult.Error -> showToast(result.message)
        }
    }

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavAgendar)
        bottomNav.selectedItemId = itemSelecionado
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home      -> { startActivity(Intent(this, HomePacienteActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                R.id.nav_consultas -> { startActivity(Intent(this, MinhasConsultasActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                R.id.nav_medicos   -> { startActivity(Intent(this, BuscaMedicosActivity::class.java)); false }
                R.id.nav_perfil    -> { startActivity(Intent(this, PerfilPacienteActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                else -> false
            }
        }
    }
}