package com.example.dsmproyecto.ui.activebreak.scheduler

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.dsmproyecto.R
import com.example.dsmproyecto.ui.activebreak.PausasActivasActivity
import com.example.dsmproyecto.ui.activebreak.eyecare.CuidadoVisualActivity
import com.example.dsmproyecto.ui.activebreak.stretchingexercises.EstiramientoActivity

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val tipoPausa = intent.getStringExtra("TIPO_PAUSA") ?: "DECIDIR"
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", 0)

        // Definir a dónde lleva el clic en la notificación
        val targetIntent = when (tipoPausa) {
            "ESTIRAMIENTO" -> Intent(context, EstiramientoActivity::class.java)
            "VISUAL" -> Intent(context, CuidadoVisualActivity::class.java)
            else -> Intent(context, PausasActivasActivity::class.java)
        }

        // 💡 CLAVE: Pasamos el ID de la notificación a la actividad destino
        // para que sepa qué pausa borrar de la lista
        targetIntent.putExtra("NOTIFICATION_ID_TO_DELETE", notificationId)

        targetIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            targetIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construir la notificación
        val builder = NotificationCompat.Builder(context, "channel_pausas_activas")
            .setSmallIcon(R.drawable.ic_eye) // Asegúrate de tener un icono válido
            .setContentTitle("¡Hora de tu Pausa Activa!")
            .setContentText(getMensajePausa(tipoPausa))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Mostrar la notificación
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    private fun getMensajePausa(tipo: String): String {
        return when (tipo) {
            "ESTIRAMIENTO" -> "Es momento de tus ejercicios de estiramiento."
            "VISUAL" -> "Tus ojos necesitan un descanso. Inicia tu rutina visual."
            else -> "Te toca un descanso. Elige tu actividad ahora."
        }
    }
}