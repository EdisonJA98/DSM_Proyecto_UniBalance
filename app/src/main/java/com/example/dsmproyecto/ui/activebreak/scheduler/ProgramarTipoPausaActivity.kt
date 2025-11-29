package com.example.dsmproyecto.ui.activebreak.scheduler

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dsmproyecto.R

class ProgramarTipoPausaActivity : AppCompatActivity() {

    private var selectedTimeMinutes: Int = 0
    private var selectedType: String? = null // "ESTIRAMIENTO", "VISUAL", "DECIDIR"

    // Referencias a las vistas contenedoras (includes)
    private lateinit var optEstiramiento: View
    private lateinit var optVisual: View
    private lateinit var optDecidir: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programar_tipo_pausa)

        // Recibimos el tiempo de la activity anterior
        selectedTimeMinutes = intent.getIntExtra("EXTRA_TIME_MINUTES", 0)

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        // Inicializar vistas
        optEstiramiento = findViewById(R.id.option_estiramiento)
        optVisual = findViewById(R.id.option_visual)
        optDecidir = findViewById(R.id.option_decidir_despues)

        // Configurar contenido visual (Texto e Iconos)
        configureOption(optEstiramiento, "Ejercicios de estiramiento", R.drawable.ic_stretch)
        configureOption(optVisual, "Descanso visual", R.drawable.ic_eye)
        // Usamos un icono genérico para decidir después (ic_help)
        configureOption(optDecidir, "Decidir después", R.drawable.ic_help)

        // Configurar Clics para Selección
        optEstiramiento.setOnClickListener { selectOption("ESTIRAMIENTO") }
        optVisual.setOnClickListener { selectOption("VISUAL") }
        optDecidir.setOnClickListener { selectOption("DECIDIR") }

        // Botón Programar
        findViewById<Button>(R.id.btn_programar_final).setOnClickListener {
            if (selectedType == null) {
                Toast.makeText(this, "Selecciona un tipo de pausa", Toast.LENGTH_SHORT).show()
            } else {
                programarNotificacion()
            }
        }
    }

    private fun configureOption(view: View, title: String, iconResId: Int) {
        view.findViewById<TextView>(R.id.tv_pausa_title).text = title
        view.findViewById<ImageView>(R.id.iv_pausa_icon).setImageResource(iconResId)
    }

    private fun selectOption(type: String) {
        selectedType = type

        // Resetear todos a inactivo (usamos el fondo sin borde que creamos para las opciones)
        // Usamos bg_option_white_rounded para el fondo normal y bg_paso_activo para resaltar
        optEstiramiento.setBackgroundResource(R.drawable.bg_option_white_rounded)
        optVisual.setBackgroundResource(R.drawable.bg_option_white_rounded)
        optDecidir.setBackgroundResource(R.drawable.bg_option_white_rounded)

        // Activar el seleccionado (borde turquesa)
        when (type) {
            // Nota: Aquí reutilizamos bg_paso_activo que tiene un borde más grueso para resaltar
            "ESTIRAMIENTO" -> optEstiramiento.setBackgroundResource(R.drawable.bg_paso_activo)
            "VISUAL" -> optVisual.setBackgroundResource(R.drawable.bg_paso_activo)
            "DECIDIR" -> optDecidir.setBackgroundResource(R.drawable.bg_paso_activo)
        }
    }

    private fun programarNotificacion() {
        // Lógica de programación de alarma/notificación (PENDIENTE DE IMPLEMENTAR)

        val mensaje = when(selectedType) {
            "ESTIRAMIENTO" -> "Estiramiento programado en $selectedTimeMinutes min. (Tiempo: $selectedTimeMinutes min)"
            "VISUAL" -> "Descanso Visual programado en $selectedTimeMinutes min. (Tiempo: $selectedTimeMinutes min)"
            "DECIDIR" -> "Recordatorio genérico programado en $selectedTimeMinutes min. (Tiempo: $selectedTimeMinutes min)"
            else -> "Error: Selecciona una opción."
        }

        Toast.makeText(this, "Programación exitosa: $mensaje", Toast.LENGTH_LONG).show()

        // Finalizar el flujo de programación
        finish()
    }
}