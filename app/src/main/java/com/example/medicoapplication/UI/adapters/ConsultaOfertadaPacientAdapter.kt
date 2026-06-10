package com.example.medicoapplication.UI.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaResponseDto

class ConsultaOfertadaPacientAdapter(
    private val onSelecionar: (ConsultaOfertadaResponseDto) -> Unit
) : RecyclerView.Adapter<ConsultaOfertadaPacientAdapter.ViewHolder>() {

    private val items = mutableListOf<ConsultaOfertadaResponseDto>()

    fun submitList(list: List<ConsultaOfertadaResponseDto>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEspecialidade: TextView = view.findViewById(R.id.tvEspecialidadeItem)
        val tvTipo: TextView = view.findViewById(R.id.tvTipoConsultaItem)
        val tvLocal: TextView = view.findViewById(R.id.tvLocalItem)
        val tvValor: TextView = view.findViewById(R.id.tvValorItem)
        val btnSelecionar: Button = view.findViewById(R.id.btnSelecionarConsulta)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_consulta_ofertada_publica, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = items[position]

        holder.tvEspecialidade.text =
            item.especialidade?.nome ?: "Especialidade"

        holder.tvTipo.text =
            item.tipoConsulta.name

        holder.tvLocal.text =
            item.local?.nome ?: "Local não informado"

        holder.tvValor.text =
            "R$ ${item.valorConsulta}"

        holder.btnSelecionar.setOnClickListener {
            onSelecionar(item)
        }
    }
}