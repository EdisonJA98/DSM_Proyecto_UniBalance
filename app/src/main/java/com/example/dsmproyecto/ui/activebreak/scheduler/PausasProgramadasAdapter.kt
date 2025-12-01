package com.example.dsmproyecto.ui.activebreak.scheduler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dsmproyecto.R

class PausasProgramadasAdapter(
    private var listaAlarmas: List<AlarmaPausa>,
    private val onDeleteClick: (AlarmaPausa) -> Unit
) : RecyclerView.Adapter<PausasProgramadasAdapter.PausaViewHolder>() {

    // El ViewHolder: Mantiene las referencias a las vistas de un item
    inner class PausaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHora: TextView = itemView.findViewById(R.id.tv_hora_programada)
        val tvTipo: TextView = itemView.findViewById(R.id.tv_tipo_pausa)
        val ivIcono: ImageView = itemView.findViewById(R.id.iv_pausa_icon)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btn_edit) // Aunque esté oculto, lo referenciamos

        fun bind(alarma: AlarmaPausa) {
            tvHora.text = alarma.horaFormato
            tvTipo.text = alarma.descripcion

            // Configurar icono según tipo
            if (alarma.tipo == "VISUAL") {
                ivIcono.setImageResource(R.drawable.ic_eye)
            } else if (alarma.tipo == "ESTIRAMIENTO") {
                ivIcono.setImageResource(R.drawable.ic_stretch)
            } else {
                ivIcono.setImageResource(R.drawable.ic_help)
            }

            // Ocultar editar por ahora
            btnEdit.visibility = View.GONE

            // Listener de eliminación
            btnDelete.setOnClickListener { onDeleteClick(alarma) }
        }
    }

    // Crea el diseño de la fila (infla el XML)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PausaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_pausa_programada, // Usamos tu layout de item existente
            parent,
            false
        )
        return PausaViewHolder(view)
    }

    // Conecta los datos con el ViewHolder
    override fun onBindViewHolder(holder: PausaViewHolder, position: Int) {
        holder.bind(listaAlarmas[position])
    }

    override fun getItemCount(): Int = listaAlarmas.size

    // Método para actualizar la lista completa
    fun updateData(nuevaLista: List<AlarmaPausa>) {
        listaAlarmas = nuevaLista
        notifyDataSetChanged()
    }
}