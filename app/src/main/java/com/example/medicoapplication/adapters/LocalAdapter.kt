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
        val tvEndereco: TextView =
            itemView.findViewById(R.id.tvEnderecoLocal)
        val btnEditar: ImageButton =
            itemView.findViewById(R.id.btnEditarLocal)
        val btnExcluir: ImageButton =
            itemView.findViewById(R.id.btnExcluirLocal)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_local, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int = locais.size

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
        val local = locais[position]
        holder.tvNome.text = local.nome
        holder.tvEndereco.text = local.endereco
        holder.btnEditar.setOnClickListener {
            onEditar(local)
        }
        holder.btnExcluir.setOnClickListener {
            onExcluir(local)
        }
    }
    fun atualizarLista(novaLista: List<LocalResponseDto>) {
        locais = novaLista
        notifyDataSetChanged()
    }
}
