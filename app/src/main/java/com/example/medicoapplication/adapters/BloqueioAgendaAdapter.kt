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
        val tvPeriodo: TextView =
            itemView.findViewById(R.id.tvPeriodoBloqueio)
        val tvMotivo: TextView = itemView.findViewById(R.id.tvMotivoBloqueio)
        val btnExcluir: ImageButton =
            itemView.findViewById(R.id.btnExcluirBloqueio)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bloqueio_agenda, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int = bloqueios.size

    fun updateData(newList: List<Any>) {
        @Suppress("UNCHECKED_CAST")
        when {
            true -> {
                try {
                    val field = this::class.java.declaredFields.firstOrNull {
                        java.util.List::class.java.isAssignableFrom(it.type)
                    }

                    field?.isAccessible = true
                    field?.set(this, newList)
                    notifyDataSetChanged()
                } catch (_: Exception) {
                }
            }
        }
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bloqueio = bloqueios[position]
        holder.tvPeriodo.text = "${bloqueio.dataInicio} até ${bloqueio.dataFim}"
        holder.tvMotivo.text = bloqueio.motivo
        holder.btnExcluir.setOnClickListener {
            onExcluir(bloqueio)
        }
    }
    fun atualizarLista(novaLista: List<BloqueioAgendaResponseDto>) {
        bloqueios = novaLista
        notifyDataSetChanged()
    }
}
