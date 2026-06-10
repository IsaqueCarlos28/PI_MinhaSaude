package com.example.medicoapplication.UI.adapters

import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaResponseDto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R

class ConsultaOfertadaPreviewAdapter(
    private var consultas: List<ConsultaOfertadaResponseDto>,
    private val onClick: (ConsultaOfertadaResponseDto) -> Unit
) : RecyclerView.Adapter<ConsultaOfertadaPreviewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val tvEspecialidade: TextView =
            itemView.findViewById(R.id.tvEspecialidade)

        private val tvTipoConsulta: TextView =
            itemView.findViewById(R.id.tvTipoConsulta)

        private val tvValor: TextView =
            itemView.findViewById(R.id.tvValor)

        fun bind(item: ConsultaOfertadaResponseDto) {

            tvEspecialidade.text =
                item.especialidade?.nome ?: "Especialidade"

            tvTipoConsulta.text =
                item.tipoConsulta.name

            tvValor.text =
                "R$ ${item.valorConsulta}"

            itemView.setOnClickListener {
                onClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_consulta_ofertada_preview,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(consultas[position])
    }

    override fun getItemCount(): Int =
        consultas.size

    fun atualizarLista(
        novaLista: List<ConsultaOfertadaResponseDto>
    ) {
        consultas = novaLista
        notifyDataSetChanged()
    }
}