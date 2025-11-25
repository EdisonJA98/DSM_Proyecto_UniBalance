package com.example.dsmproyecto.ui.activebreak.stretchingexercises

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dsmproyecto.R


class EstiramientoActivity : AppCompatActivity() {

    // Duración total de la pausa: 20 segundos (+500ms de buffer para visualización)
    private val TOTAL_TIME_MS: Long = 20500
    // Punto de cambio de ejercicio: a los 10 segundos
    private val SWITCH_TIME_MS: Long = 10000

    private var timeLeftMS: Long = TOTAL_TIME_MS
    private lateinit var countDownTimer: CountDownTimer
    private var isTimerRunning = false

    // Referencias de UI
    private lateinit var btnPausePlay: ImageButton
    private lateinit var tvInstruccion: TextView
    private lateinit var ivPasoIzquierdo: ImageView
    private lateinit var ivPasoDerecho: ImageView

    // Estado actual: 1 = Izquierda (Paso 1), 2 = Derecha (Paso 2)
    private var currentStep: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estiramiento)

        // Inicializar vistas
        ivPasoIzquierdo = findViewById(R.id.iv_paso_izquierdo)
        ivPasoDerecho = findViewById(R.id.iv_paso_derecho)
        tvInstruccion = findViewById(R.id.tv_instruccion)

        // 1. Configurar el botón de retroceso (Back Button)
        findViewById<View>(R.id.btn_back).setOnClickListener {
            showExitConfirmationDialog()
        }

        // 2. Configurar el botón de Ayuda
        findViewById<View>(R.id.btn_help).setOnClickListener {
            val dialog = AyudaEstiramientoDialog()
            dialog.show(supportFragmentManager, "AyudaEstiramiento")
        }

        // 3. Configurar Temporizador y Controles iniciales
        updateTimerText()
        setupTimerControls()

        // 💡 MEJORA 1: Mensaje inicial claro
        tvInstruccion.text = "Pulsa play para comenzar"
    }

    /**
     * Captura el botón "Atrás" del sistema (físico o gesto).
     */
    override fun onBackPressed() {
        showExitConfirmationDialog()
    }

    /**
     * Muestra el diálogo de confirmación de salida y pausa el temporizador si está corriendo.
     */
    private fun showExitConfirmationDialog() {
        if (isTimerRunning) {
            pauseTimer()
        }
        val dialog = ConfirmExitEstiramientoDialog()
        dialog.show(supportFragmentManager, "ConfirmExitEstiramiento")
    }

    /**
     * Cambia visualmente el paso activo (borde turquesa vs borde gris).
     */
    private fun switchStep(step: Int) {
        currentStep = step
        if (step == 1) {
            // Paso 1 Activo (Izquierda)
            ivPasoIzquierdo.setBackgroundResource(R.drawable.bg_paso_activo)
            ivPasoDerecho.setBackgroundResource(R.drawable.bg_paso_inactivo)
        } else if (step == 2) {
            // Paso 2 Activo (Derecha)
            ivPasoIzquierdo.setBackgroundResource(R.drawable.bg_paso_inactivo)
            ivPasoDerecho.setBackgroundResource(R.drawable.bg_paso_activo)
        }
    }

    /**
     * 💡 MEJORA 3: Actualiza el texto con cuenta regresiva dinámica.
     * Se llama cada segundo dentro del temporizador.
     */
    private fun updateInstructionWithTime() {
        // Calculamos segundos restantes para el paso actual
        val secondsRemainingInStep = if (currentStep == 1) {
            // Si estamos en el paso 1 (primeros 10s), restamos el tiempo de cambio
            (timeLeftMS - SWITCH_TIME_MS) / 1000
        } else {
            // Si estamos en el paso 2 (últimos 10s), es el tiempo restante total
            timeLeftMS / 1000
        }

        // Evitamos números negativos visuales
        val secondsDisplay = if (secondsRemainingInStep < 0) 0 else secondsRemainingInStep

        if (currentStep == 1) {
            tvInstruccion.text = "Inclina tu torso hacia el lado izquierdo suavemente durante ${secondsDisplay}s"
        } else {
            tvInstruccion.text = "Cambio. Inclina tu torso hacia el lado derecho suavemente durante ${secondsDisplay}s"
        }
    }

    /**
     * Inicia el temporizador.
     */
    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftMS, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeftMS = millisUntilFinished
                updateTimerText()

                // Lógica de cambio de paso: Si quedan 10s o menos, pasamos al paso 2
                if (millisUntilFinished <= SWITCH_TIME_MS && currentStep == 1) {
                    switchStep(2)
                }

                // 💡 Actualizar texto dinámico cada segundo
                updateInstructionWithTime()
            }

            override fun onFinish() {
                timeLeftMS = 0
                updateTimerText()
                isTimerRunning = false
                btnPausePlay.setImageResource(R.drawable.ic_play)

                tvInstruccion.text = "Rutina finalizada. ¡Bien hecho!"
                Toast.makeText(this@EstiramientoActivity, "Rutina finalizada", Toast.LENGTH_LONG).show()

                // Reiniciamos visualmente al paso 1
                switchStep(1)
            }
        }.start()

        isTimerRunning = true
        btnPausePlay.setImageResource(R.drawable.ic_pause)

        // Actualización inmediata del texto al dar Play
        updateInstructionWithTime()
    }

    /**
     * Formatea y muestra el tiempo en el reloj digital (00:XX).
     */
    private fun updateTimerText() {
        val seconds = (timeLeftMS / 1000) % 60
        val minutes = (timeLeftMS / 1000) / 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        findViewById<TextView>(R.id.tv_timer).text = timeFormatted
    }

    /**
     * Pausa el temporizador y muestra el mensaje de pausa.
     */
    private fun pauseTimer() {
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        isTimerRunning = false
        btnPausePlay.setImageResource(R.drawable.ic_play)

        // 💡 MEJORA 2: Mensaje de retroalimentación de pausa
        tvInstruccion.text = "Entrenamiento pausado, pulse play para continuar"
    }

    /**
     * Configura los listeners de los botones de control.
     */
    private fun setupTimerControls() {
        btnPausePlay = findViewById(R.id.btn_pause_play)
        val btnRestart = findViewById<ImageButton>(R.id.btn_restart)

        // Estado inicial
        btnPausePlay.setImageResource(R.drawable.ic_play)
        switchStep(1)

        // Botón PAUSA / PLAY
        btnPausePlay.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        // Botón REINICIAR
        btnRestart.setOnClickListener {
            pauseTimer() // Detiene y pone mensaje de pausa (que sobreescribiremos abajo)

            timeLeftMS = TOTAL_TIME_MS
            updateTimerText()
            switchStep(1)
            btnPausePlay.setImageResource(R.drawable.ic_play)

            // 💡 Restaurar mensaje inicial al reiniciar
            tvInstruccion.text = "Pulsa play para comenzar"
        }
    }
}