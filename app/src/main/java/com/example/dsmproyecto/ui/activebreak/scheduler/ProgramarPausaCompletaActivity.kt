package com.example.dsmproyecto.ui.activebreak.scheduler

import android.Manifest
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
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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

        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("TIPO_PAUSA", tipo)
            putExtra("NOTIFICATION_ID", System.currentTimeMillis().toInt()) // ID único
        }

        // CORRECCIÓN 2: Calcular flags compatibles con versiones antiguas (API 21)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            flags
        )

        // Calcular tiempo de disparo (Ahora + minutos)
        val triggerTime = Calendar.getInstance().timeInMillis + (minutos * 60 * 1000)

        // Programar alarma
        try {
            // CORRECCIÓN 3: Verificar versión para usar setExactAndAllowWhileIdle (API 23+)
            // Si es Android 12+ (API 31/S), verificamos permiso de alarmas exactas
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Para Android 6 a 11
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                // Para Android 5 (Lollipop)
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }

            Toast.makeText(this, "¡Alarma programada en $minutos minutos!", Toast.LENGTH_LONG).show()

            // Volver al Home
            val intentHome = Intent(this, MainActivity::class.java)
            intentHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intentHome)
            finish()

        } catch (e: SecurityException) {
            Toast.makeText(this, "Error de permisos de alarma", Toast.LENGTH_SHORT).show()
        }
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