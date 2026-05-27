package com.example.medicoapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaResponseDto
class ConsultaOfertadaAdapter(
    private var consultas: List<ConsultaOfertadaResponseDto>,
    private val onEditar: (ConsultaOfertadaResponseDto) -> Unit,
    private val onExcluir: (ConsultaOfertadaResponseDto) -> Unit
) : RecyclerView.Adapter<ConsultaOfertadaAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTituloConsulta)
        val tvValor: TextView = itemView.findViewById(R.id.tvValorConsulta)
        val btnEditar: ImageButton =
            itemView.findViewById(R.id.btnEditarConsulta)
        val btnExcluir: ImageButton =
            itemView.findViewById(R.id.btnExcluirConsulta)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_consulta_ofertada, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int = consultas.size

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
        val consulta = consultas[position]
        holder.tvTitulo.text = consulta.nome
        holder.tvValor.text = "R$ ${consulta.valor}"
        holder.btnEditar.setOnClickListener {
            onEditar(consulta)
        }
        holder.btnExcluir.setOnClickListener {
            onExcluir(consulta)
        }
    }
    fun atualizarLista(novaLista: List<ConsultaOfertadaResponseDto>) {
        consultas = novaLista
        notifyDataSetChanged()
    }
}