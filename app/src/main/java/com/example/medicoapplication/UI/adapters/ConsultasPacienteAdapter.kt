package com.example.medicoapplication.UI.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.data.remote.DTO.StatusConsulta
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto

class ConsultasPacienteAdapter(
    private var consultas: List<ConsultaResponseDto>,
    private val onItemClick: (ConsultaResponseDto) -> Unit = {},
) : RecyclerView.Adapter<ConsultasPacienteAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNomeMedico: TextView = itemView.findViewById(R.id.tvNomeMedicoItem)
        val tvData:       TextView = itemView.findViewById(R.id.tvDataConsultaItem)
        val btnVerConsulta: Button   = itemView.findViewById(R.id.btnReagendar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_consulta_paciente, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = consultas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val consulta = consultas[position]

        val labelMedico = buildString {
            append(consulta.nomeMedico ?: "Médico não informado")
            if (!consulta.nomeConvenio.isNullOrBlank()) append(" - ${consulta.nomeConvenio}")
        }
        holder.tvNomeMedico.text = labelMedico
        holder.tvData.text = formatarDataHora(consulta.data, consulta.horaInicio)

        // Clique no item abre o detalhe
        holder.itemView.setOnClickListener { onItemClick(consulta) }

        val podeAcionar = consulta.status == StatusConsulta.AGENDADA
        holder.btnVerConsulta.visibility = if (podeAcionar) View.VISIBLE else View.GONE
        holder.btnVerConsulta.setOnClickListener { onReagendar(consulta) }
    }

    fun atualizarLista(novaLista: List<ConsultaResponseDto>) {
        consultas = novaLista
        notifyDataSetChanged()
    }

    private fun formatarDataHora(data: String, hora: String): String {
        return try {
            val partes = data.split("-")
            val dataFormatada = "${partes[2]}/${partes[1]}/${partes[0]}"
            val horaFormatada = hora.substring(0, 5)
            "$dataFormatada às $horaFormatada"
        } catch (e: Exception) {
            "$data às $hora"
        }
    }
}
