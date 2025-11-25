package com.example.dsmproyecto.ui.activebreak.eyecare

import AyudaCuidadoVisualDialog
import ConfirmExitDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.dsmproyecto.R

class CuidadoVisualActivity : AppCompatActivity() {

    // Duración de la pausa: 20 segundos en milisegundos
    private val TOTAL_TIME_MS: Long = 20500
    private var timeLeftMS: Long = TOTAL_TIME_MS // Tiempo restante
    private lateinit var countDownTimer: CountDownTimer // Objeto temporizador
    private var isTimerRunning = false // Estado del temporizador

    // Declaramos el botón globalmente para acceder desde startTimer()
    private lateinit var btnPausePlay: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuidado_visual)

        // 1. Configurar el botón de retroceso (Flecha superior izquierda)
        findViewById<View>(R.id.btn_back).setOnClickListener {
            // 💡 CAMBIO: En lugar de cerrar directo, pedimos confirmación
            showExitConfirmationDialog()
        }

        // 2. Configurar el botón de Ayuda
        findViewById<View>(R.id.btn_help).setOnClickListener {
            // Creamos una instancia del diálogo de ayuda
            val dialog = AyudaCuidadoVisualDialog()
            dialog.show(supportFragmentManager, "AyudaCuidadoVisual")
        }

        // 3. Lógica del temporizador
        updateTimerText()
        setupTimerControls()
    }

    /**
     * 💡 NUEVO: Muestra el diálogo de confirmación de salida.
     */
    private fun showExitConfirmationDialog() {
        // 💡 NUEVO: Si el temporizador está corriendo, lo pausamos automáticamente
        if (isTimerRunning) {
            pauseTimer()
        }

        val dialog = ConfirmExitDialog()
        dialog.show(supportFragmentManager, "ConfirmExit")
    }

    /**
     * 💡 NUEVA FUNCIÓN HELPER: Pausa el temporizador y actualiza la UI.
     * (Extraemos esta lógica para usarla aquí y en el botón de Pausa)
     */
    private fun pauseTimer() {
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        isTimerRunning = false
        btnPausePlay.setImageResource(R.drawable.ic_play) // Cambiar icono a Play
    }

    /**
     * 💡 NUEVO: Captura el botón "Atrás" del sistema (físico o gesto).
     */
    override fun onBackPressed() {
        // En lugar de cerrar la app, mostramos el diálogo
        showExitConfirmationDialog()
    }

    /**
     * Crea y arranca el temporizador de cuenta regresiva.
     */
    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftMS, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeftMS = millisUntilFinished
                updateTimerText()
            }

            override fun onFinish() {
                timeLeftMS = 0
                updateTimerText()
                isTimerRunning = false

                // Al finalizar, volvemos el botón al estado de Play
                btnPausePlay.setImageResource(R.drawable.ic_play)

                Toast.makeText(this@CuidadoVisualActivity, "Pausa finalizada", Toast.LENGTH_LONG).show()
            }
        }.start()

        isTimerRunning = true

        // Al iniciar el timer, cambiamos el icono a PAUSA visualmente
        btnPausePlay.setImageResource(R.drawable.ic_pause)
    }

    /**
     * Actualiza el TextView con el tiempo restante formateado a "mm:ss".
     */
    private fun updateTimerText() {
        val seconds = (timeLeftMS / 1000) % 60
        val minutes = (timeLeftMS / 1000) / 60

        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        findViewById<TextView>(R.id.tv_timer).text = timeFormatted
    }

    /**
     * Maneja la lógica de los botones Pausa/Play y Reinicio.
     */
    private fun setupTimerControls() {
        // Inicializamos la variable global
        btnPausePlay = findViewById(R.id.btn_pause_play)
        val btnRestart = findViewById<ImageButton>(R.id.btn_restart)

        // Aseguramos que arranque visualmente con el icono de PLAY
        btnPausePlay.setImageResource(R.drawable.ic_play)

        // Lógica del botón PAUSA / PLAY
        btnPausePlay.setOnClickListener {
            if (isTimerRunning) {
                // Si está corriendo, usamos nuestra nueva función
                pauseTimer()
            } else {
                startTimer()
            }
        }

        // Lógica del botón REINICIAR
        btnRestart.setOnClickListener {
            // También podemos usar pauseTimer aquí para detener antes de reiniciar
            pauseTimer()

            timeLeftMS = TOTAL_TIME_MS
            updateTimerText()
            // (El icono ya se puso en Play gracias a pauseTimer)
        }
    }
}