package com.example.medicoapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.data.remote.DTO.bloqueioagenda.BloqueioAgendaResponseDto

class BloqueioAgendaAdapter(
    private var bloqueios: List<BloqueioAgendaResponseDto>,
    private val onExcluir: (BloqueioAgendaResponseDto) -> Unit
) : RecyclerView.Adapter<BloqueioAgendaAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPeriodo: TextView     = itemView.findViewById(R.id.tvPeriodoBloqueio)
        val tvMotivo: TextView      = itemView.findViewById(R.id.tvMotivoBloqueio)
        val btnExcluir: ImageButton = itemView.findViewById(R.id.btnExcluirBloqueio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bloqueio_agenda, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = bloqueios.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bloqueio = bloqueios[position]
        // data: "yyyy-MM-dd", formato amigável
        holder.tvPeriodo.text = formatarData(bloqueio.data)
        holder.tvMotivo.text  = "${bloqueio.horaInicio} – ${bloqueio.horaFim}"
        holder.btnExcluir.setOnClickListener { onExcluir(bloqueio) }
    }

    fun atualizarLista(novaLista: List<BloqueioAgendaResponseDto>) {
        bloqueios = novaLista
        notifyDataSetChanged()
    }

    private fun formatarData(data: String): String {
        return try {
            val p = data.split("-")
            "${p[2]}/${p[1]}/${p[0]}"
        } catch (_: Exception) { data }
    }
}