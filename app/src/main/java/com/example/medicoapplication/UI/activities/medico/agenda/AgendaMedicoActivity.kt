package com.example.medicoapplication.UI.activities.medico.agenda

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.activities.medico.consultas.VisualisarConsultaMedico
import com.example.medicoapplication.UI.adapters.ConsultasMedicoAdapter
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.UI.common.formatters.DateFormatter
import com.example.medicoapplication.UI.common.mappers.ConsultaMapper
import com.example.medicoapplication.viewmodel.medico.agenda.AgendaMedicoViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

class AgendaMedicoActivity : BaseActivity() {

    private val viewModel: AgendaMedicoViewModel by viewModels()

    override val menuType = BottomMenuType.MEDICO

    private var calAtual = LocalDate.now()
    private var diaSelecionado = LocalDate.now().dayOfMonth

    private lateinit var adapter: ConsultasMedicoAdapter
    private lateinit var tvVazio: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda_medico)

        // RecyclerView consultas do dia
        val rv = findViewById<RecyclerView>(R.id.rvConsultasDia)
        rv.layoutManager = LinearLayoutManager(this)

        adapter = ConsultasMedicoAdapter(emptyList()) { consulta ->
            startActivity(
                Intent(this, VisualisarConsultaMedico::class.java).apply {
                    putExtra("ID_EVENTO", consulta.id)
                    putExtra("NOME_PACIENTE", consulta.nomePaciente ?: "Paciente")
                }
            )
        }
        rv.adapter = adapter

        // TextView para estado vazio (criado dinamicamente para não alterar o layout)
        tvVazio = TextView(this).apply {
            text = "Nenhuma consulta para este dia"
            textSize = 14f
            setTextColor(0xFF64748B.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 48, 0, 0)
            visibility = View.GONE
        }
        (rv.parent as? android.widget.LinearLayout)?.addView(tvVazio)

        // Navegação mês
        findViewById<ImageView>(R.id.btnMesAnterior).setOnClickListener {
            calAtual = calAtual.minusMonths(1)
            diaSelecionado = 1
            atualizarCalendario()
        }
        findViewById<ImageView>(R.id.btnProximoMes).setOnClickListener {
            calAtual = calAtual.plusMonths(1)
            diaSelecionado = 1
            atualizarCalendario()
        }

        observeViewModel()
        viewModel.carregarNome()
        viewModel.carregarConsultas()

        atualizarCalendario()
        setupBottomNavigation(R.id.nav_agenda_medico)
    }

    // -----------------------------------------------------------------------
    // Observador
    // -----------------------------------------------------------------------
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is AgendaMedicoViewModel.UiState.Idle    -> Unit
                    is AgendaMedicoViewModel.UiState.Loading -> Unit
                    is AgendaMedicoViewModel.UiState.NomeCarregado -> {
                        val primeiroNome = state.nome.split(" ").firstOrNull() ?: state.nome
                        findViewById<TextView>(R.id.tvSaudacaoAgenda).text = "Olá, $primeiroNome"
                    }
                    is AgendaMedicoViewModel.UiState.ConsultasCarregadas -> {
                        adapter.atualizarLista(state.consultas)
                        tvVazio.visibility = if (state.consultas.isEmpty()) View.VISIBLE else View.GONE
                    }
                    is AgendaMedicoViewModel.UiState.Error -> handleError(state.error)
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Calendário
    // -----------------------------------------------------------------------
    private fun atualizarCalendario() {
        val meses = arrayOf(
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        )
        val mes = calAtual.monthValue - 1
        val ano = calAtual.year

        findViewById<TextView>(R.id.tvMesAno).text = "${meses[mes]}, $ano"

        val grid = findViewById<GridLayout>(R.id.gridCalendario)
        grid.removeAllViews()

        val primeiroDia = calAtual.withDayOfMonth(1)
        // LocalDate.dayOfWeek: MONDAY=1..SUNDAY=7 → queremos DOM=0
        val diaDaSemanaInicio = (primeiroDia.dayOfWeek.value % 7) // DOM=0,SEG=1..SAB=6
        val totalDias = calAtual.lengthOfMonth()

        val hoje = LocalDate.now()
        val ehMesAtual = hoje.year == ano && hoje.monthValue - 1 == mes
        val diaHoje = if (ehMesAtual) hoje.dayOfMonth else -1

        // Células vazias antes do primeiro dia
        for (i in 0 until diaDaSemanaInicio) {
            grid.addView(criarCelula("", false, false, false))
        }

        for (dia in 1..totalDias) {
            val isSelecionado = dia == diaSelecionado
            val isHoje = dia == diaHoje
            grid.addView(criarCelula(dia.toString(), isSelecionado, isHoje, true) {
                diaSelecionado = dia
                atualizarCalendario()
                atualizarDataSelecionada(dia, mes, ano)
                viewModel.filtrarPorData(LocalDate.of(ano, mes + 1, dia))
            })
        }

        // Atualiza o label da data selecionada
        atualizarDataSelecionada(diaSelecionado, mes, ano)
    }

    private fun criarCelula(
        texto: String,
        selecionado: Boolean,
        hoje: Boolean,
        clicavel: Boolean,
        onClick: (() -> Unit)? = null
    ): TextView {
        return TextView(this).apply {
            text = texto
            gravity = Gravity.CENTER
            textSize = 13f
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = resources.getDimensionPixelSize(R.dimen.calendar_cell_height)
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(2, 2, 2, 2)
            }
            when {
                selecionado -> {
                    setBackgroundResource(R.drawable.bg_button_primary)
                    backgroundTintList = ColorStateList.valueOf(0xFF3B82F6.toInt())
                    setTextColor(0xFFFFFFFF.toInt())
                }
                hoje -> {
                    setTextColor(0xFF3B82F6.toInt())
                    setTypeface(typeface, Typeface.BOLD)
                }
                else -> setTextColor(0xFF1E293B.toInt())
            }
            if (clicavel && onClick != null) {
                isClickable = true
                isFocusable = true
                setOnClickListener { onClick() }
            }
        }
    }

    private fun atualizarDataSelecionada(dia: Int, mes: Int, ano: Int) {
        val diasSemana = arrayOf("Domingo", "Segunda", "Terca", "Quarta", "Quinta", "Sexta", "Sabado")
        val meses = arrayOf(
            "Janeiro", "Fevereiro", "Marco", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        )
        val data = LocalDate.of(ano, mes + 1, dia)
        // dayOfWeek: MON=1..SUN=7 → array DOM=0..SAB=6
        val diaSemana = diasSemana[data.dayOfWeek.value % 7]
        findViewById<TextView>(R.id.tvDataSelecionada).text =
            "$diaSemana, $dia de ${meses[mes]}"
    }
}