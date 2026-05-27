package com.example.medicoapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.data.remote.DTO.agenda.AgendaResponseDto

class AgendaAdapter(
    private var agendas: List<AgendaResponseDto>,
    private val onEditar: (AgendaResponseDto) -> Unit,
    private val onExcluir: (AgendaResponseDto) -> Unit
) : RecyclerView.Adapter<AgendaAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDiaSemana: TextView = itemView.findViewById(R.id.tvDiaSemana)
        val tvHorario: TextView = itemView.findViewById(R.id.tvHorarioAgenda)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditarAgenda)
        val btnExcluir: ImageButton = itemView.findViewById(R.id.btnExcluirAgenda)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_agenda, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = agendas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val agenda = agendas[position]
        holder.tvDiaSemana.text = agenda.diaSemana.name   // DiaSemana é enum — usar .name
        holder.tvHorario.text = "${agenda.horaInicio} - ${agenda.horaFim}"
        holder.btnEditar.setOnClickListener { onEditar(agenda) }
        holder.btnExcluir.setOnClickListener { onExcluir(agenda) }
    }

    fun atualizarLista(novaLista: List<AgendaResponseDto>) {
        agendas = novaLista
        notifyDataSetChanged()
    }
}