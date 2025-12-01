package com.example.dsmproyecto.ui.activebreak.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dsmproyecto.R

class ListaPausasProgramadasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PausasProgramadasAdapter
    private lateinit var layoutEmptyState: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_pausas_programadas)

        // Referencias UI
        recyclerView = findViewById(R.id.rv_pausas_programadas)
        layoutEmptyState = findViewById(R.id.layout_empty_state)

        // 1. Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this) // Lista vertical estándar

        // Inicializar Adapter vacío al principio
        adapter = PausasProgramadasAdapter(emptyList()) { alarma ->
            eliminarAlarma(alarma)
        }
        recyclerView.adapter = adapter

        // 2. Botones
        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        findViewById<View>(R.id.fab_add).setOnClickListener {
            val intent = Intent(this, ProgramarPausaCompletaActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Cargar datos cada vez que la pantalla se muestra
        cargarDatos()
    }

    private fun cargarDatos() {
        // Obtenemos la lista real guardada
        val listaReal = PausasStorage.obtenerPausas(this)

        // Actualizamos el adaptador
        adapter.updateData(listaReal)

        // Manejo de visibilidad (Lista vs Estado Vacío)
        if (listaReal.isEmpty()) {
            layoutEmptyState.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            layoutEmptyState.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun eliminarAlarma(alarma: AlarmaPausa) {
        // 1. Cancelar la alarma del sistema Android
        cancelarAlarmaDelSistema(alarma.id)

        // 2. Eliminar de la base de datos local (SharedPreferences)
        PausasStorage.eliminarPausa(this, alarma.id)

        // 3. Recargar la lista en pantalla
        cargarDatos()

        Toast.makeText(this, "Alarma cancelada", Toast.LENGTH_SHORT).show()
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