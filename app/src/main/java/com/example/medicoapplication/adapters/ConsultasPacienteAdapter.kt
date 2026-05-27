package com.example.medicoapplication.adapters

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
    private val onReagendar: (ConsultaResponseDto) -> Unit,
    private val onCancelar:  (ConsultaResponseDto) -> Unit
) : RecyclerView.Adapter<ConsultasPacienteAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNomeMedico: TextView = itemView.findViewById(R.id.tvNomeMedicoItem)
        val tvData:       TextView = itemView.findViewById(R.id.tvDataConsultaItem)
        val btnReagendar: Button   = itemView.findViewById(R.id.btnReagendar)
        val btnCancelar:  Button   = itemView.findViewById(R.id.btnCancelar)
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
            append(consulta.nomeMedico ?: "Medico nao informado")
            if (!consulta.nomeConvenio.isNullOrBlank()) {
                append(" - ${consulta.nomeConvenio}")
            }
        }
        holder.tvNomeMedico.text = labelMedico
        holder.tvData.text = formatarDataHora(consulta.data, consulta.horaInicio)

        val podeAcionar = consulta.status == StatusConsulta.AGENDADA
        holder.btnReagendar.visibility = if (podeAcionar) View.VISIBLE else View.GONE
        holder.btnCancelar.visibility  = if (podeAcionar) View.VISIBLE else View.GONE

        holder.btnReagendar.setOnClickListener { onReagendar(consulta) }
        holder.btnCancelar.setOnClickListener  { onCancelar(consulta)  }
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
            "$dataFormatada as $horaFormatada"
        } catch (e: Exception) {
            "$data as $hora"
        }
}
}