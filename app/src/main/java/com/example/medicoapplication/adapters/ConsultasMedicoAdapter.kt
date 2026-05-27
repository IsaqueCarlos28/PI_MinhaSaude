package com.example.medicoapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto

class ConsultasMedicoAdapter(
    private var consultas: List<ConsultaResponseDto>,
    private val onItemClick: (ConsultaResponseDto) -> Unit = {}
) : RecyclerView.Adapter<ConsultasMedicoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNomePaciente: TextView = itemView.findViewById(R.id.tvNomePacienteItem)
        val tvTipoConsulta: TextView = itemView.findViewById(R.id.tvTipoConsultaItem)
        val tvCpfPaciente: TextView  = itemView.findViewById(R.id.tvCpfPacienteItem)
        val tvEndereco: TextView     = itemView.findViewById(R.id.tvEnderecoItem)
        val tvValor: TextView        = itemView.findViewById(R.id.tvValorItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_consulta_medico, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = consultas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val consulta = consultas[position]
        holder.tvNomePaciente.text = consulta.nomePaciente ?: "Paciente"
        // Data e hora formatados: dd/MM/yyyy às HH:mm
        holder.tvTipoConsulta.text = formatarDataHora(consulta.data, consulta.horaInicio)
        holder.tvCpfPaciente.text  = "Status: ${consulta.status?.name ?: "—"}"
        holder.tvEndereco.text     = consulta.nomeConvenio ?: "Particular"
        holder.tvValor.text        = ""  // Valor não disponível no ConsultaResponseDto
        holder.itemView.setOnClickListener { onItemClick(consulta) }
    }

    fun atualizarLista(novaLista: List<ConsultaResponseDto>) {
        consultas = novaLista
        notifyDataSetChanged()
    }

    private fun formatarDataHora(data: String, hora: String): String {
        return try {
            val partes = data.split("-")
            "${partes[2]}/${partes[1]}/${partes[0]} às ${hora.substring(0, 5)}"
        } catch (e: Exception) {
            "$data às $hora"
        }
    }
}