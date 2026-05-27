package com.example.medicoapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto
class MedicoAdapter(
    private var medicos: List<MedicoResponseDto>,
    private val onSelecionar: (MedicoResponseDto) -> Unit
) : RecyclerView.Adapter<MedicoAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNome: TextView = itemView.findViewById(R.id.tvNomeMedico)
        val tvEspecialidade: TextView =
            itemView.findViewById(R.id.tvEspecialidadeMedico)
        val btnSelecionar: Button =
            itemView.findViewById(R.id.btnSelecionarMedico)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medico, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int = medicos.size

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
        val medico = medicos[position]
        holder.tvNome.text = medico.nome
        holder.tvEspecialidade.text = medico.especialidade.nome
        holder.btnSelecionar.setOnClickListener {
            onSelecionar(medico)
        }
    }
    fun atualizarLista(novaLista: List<MedicoResponseDTO>) {
        medicos = novaLista
        notifyDataSetChanged()
    }
}
