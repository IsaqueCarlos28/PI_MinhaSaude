package com.example.medicoapplication.UI.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.data.remote.DTO.StatusConsulta
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto

class ConsultasPacienteAdapter(
    private var consultas: List<ConsultaResponseDto>,
    private val onItemClick: (ConsultaResponseDto) -> Unit = {}
) : RecyclerView.Adapter<ConsultasPacienteAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNomeMedico:   TextView  = itemView.findViewById(R.id.tvNomeMedicoItem)
        val tvEspecialidade: TextView = itemView.findViewById(R.id.tvEspecialidadeItem)
        val tvData:         TextView  = itemView.findViewById(R.id.tvDataConsultaItem)
        val tvStatus:       TextView  = itemView.findViewById(R.id.tvStatusItem)
        val imgAvatar:      ImageView = itemView.findViewById(R.id.imgMedicoAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_consulta_paciente, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = consultas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val consulta = consultas[position]

        holder.tvNomeMedico.text    = consulta.nomeMedico ?: "Médico não informado"
        holder.tvEspecialidade.text = consulta.nomeConvenio
            ?.takeIf { it.isNotBlank() }
            ?.let { "Convênio: $it" }
            ?: "Particular"
        holder.tvData.text = formatarDataHora(consulta.data, consulta.horaInicio)

        bindStatus(holder.tvStatus, consulta.status)

        // The whole card opens DetalheConsultaActivity — where cancel/reagendar live
        holder.itemView.setOnClickListener { onItemClick(consulta) }
    }

    fun atualizarLista(novaLista: List<ConsultaResponseDto>) {
        consultas = novaLista
        notifyDataSetChanged()
    }

    // ─── Status badge ────────────────────────────────────────────────────────

    private fun bindStatus(tv: TextView, status: StatusConsulta) {
        when (status) {
            StatusConsulta.AGENDADA -> {
                tv.text = "Agendada"
                tv.setTextColor(Color.parseColor("#16A34A"))
                tv.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#DCFCE7"))
            }
            StatusConsulta.CANCELADA -> {
                tv.text = "Cancelada"
                tv.setTextColor(Color.parseColor("#DC2626"))
                tv.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FEE2E2"))
            }
            StatusConsulta.REALIZADA -> {
                tv.text = "Realizada"
                tv.setTextColor(Color.parseColor("#2563EB"))
                tv.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#DBEAFE"))
            }
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private fun formatarDataHora(data: String, hora: String): String {
        return try {
            val (ano, mes, dia) = data.split("-")
            "$dia/$mes/$ano às ${hora.substring(0, 5)}"
        } catch (e: Exception) {
            "$data às $hora"
        }
    }
}
