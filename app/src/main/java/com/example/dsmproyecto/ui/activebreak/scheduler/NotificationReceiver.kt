package com.example.dsmproyecto.ui.activebreak.scheduler

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.dsmproyecto.R
import com.example.dsmproyecto.ui.activebreak.PausasActivasActivity
import com.example.dsmproyecto.ui.activebreak.eyecare.CuidadoVisualActivity
import com.example.dsmproyecto.ui.activebreak.stretchingexercises.EstiramientoActivity

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val tipoPausa = intent.getStringExtra("TIPO_PAUSA") ?: "DECIDIR"
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", 0)

        // 1. Eliminar la pausa de la lista de pendientes al sonar
        if (notificationId != 0) {
            PausasStorage.eliminarPausa(context, notificationId)
        }

        // 2. Configurar destino e información visual según el tipo
        val targetIntent: Intent
        val tituloNotificacion: String
        val textoNotificacion: String
        val iconResId: Int

        when (tipoPausa) {
            "ESTIRAMIENTO" -> {
                targetIntent = Intent(context, EstiramientoActivity::class.java)
                tituloNotificacion = "UniBalance: Estiramiento"
                textoNotificacion = "Es momento de tus ejercicios de estiramiento."
                iconResId = R.drawable.ic_stretch
            }
            "VISUAL" -> {
                targetIntent = Intent(context, CuidadoVisualActivity::class.java)
                tituloNotificacion = "UniBalance: Descanso Visual"
                textoNotificacion = "Tus ojos necesitan un descanso. Inicia tu rutina."
                iconResId = R.drawable.ic_eye
            }
            else -> {
                targetIntent = Intent(context, PausasActivasActivity::class.java)
                tituloNotificacion = "UniBalance: Pausa Activa"
                textoNotificacion = "Te toca un descanso. Elige tu actividad ahora."
                iconResId = R.drawable.ic_help
            }
        }

        targetIntent.putExtra("NOTIFICATION_ID_TO_DELETE", notificationId)
        targetIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // 🔒 CORRECCIÓN DE SEGURIDAD (Flags compatibles)
        // Verificamos la versión de Android para usar FLAG_IMMUTABLE solo si es soportado (API 23+)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        // Creamos el PendingIntent usando los flags seguros calculados arriba
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            targetIntent,
            flags
        )

        // 3. Convertir el icono vectorial a Bitmap para el Large Icon
        val largeIconBitmap = vectorToBitmap(context, iconResId)

        // 4. Construir la notificación
        val builder = NotificationCompat.Builder(context, "channel_pausas_activas")
            .setSmallIcon(iconResId)
            .setLargeIcon(largeIconBitmap)
            .setContentTitle(tituloNotificacion)
            .setContentText(textoNotificacion)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(context, R.color.color_accent_primary))

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    /**
     * Función utilitaria para convertir un VectorDrawable (XML) a Bitmap.
     */
    private fun vectorToBitmap(context: Context, drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}