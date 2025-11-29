package com.example.dsmproyecto.ui.activebreak.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
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

    private lateinit var containerItems: LinearLayout
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var scrollContainer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_pausas_programadas)

        containerItems = findViewById(R.id.ll_lista_items)
        layoutEmptyState = findViewById(R.id.layout_empty_state)
        scrollContainer = findViewById(R.id.scroll_container)

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        findViewById<View>(R.id.fab_add).setOnClickListener {
            val intent = Intent(this, ProgramarPausaCompletaActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargar la lista cada vez que volvemos a esta pantalla
        renderizarLista()
    }

    private fun renderizarLista() {
        containerItems.removeAllViews()

        // 💡 CARGAR DATOS REALES
        val listaAlarmas = PausasStorage.obtenerPausas(this)

        if (listaAlarmas.isEmpty()) {
            layoutEmptyState.visibility = View.VISIBLE
            scrollContainer.visibility = View.GONE
        } else {
            layoutEmptyState.visibility = View.GONE
            scrollContainer.visibility = View.VISIBLE

            for (alarma in listaAlarmas) {
                agregarItemAlarma(alarma)
            }
        }
    }

    private fun agregarItemAlarma(alarma: AlarmaPausa) {
        val inflater = LayoutInflater.from(this)
        val itemView = inflater.inflate(R.layout.item_pausa_programada, containerItems, false)

        val tvHora = itemView.findViewById<TextView>(R.id.tv_hora_programada)
        val tvTipo = itemView.findViewById<TextView>(R.id.tv_tipo_pausa)
        val ivIcono = itemView.findViewById<ImageView>(R.id.iv_pausa_icon)
        val btnDelete = itemView.findViewById<ImageButton>(R.id.btn_delete)

        // Ocultar botón editar por ahora (implementación futura)
        itemView.findViewById<ImageButton>(R.id.btn_edit).visibility = View.GONE

        // Asignar datos reales
        tvHora.text = alarma.horaFormato
        tvTipo.text = alarma.descripcion

        if (alarma.tipo == "VISUAL") {
            ivIcono.setImageResource(R.drawable.ic_eye)
        } else if (alarma.tipo == "ESTIRAMIENTO") {
            ivIcono.setImageResource(R.drawable.ic_stretch)
        } else {
            ivIcono.setImageResource(R.drawable.ic_help)
        }

        btnDelete.setOnClickListener {
            cancelarAlarmaDelSistema(alarma.id) // Cancelar PendingIntent
            PausasStorage.eliminarPausa(this, alarma.id) // Borrar de almacenamiento
            renderizarLista() // Actualizar UI
            Toast.makeText(this, "Alarma cancelada", Toast.LENGTH_SHORT).show()
        }

        containerItems.addView(itemView)
    }

    private fun cancelarAlarmaDelSistema(notificationId: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            notificationId,
            intent,
            flags
        )

        alarmManager.cancel(pendingIntent)
    }
}