package com.example.dsmproyecto.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.dsmproyecto.R
import com.example.dsmproyecto.ui.activebreak.PausasActivasActivity
import com.example.dsmproyecto.ui.breathing.BreathingActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Llama a la función que configura todas las opciones de menú
        setupMenuOptions()
    }

    /**
     * Función que localiza las 6 vistas incluidas y les asigna el texto y el ícono correctos.
     */
    private fun setupMenuOptions() {

        // --------------------------------------------------------------------
        // 1. OPCIÓN: Jugar Trivia Interactiva
        // --------------------------------------------------------------------
        // Accedemos a la vista contenedor completa de la opción (usando el ID del include)
        val triviaView = findViewById<View>(R.id.option_trivia)
        // Configuramos el contenido dentro de esa vista
        configureOption(
            view = triviaView,
            title = "Jugar Trivia Interactiva",
            description = "Pon a prueba tus conocimientos sobre salud y bienestar",
            iconResId = R.drawable.ic_trivia // Ya creaste este icono
        )

        // --------------------------------------------------------------------
        // 2. OPCIÓN: Entrenar lectura rápida
        // --------------------------------------------------------------------
        val lecturaView = findViewById<View>(R.id.option_lectura)
        configureOption(
            view = lecturaView,
            title = "Entrenar lectura rápida",
            description = "Mejora tu velocidad y comprensión lectora",
            iconResId = R.drawable.ic_book // Ya creaste este icono
        )

        // --------------------------------------------------------------------
        // 3. OPCIÓN: Realizar respiración guiada
        // --------------------------------------------------------------------
        val respiracionView = findViewById<View>(R.id.option_respiracion)
        configureOption(
            view = respiracionView,
            title = "Realizar respiración guiada",
            description = "Relájate con una rutina de guiada de respiración",
            iconResId = R.drawable.ic_wellness // Usamos el ícono de bienestar/meditación
        )

        // --------------------------------------------------------------------
        // 4. OPCIÓN: Ejecutar pausas activas
        // --------------------------------------------------------------------
        val pausasView = findViewById<View>(R.id.option_pausas)
        configureOption(
            view = pausasView,
            title = "Ejecutar pausas activas",
            description = "Realiza pausas guiadas para sentirte mejor",
            iconResId = R.drawable.ic_stretch // Usamos el ícono de estiramiento/movimiento
        )

        // --------------------------------------------------------------------
        // 5. OPCIÓN: Registrar Estado Emocional
        // --------------------------------------------------------------------
        val emocionalView = findViewById<View>(R.id.option_emocional)
        configureOption(
            view = emocionalView,
            title = "Registrar Estado Emocional",
            description = "Registra cómo te sientes actualmente",
            iconResId = R.drawable.ic_brain // Usamos el ícono de cerebro/mente
        )

        // --------------------------------------------------------------------
        // 6. OPCIÓN: Practica de Exposicion Oral
        // --------------------------------------------------------------------
        val exposicionView = findViewById<View>(R.id.option_exposicion)
        configureOption(
            view = exposicionView,
            title = "Practica de Exposicion Oral",
            description = "Mejora tus habilidades para hablar en público y exposicion",
            iconResId = R.drawable.ic_mic // Ya creaste este icono
        )

        // --------------------------------------------------------------------
        // EVENTOS DE CLIC (Usamos las variables locales que ya se definieron)
        // --------------------------------------------------------------------

        // Clic para la opción 3: de Respiración (usando BreathingActivity)
        respiracionView.setOnClickListener {
            val intent = Intent(this, BreathingActivity::class.java)
            startActivity(intent)
        }

        // 💡 CLIC PARA LA OPCIÓN 4: DE PAUSAS ACTIVAS (Usando PausasActivasActivity)
        pausasView.setOnClickListener {
            val intent = Intent(this, PausasActivasActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Función helper (ayudante) para configurar el título, descripción e ícono
     * de un layout incluido.
     * @param view La vista contenedora del include (ej. R.id.option_trivia).
     */
    private fun configureOption(view: View, title: String, description: String, iconResId: Int) {
        // Encontramos los TextViews e ImageView DENTRO del layout incluido (item_menu_option.xml)
        val titleTextView = view.findViewById<TextView>(R.id.tv_option_title)
        val descTextView = view.findViewById<TextView>(R.id.tv_option_description)
        val iconImageView = view.findViewById<ImageView>(R.id.iv_option_icon)

        // Asignamos los valores
        titleTextView.text = title
        descTextView.text = description
        iconImageView.setImageResource(iconResId)
    }
}