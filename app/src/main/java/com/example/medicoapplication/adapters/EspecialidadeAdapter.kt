package com.example.medicoapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadeResponseDto

class EspecialidadeAdapter(
    private var especialidades: List<EspecialidadeResponseDto>,
    private val onEditar: (EspecialidadeResponseDto) -> Unit,
    private val onExcluir: (EspecialidadeResponseDto) -> Unit
) : RecyclerView.Adapter<EspecialidadeAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNome: TextView =
            itemView.findViewById(R.id.tvNomeEspecialidade)
        val btnEditar: ImageButton =
            itemView.findViewById(R.id.btnEditarEspecialidade)
        val btnExcluir: ImageButton =
            itemView.findViewById(R.id.btnExcluirEspecialidade)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_especialidade, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int = especialidades.size

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
        val especialidade = especialidades[position]
        holder.tvNome.text = especialidade.nome
        holder.btnEditar.setOnClickListener {
            onEditar(especialidade)
        }
        holder.btnExcluir.setOnClickListener {
            onExcluir(especialidade)
        }
    }
    fun atualizarLista(novaLista: List<EspecialidadeResponseDto>) {
        especialidades = novaLista
        notifyDataSetChanged()
    }
}
