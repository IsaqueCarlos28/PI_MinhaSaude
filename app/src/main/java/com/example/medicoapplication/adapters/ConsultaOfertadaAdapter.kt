package com.example.medicoapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaResponseDto
import com.example.medicoapplication.data.remote.DTO.consultaofertada.TipoConsulta

class ConsultaOfertadaAdapter(
    private val onExcluir: (ConsultaOfertadaResponseDto) -> Unit
) : ListAdapter<ConsultaOfertadaResponseDto, ConsultaOfertadaAdapter.ViewHolder>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<ConsultaOfertadaResponseDto>() {
            override fun areItemsTheSame(a: ConsultaOfertadaResponseDto, b: ConsultaOfertadaResponseDto) = a.id == b.id
            override fun areContentsTheSame(a: ConsultaOfertadaResponseDto, b: ConsultaOfertadaResponseDto) = a == b
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEspecialidade: TextView = view.findViewById(R.id.tvEspecialidadeOfertada)
        val tvTipo: TextView = view.findViewById(R.id.tvTipoConsultaOfertada)
        val tvValor: TextView = view.findViewById(R.id.tvValorOfertada)
        val tvDuracao: TextView = view.findViewById(R.id.tvDuracaoOfertada)
        val tvParticular: TextView = view.findViewById(R.id.tvParticularOfertada)
        val tvConvenios: TextView = view.findViewById(R.id.tvConveniosOfertada)
        val btnExcluir: Button = view.findViewById(R.id.btnExcluirConsultaOfertada)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_consulta_ofertada_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvEspecialidade.text = item.especialidade?.nome ?: "Especialidade"
        holder.tvTipo.text = if (item.tipoConsulta == TipoConsulta.PRESENCIAL) "Presencial" else "Teleconsulta"

        // Formatar valor (vem como String da API)
        val valorFormatado = try {
            val v = item.valorConsulta.toDouble()
            "R$ %.2f".format(v)
        } catch (e: Exception) { "R$ ${item.valorConsulta}" }
        holder.tvValor.text = valorFormatado

        // Formatar duração (vem como "PT30M" por exemplo)
        val duracaoMin = parseDuracao(item.duracao)
        holder.tvDuracao.text = "$duracaoMin min"

        holder.tvParticular.text = if (item.aceitaParticular) "✓ Aceita particular" else "✗ Não aceita particular"
        holder.tvParticular.setTextColor(
            if (item.aceitaParticular) 0xFF10B981.toInt() else 0xFF6B7280.toInt()
        )

        val conveniosNomes = item.conveniosAceitos.joinToString(", ") { it.nome }
        holder.tvConvenios.text = if (conveniosNomes.isNotEmpty()) "Convênios: $conveniosNomes" else "Sem convênios"

        holder.btnExcluir.setOnClickListener { onExcluir(item) }
    }

    private fun parseDuracao(duracao: String): Int {
        // "PT30M" → 30, "PT1H" → 60, "PT1H30M" → 90
        return try {
            val hours = Regex("(\\d+)H").find(duracao)?.groupValues?.get(1)?.toInt() ?: 0
            val minutes = Regex("(\\d+)M").find(duracao)?.groupValues?.get(1)?.toInt() ?: 0
            hours * 60 + minutes
        } catch (e: Exception) { 0 }
    }
}
