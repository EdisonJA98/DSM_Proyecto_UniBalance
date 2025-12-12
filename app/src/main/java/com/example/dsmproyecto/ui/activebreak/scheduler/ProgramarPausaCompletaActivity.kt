package com.example.dsmproyecto.ui.activebreak.scheduler

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.dsmproyecto.ui.MainActivity
import com.example.dsmproyecto.R
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale


class ProgramarPausaCompletaActivity : AppCompatActivity() {

    private lateinit var etTiempo: EditText
    private lateinit var spinnerPausas: Spinner

    // Launcher para pedir permiso de notificación
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permiso concedido, intentamos programar de nuevo
                validarYProgramar()
            } else {
                Toast.makeText(this, "Se necesita permiso para notificarte", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programar_pausa_completa)

        createNotificationChannel() // Crear canal al inicio

        etTiempo = findViewById(R.id.et_tiempo_minutos)
        spinnerPausas = findViewById(R.id.spinner_pausas)

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<View>(R.id.btn_help).setOnClickListener {
            val dialog = AyudaProgramacionDialog()
            dialog.show(supportFragmentManager, "AyudaProgramacion")
        }

        setupSpinner()

        findViewById<Button>(R.id.btn_confirmar).setOnClickListener {
            checkPermissionAndSchedule()
        }
    }

    private fun setupSpinner() {
        val opciones = listOf("Ejercicios de estiramiento", "Descanso visual", "Decidir después")

        // 1. Vista "Cerrada" (Usa el archivo anterior, fondo transparente/heredado)
        // Esto mantiene el diseño turquesa de tu pantalla principal
        val adapter = ArrayAdapter(
            this,
            R.layout.pa_progpausa_tpa_spinnerselected_item,
            opciones
        )

        // 2. Vista "Desplegada" (Usa el NUEVO archivo, fondo blanco forzado)
        // Esto arregla el menú oscuro en el Samsung A15
        adapter.setDropDownViewResource(R.layout.pa_progpausa_tpa_spinnerdropdown_item)

        spinnerPausas.adapter = adapter
    }

    private fun checkPermissionAndSchedule() {
        // CORRECCIÓN 1: Usamos el valor numérico 33 (Android 13/Tiramisu) directamente
        // y el string del permiso para evitar errores si compileSdk < 33
        if (Build.VERSION.SDK_INT >= 33) {
            val permissionName = "android.permission.POST_NOTIFICATIONS"
            if (ContextCompat.checkSelfPermission(this, permissionName) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                validarYProgramar()
            } else {
                requestPermissionLauncher.launch(permissionName)
            }
        } else {
            validarYProgramar()
        }
    }

    private fun validarYProgramar() {
        val tiempoTexto = etTiempo.text.toString()
        if (tiempoTexto.isEmpty()) {
            etTiempo.error = "Ingresa el tiempo"
            return
        }
        val minutos = tiempoTexto.toIntOrNull()
        if (minutos == null || minutos < 1) {
            etTiempo.error = "Mínimo 1 minuto"
            return
        }

        // Mapear selección del spinner a códigos internos
        val tipoSeleccionado = when (spinnerPausas.selectedItemPosition) {
            0 -> "ESTIRAMIENTO"
            1 -> "VISUAL"
            else -> "DECIDIR"
        }

        programarAlarma(minutos, tipoSeleccionado)
    }

    private fun programarAlarma(minutos: Int, tipo: String) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Generamos un ID único basado en el tiempo actual
        val notificationId = System.currentTimeMillis().toInt()

        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("TIPO_PAUSA", tipo)
            putExtra("NOTIFICATION_ID", notificationId)
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getBroadcast(this, notificationId, intent, flags)

        // Calcular tiempo de disparo
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, minutos)
        val triggerTime = calendar.timeInMillis

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }

            // 💡 NUEVO: Guardar en el historial (PausasStorage)
            guardarEnHistorial(notificationId, calendar, tipo)

            Toast.makeText(this, "¡Alarma programada en $minutos minutos!", Toast.LENGTH_LONG).show()

            val intentHome = Intent(this, MainActivity::class.java)
            intentHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intentHome)
            finish()

        } catch (e: SecurityException) {
            Toast.makeText(this, "Error de permisos de alarma", Toast.LENGTH_SHORT).show()
        }
    }

    // Función auxiliar para formatear y guardar
    private fun guardarEnHistorial(id: Int, calendar: Calendar, tipoCodigo: String) {
        // 1. Formatear la hora (ej: "14:30")
        val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())
        val horaTexto = formatoHora.format(calendar.time)

        // 2. Obtener descripción legible
        val descripcion = when(tipoCodigo) {
            "ESTIRAMIENTO" -> "Ejercicios de estiramiento"
            "VISUAL" -> "Descanso visual"
            else -> "Pausa por definir"
        }

        // 3. Crear objeto y guardar
        val nuevaPausa = AlarmaPausa(id, horaTexto, tipoCodigo, descripcion)
        PausasStorage.guardarPausa(this, nuevaPausa)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Pausas Activas"
            val descriptionText = "Recordatorios para realizar pausas activas"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("channel_pausas_activas", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}