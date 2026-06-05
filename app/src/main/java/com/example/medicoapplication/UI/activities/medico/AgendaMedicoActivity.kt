package com.example.medicoapplication.UI.activities.medico

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

class AgendaMedicoActivity : BaseActivity() {

    private var calAtual = Calendar.getInstance()
    private var diaSelecionado = calAtual.get(Calendar.DAY_OF_MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda_medico)

        val nomeMedico = intent.getStringExtra("NOME_MEDICO") ?: "Médico"
        val idMedico = intent.getLongExtra("ID_MEDICO", -1L)

        findViewById<TextView>(R.id.tvSaudacaoAgenda).text = "Olá, $nomeMedico"

        // Navegação mês
        findViewById<ImageView>(R.id.btnMesAnterior).setOnClickListener {
            calAtual.add(Calendar.MONTH, -1)
            atualizarCalendario()
        }
        findViewById<ImageView>(R.id.btnProximoMes).setOnClickListener {
            calAtual.add(Calendar.MONTH, 1)
            atualizarCalendario()
        }

        // RecyclerView consultas do dia
        val rv = findViewById<RecyclerView>(R.id.rvConsultasDia)
        rv.layoutManager = LinearLayoutManager(this)
        // TODO: carregar via GET medicos/{idMedico}/consultas-agendadas e filtrar pelo dia selecionado

        atualizarCalendario()
        setupBottomNavigation(R.id.nav_agenda_medico)
    }

    private fun atualizarCalendario() {
        val meses = arrayOf("Janeiro","Fevereiro","Março","Abril","Maio","Junho",
            "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro")
        val mes = calAtual.get(Calendar.MONTH)
        val ano = calAtual.get(Calendar.YEAR)

        findViewById<TextView>(R.id.tvMesAno).text = "${meses[mes]}, $ano"

        val grid = findViewById<GridLayout>(R.id.gridCalendario)
        grid.removeAllViews()

        // Primeiro dia do mês
        val primeiroDia = Calendar.getInstance().apply {
            set(ano, mes, 1)
        }
        val diaDaSemanaInicio = primeiroDia.get(Calendar.DAY_OF_WEEK) - 1 // 0=Dom

        // Total de dias no mês
        val totalDias = primeiroDia.getActualMaximum(Calendar.DAY_OF_MONTH)

        val hoje = Calendar.getInstance()
        val ehMesAtual = hoje.get(Calendar.MONTH) == mes && hoje.get(Calendar.YEAR) == ano
        val diaHoje = if (ehMesAtual) hoje.get(Calendar.DAY_OF_MONTH) else -1

        // Células vazias antes do início
        for (i in 0 until diaDaSemanaInicio) {
            grid.addView(criarCelula("", false, false, false))
        }

        // Dias do mês
        for (dia in 1..totalDias) {
            val isSelecionado = dia == diaSelecionado
            val isHoje = dia == diaHoje
            grid.addView(criarCelula(dia.toString(), isSelecionado, isHoje, true) {
                diaSelecionado = dia
                atualizarCalendario()
                atualizarDataSelecionada(dia, mes, ano)
            })
        }
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
        val diasSemana = arrayOf("Domingo","Segunda","Terça","Quarta","Quinta","Sexta","Sábado")
        val meses = arrayOf("Janeiro","Fevereiro","Março","Abril","Maio","Junho",
            "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro")
        val cal = Calendar.getInstance().apply { set(ano, mes, dia) }
        val diaSemana = diasSemana[cal.get(Calendar.DAY_OF_WEEK) - 1]
        findViewById<TextView>(R.id.tvDataSelecionada).text = "$diaSemana Feira, $dia de ${meses[mes]}"
    }


}
