package com.example.dsmproyecto.ui.activebreak.eyecare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.dsmproyecto.R

class CuidadoVisualActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuidado_visual) // Referencia al nuevo layout XML

        // 1. Configurar el botón de retroceso
        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish() // Cierra esta Activity y vuelve a PausasActivasActivity
        }

        // 2. Configurar el botón de Ayuda
        findViewById<View>(R.id.btn_help).setOnClickListener {
            Toast.makeText(this, "Información sobre ejercicios oculares", Toast.LENGTH_SHORT).show()
        }

        // 3. Aquí irá la lógica del temporizador y los controladores (Pausa/Reinicio)
        setupTimerControls()
    }

    // Función auxiliar (por ahora solo con Toast)
    private fun setupTimerControls() {
        findViewById<View>(R.id.btn_pause_play).setOnClickListener {
            Toast.makeText(this, "Botón Pausa/Play presionado", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.btn_restart).setOnClickListener {
            Toast.makeText(this, "Botón Reiniciar presionado", Toast.LENGTH_SHORT).show()
        }
    }
}