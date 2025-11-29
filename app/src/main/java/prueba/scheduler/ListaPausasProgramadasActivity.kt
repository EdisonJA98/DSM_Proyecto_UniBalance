package com.example.dsmproyecto.ui.activebreak.scheduler

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dsmproyecto.R

class ListaPausasProgramadasActivity : AppCompatActivity() {

    // Modelo de datos simple para nuestras pruebas
    data class AlarmaPausa(
        val id: Int,
        val hora: String,
        val tipo: String, // "VISUAL", "ESTIRAMIENTO"
        val descripcion: String
    )

    // Lista simulada de alarmas (en el futuro vendrá de una Base de Datos)
    private val listaAlarmas = arrayListOf(
        AlarmaPausa(1, "14:30 PM", "VISUAL", "Descanso visual"),
        AlarmaPausa(2, "16:00 PM", "ESTIRAMIENTO", "Ejercicios de estiramiento"),
        AlarmaPausa(3, "17:45 PM", "VISUAL", "Descanso visual")
    )

    private lateinit var containerItems: LinearLayout
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var scrollContainer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_pausas_programadas)

        // Referencias UI
        containerItems = findViewById(R.id.ll_lista_items)
        layoutEmptyState = findViewById(R.id.layout_empty_state)
        scrollContainer = findViewById(R.id.scroll_container)

        // 1. Botón Retroceso
        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        // 2. Botón Flotante (Agregar Nueva)
        findViewById<View>(R.id.fab_add).setOnClickListener {
            val intent = Intent(this, ProgramarTiempoActivity::class.java)
            startActivity(intent)
        }

        // 3. Renderizar la lista
        renderizarLista()
    }

    /**
     * Dibuja la lista de alarmas o muestra el estado vacío.
     */
    private fun renderizarLista() {
        // Limpiamos la vista antes de dibujar (por si llamamos a esta función al borrar)
        containerItems.removeAllViews()

        if (listaAlarmas.isEmpty()) {
            // Mostrar estado vacío
            layoutEmptyState.visibility = View.VISIBLE
            scrollContainer.visibility = View.GONE
        } else {
            // Mostrar lista
            layoutEmptyState.visibility = View.GONE
            scrollContainer.visibility = View.VISIBLE

            // Inflar un item por cada alarma
            for (alarma in listaAlarmas) {
                agregarItemAlarma(alarma)
            }
        }
    }

    /**
     * Infla el layout 'item_pausa_programada' y configura sus datos.
     */
    private fun agregarItemAlarma(alarma: AlarmaPausa) {
        // Inflamos el XML del item
        val inflater = LayoutInflater.from(this)
        val itemView = inflater.inflate(R.layout.item_pausa_programada, containerItems, false)

        // Referencias dentro del item
        val tvHora = itemView.findViewById<TextView>(R.id.tv_hora_programada)
        val tvTipo = itemView.findViewById<TextView>(R.id.tv_tipo_pausa)
        val ivIcono = itemView.findViewById<ImageView>(R.id.iv_pausa_icon)
        val btnEdit = itemView.findViewById<ImageButton>(R.id.btn_edit)
        val btnDelete = itemView.findViewById<ImageButton>(R.id.btn_delete)

        // Asignar datos
        tvHora.text = alarma.hora
        tvTipo.text = alarma.descripcion

        if (alarma.tipo == "VISUAL") {
            ivIcono.setImageResource(R.drawable.ic_eye)
        } else {
            ivIcono.setImageResource(R.drawable.ic_stretch)
        }

        // Configurar Botones de Acción
        btnEdit.setOnClickListener {
            Toast.makeText(this, "Editar alarma de las ${alarma.hora}", Toast.LENGTH_SHORT).show()
            // Aquí abrirías ProgramarTiempoActivity con los datos pre-cargados
        }

        btnDelete.setOnClickListener {
            // Simular eliminación: Borrar de la lista y redibujar
            listaAlarmas.remove(alarma)
            renderizarLista() // Actualiza la UI (si queda vacía, muestra el estado vacío)
            Toast.makeText(this, "Alarma eliminada", Toast.LENGTH_SHORT).show()
        }

        // Añadir la vista inflada al contenedor vertical
        containerItems.addView(itemView)
    }
}