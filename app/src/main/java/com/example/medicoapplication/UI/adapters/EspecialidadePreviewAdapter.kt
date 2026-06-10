package com.example.medicoapplication.UI.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.data.remote.DTO.medicoespecialidade.MedicoEspecialidadeResponseDto

class EspecialidadePreviewAdapter(
    private var especialidades: List<MedicoEspecialidadeResponseDto>,
    private val onClick: (MedicoEspecialidadeResponseDto) -> Unit
) : RecyclerView.Adapter<EspecialidadePreviewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvNome: TextView =
            itemView.findViewById(R.id.tvNomeEspecialidade)

        val tvRqe: TextView =
            itemView.findViewById(R.id.tvRqe)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_especialidade_preview,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun getItemCount(): Int =
        especialidades.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = especialidades[position]

        holder.tvNome.text =
            item.especialidade?.nome ?: ""

        holder.tvRqe.text =
            "RQE: ${item.rqe ?: "-"}"

        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    fun atualizarLista(
        novaLista: List<MedicoEspecialidadeResponseDto>
    ) {
        especialidades = novaLista
        notifyDataSetChanged()
    }
}