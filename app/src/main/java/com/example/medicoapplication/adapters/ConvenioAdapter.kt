package com.example.medicoapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.data.remote.DTO.convenio.ConvenioResponseDto

class ConvenioAdapter(
    private var convenios: List<ConvenioResponseDto>,
    private val onEditar: (ConvenioResponseDto) -> Unit,
    private val onExcluir: (ConvenioResponseDto) -> Unit
) : RecyclerView.Adapter<ConvenioAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNome: TextView = itemView.findViewById(R.id.tvNomeConvenio)
        val btnEditar: ImageButton  = itemView.findViewById(R.id.btnEditarConvenio)
        val btnExcluir: ImageButton = itemView.findViewById(R.id.btnExcluirConvenio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_convenio, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = convenios.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val convenio = convenios[position]
        holder.tvNome.text = convenio.nome
        holder.btnEditar.setOnClickListener  { onEditar(convenio) }
        holder.btnExcluir.setOnClickListener { onExcluir(convenio) }
    }

    fun atualizarLista(novaLista: List<ConvenioResponseDto>) {
        convenios = novaLista
        notifyDataSetChanged()
    }
}