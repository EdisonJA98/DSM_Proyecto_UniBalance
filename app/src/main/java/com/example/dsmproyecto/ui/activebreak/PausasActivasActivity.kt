package com.example.dsmproyecto.ui.activebreak

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.dsmproyecto.R
import com.example.dsmproyecto.ui.activebreak.eyecare.CuidadoVisualActivity
import com.example.dsmproyecto.ui.activebreak.stretchingexercises.EstiramientoActivity
import com.example.dsmproyecto.ui.activebreak.scheduler.ListaPausasProgramadasActivity
import com.example.dsmproyecto.ui.activebreak.scheduler.ProgramarPausaCompletaActivity

class PausasActivasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pausas_activas)

        // 1. Configurar el botón de retroceso (Back Button)
        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish() // Cierra esta Activity y vuelve a MainActivity
        }

        // 2. Configurar las opciones del menú
        setupPausasOptions()

        // 3. Configurar Botones de Programación
        setupBottomButtons()
    }

    /**
     * Configura las dos opciones de pausa activa (Estiramiento y Descanso Visual).
     */
    private fun setupPausasOptions() {

        // --------------------------------------------------------------------
        // OPCIÓN 1: EJERCICIOS DE ESTIRAMIENTO (Pausa Activa)
        // --------------------------------------------------------------------
        val estiramientoView = findViewById<View>(R.id.option_estiramiento)
        configurePausaOption(
            view = estiramientoView,
            title = "Ejercicios de estiramiento",
            iconResId = R.drawable.ic_stretch // Ícono para pausas ergonómicas
        )

        // Evento de clic: El flujo principal dice que lleva a la animación de estiramiento (20s)
        estiramientoView.setOnClickListener {
            val intent = Intent(this, EstiramientoActivity::class.java)
            startActivity(intent)
        }

        // --------------------------------------------------------------------
        // OPCIÓN 2: DESCANSO VISUAL (Cuidado Visual)
        // --------------------------------------------------------------------
        val descansoVisualView = findViewById<View>(R.id.option_descanso_visual)
        configurePausaOption(
            view = descansoVisualView,
            title = "Descanso visual",
            iconResId = R.drawable.ic_eye // Ícono para cuidado visual
        )

        // Evento de clic: El flujo principal dice que lleva a la animación de ejercicios oculares
        descansoVisualView.setOnClickListener {
            val intent = Intent(this, CuidadoVisualActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Función helper para configurar el título y el ícono de un layout de pausa incluido.
     */
    private fun configurePausaOption(view: View, title: String, iconResId: Int) {
        // Accedemos a los elementos internos definidos en item_pausa_option.xml
        val titleTextView = view.findViewById<TextView>(R.id.tv_pausa_title)
        val iconImageView = view.findViewById<ImageView>(R.id.iv_pausa_icon)

        // Asignamos los valores
        titleTextView.text = title
        iconImageView.setImageResource(iconResId)
    }

    /**
     * Configura los botones de Programar Nueva y Ver Historial.
     */
    private fun setupBottomButtons() {
        // Botón 1: Programar Nueva (Lleva a la pantalla unificada de programación)
        findViewById<View>(R.id.btn_programar_nueva).setOnClickListener {
            // 💡 CAMBIO: Apunta a la nueva Activity unificada
            val intent = Intent(this, ProgramarPausaCompletaActivity::class.java)
            startActivity(intent)
        }

        // Botón 2: Ver Programadas (Lleva a la lista/historial)
        findViewById<View>(R.id.btn_ver_programadas).setOnClickListener {
            // 💡 CONEXIÓN: Abre la lista de historial
            val intent = Intent(this, ListaPausasProgramadasActivity::class.java)
            startActivity(intent)
        }
    }
}