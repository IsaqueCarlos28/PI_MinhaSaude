package com.example.medicoapplication.UI.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R

/**
 * RecyclerView adapter for the 7-column month calendar in MinhasConsultasActivity.
 *
 * Each cell is either:
 *   - a padding cell ("") for the days before the 1st of the month
 *   - a day number cell, optionally highlighted with a dot if a consulta exists
 *     on that date, and with a filled circle background if it is selected.
 *
 * The adapter owns no date logic — the Activity builds the list of day strings
 * and passes the Set<String> of dates that have consultas (in "yyyy-MM-dd" format).
 */
class CalendarioAdapter(
    private var diasNoMes: List<String>,
    private var datasComConsulta: Set<String>,
    private var dataSelecionada: String?,
    private val onDiaClick: (String) -> Unit
) : RecyclerView.Adapter<CalendarioAdapter.DiaViewHolder>() {

    // Reference month-year to build full dates for comparison (set from outside)
    // These are updated together with diasNoMes so they're always in sync.
    private var ano: Int = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
    private var mes: Int = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) // 0-based

    inner class DiaViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvDia: TextView    = view.findViewById(R.id.tvDiaCalendario)
        val dotConsulta: View  = view.findViewById(R.id.dotConsultaCalendario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dia_calendario, parent, false)
        return DiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaViewHolder, position: Int) {
        val diaStr = diasNoMes[position]

        if (diaStr.isBlank()) {
            // Padding cell
            holder.tvDia.text       = ""
            holder.dotConsulta.visibility = View.INVISIBLE
            holder.view.setBackgroundColor(Color.TRANSPARENT)
            holder.view.setOnClickListener(null)
            return
        }

        holder.tvDia.text = diaStr

        // Build "yyyy-MM-dd" for this cell
        val dataApi = String.format("%04d-%02d-%02d", ano, mes + 1, diaStr.toInt())

        val temConsulta = datasComConsulta.contains(dataApi)
        val selecionado = dataSelecionada == dataApi

        holder.dotConsulta.visibility = if (temConsulta) View.VISIBLE else View.INVISIBLE

        if (selecionado) {
            holder.view.setBackgroundResource(R.drawable.bg_dia_selecionado)
            holder.tvDia.setTextColor(Color.WHITE)
        } else {
            holder.view.setBackgroundColor(Color.TRANSPARENT)
            holder.tvDia.setTextColor(Color.parseColor("#1E293B"))
        }

        holder.view.setOnClickListener { onDiaClick(diaStr) }
    }

    override fun getItemCount(): Int = diasNoMes.size

    // ─── Public update methods ────────────────────────────────────────────────

    fun atualizarMes(
        dias: List<String>,
        ano: Int,
        mes: Int,
        selecao: String?
    ) {
        diasNoMes = dias
        this.ano = ano
        this.mes = mes
        dataSelecionada = selecao

        notifyDataSetChanged()
    }

    fun atualizarDatasComConsulta(datas: Set<String>) {
        datasComConsulta = datas
        notifyDataSetChanged()
    }

    fun atualizarSelecao(dataApi: String?) {
        dataSelecionada = dataApi
        notifyDataSetChanged()
    }
}
