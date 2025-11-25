package com.example.dsmproyecto.ui.activebreak.eyecare

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
    // (Nota: 20500 da un pequeño buffer para que el usuario vea el 20 antes de bajar)
    private val TOTAL_TIME_MS: Long = 20500
    private var timeLeftMS: Long = TOTAL_TIME_MS // Tiempo restante
    private lateinit var countDownTimer: CountDownTimer // Objeto temporizador
    private var isTimerRunning = false // Estado del temporizador

    // 💡 AJUSTE 2: Declaramos el botón globalmente para acceder desde startTimer()
    private lateinit var btnPausePlay: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuidado_visual)

        // 1. Configurar el botón de retroceso
        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }

        // 2. Configurar el botón de Ayuda
        findViewById<View>(R.id.btn_help).setOnClickListener {
            Toast.makeText(this, "Información sobre ejercicios oculares", Toast.LENGTH_SHORT).show()
        }

        // 3. Lógica del temporizador
        // Ajuste 1 (Ya aplicado): Mostrar tiempo inicial sin arrancar el timer
        updateTimerText()

        setupTimerControls()
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

        // 💡 AJUSTE 2: Al iniciar el timer, cambiamos el icono a PAUSA visualmente
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
        // 💡 AJUSTE 2: Inicializamos la variable global
        btnPausePlay = findViewById(R.id.btn_pause_play)
        val btnRestart = findViewById<ImageButton>(R.id.btn_restart)

        // 💡 AJUSTE 2: Aseguramos que arranque visualmente con el icono de PLAY
        btnPausePlay.setImageResource(R.drawable.ic_play)

        // Lógica del botón PAUSA / PLAY
        btnPausePlay.setOnClickListener {
            if (isTimerRunning) {
                // Si está corriendo y tocamos, PAUSAMOS
                countDownTimer.cancel()
                isTimerRunning = false
                btnPausePlay.setImageResource(R.drawable.ic_play) // Cambiar icono a Play
            } else {
                // Si está pausado y tocamos, ARRANCAMOS
                startTimer()
                // (startTimer ya se encarga de poner el icono de Pausa)
            }
        }

        // Lógica del botón REINICIAR
        btnRestart.setOnClickListener {
            // Detener si está corriendo
            if (::countDownTimer.isInitialized) {
                countDownTimer.cancel()
            }

            timeLeftMS = TOTAL_TIME_MS // Restaurar el tiempo a 20s
            updateTimerText()
            isTimerRunning = false

            // 💡 AJUSTE 2 (Corrección): Al reiniciar, el icono debe volver a PLAY (listo para iniciar)
            btnPausePlay.setImageResource(R.drawable.ic_play)
        }
    }
}