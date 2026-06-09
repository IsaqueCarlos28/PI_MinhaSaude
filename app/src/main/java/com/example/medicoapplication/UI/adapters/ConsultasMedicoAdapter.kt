package com.example.medicoapplication.UI.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.data.remote.DTO.StatusConsulta
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto

class ConsultasMedicoAdapter(
    private var consultas: List<ConsultaResponseDto>,
    private val onItemClick: (ConsultaResponseDto) -> Unit
) : RecyclerView.Adapter<ConsultasMedicoAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNomePaciente: TextView = view.findViewById(R.id.tv_nome_paciente)
        val tvData: TextView         = view.findViewById(R.id.tv_data_consulta)
        val tvStatus: TextView       = view.findViewById(R.id.tv_status_consulta)
        val tvHorario: TextView      = view.findViewById(R.id.tv_horario_consulta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_consulta_medico, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val consulta = consultas[position]

        holder.tvNomePaciente.text = consulta.nomePaciente ?: "—"
        holder.tvData.text         = consulta.data
        holder.tvHorario.text      = consulta.horaInicio

        val (label, cor) = when (consulta.status) {
            StatusConsulta.AGENDADA  -> "Agendada"  to Color.parseColor("#3B82F6")
            StatusConsulta.REALIZADA -> "Realizada" to Color.parseColor("#22C55E")
            StatusConsulta.CANCELADA -> "Cancelada" to Color.parseColor("#EF4444")
        }
        holder.tvStatus.text = label
        holder.tvStatus.backgroundTintList = ColorStateList.valueOf(cor)

        holder.itemView.setOnClickListener { onItemClick(consulta) }
    }

    override fun getItemCount() = consultas.size

    fun atualizarLista(novaLista: List<ConsultaResponseDto>) {
        consultas = novaLista
        notifyDataSetChanged()
    }
}
