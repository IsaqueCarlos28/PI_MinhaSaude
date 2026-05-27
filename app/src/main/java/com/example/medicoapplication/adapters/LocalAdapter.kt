package com.example.medicoapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.data.remote.DTO.local.LocalResponseDto

class LocalAdapter(
    private var locais: List<LocalResponseDto>,
    private val onEditar: (LocalResponseDto) -> Unit,
    private val onExcluir: (LocalResponseDto) -> Unit
) : RecyclerView.Adapter<LocalAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNome: TextView = itemView.findViewById(R.id.tvNomeLocal)
        val tvEndereco: TextView = itemView.findViewById(R.id.tvEnderecoLocal)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditarLocal)
        val btnExcluir: ImageButton = itemView.findViewById(R.id.btnExcluirLocal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_local, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = locais.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val local = locais[position]
        holder.tvNome.text = local.nome ?: "—"

        // Montar endereço a partir do EnderecoResponseDto
        val end = local.endereco
        holder.tvEndereco.text = if (end != null) {
            buildString {
                if (!end.logradouro.isNullOrBlank()) append(end.logradouro)
                if (!end.numero.isNullOrBlank()) append(", ${end.numero}")
                if (!end.bairro.isNullOrBlank()) append(" - ${end.bairro}")
                if (!end.cidade.isNullOrBlank()) append(", ${end.cidade}")
                if (!end.uf.isNullOrBlank()) append("/${end.uf}")
            }.ifBlank { "Endereço não informado" }
        } else {
            "Endereço não informado"
        }

        holder.btnEditar.setOnClickListener { onEditar(local) }
        holder.btnExcluir.setOnClickListener { onExcluir(local) }
    }

    fun atualizarLista(novaLista: List<LocalResponseDto>) {
        locais = novaLista
        notifyDataSetChanged()
    }
}