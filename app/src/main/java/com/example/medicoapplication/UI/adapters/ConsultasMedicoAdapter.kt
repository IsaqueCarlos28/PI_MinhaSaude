import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto

class ConsultasMedicoAdapter(
    private var consultas: List<ConsultaResponseDto>,
    private val onItemClick: (ConsultaResponseDto) -> Unit  // (1)
) : RecyclerView.Adapter<ConsultasMedicoAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNomePaciente: TextView = view.findViewById(R.id.tv_nome_paciente)
        val tvData: TextView         = view.findViewById(R.id.tv_data_consulta)
        val tvStatus: TextView       = view.findViewById(R.id.tv_status_consulta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_consulta_medico, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val consulta = consultas[position]
        holder.tvNomePaciente.text = consulta.nomePaciente   // (2)
        holder.tvData.text         = consulta.data
        holder.tvStatus.text       = consulta.status.toString()

        holder.itemView.setOnClickListener {
            onItemClick(consulta)  // (3)
        }
    }

    override fun getItemCount() = consultas.size

    fun atualizarLista(novaLista: List<ConsultaResponseDto>) {  // (4)
        consultas = novaLista
        notifyDataSetChanged()  // substituir por DiffUtil depois
    }
}