package com.example.dsmproyecto.ui.activebreak.eyecare

import AyudaCuidadoVisualDialog
import ConfirmExitDialog
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dsmproyecto.R
// Asegúrate de importar tus diálogos si están en el mismo paquete o ajusta la importación

class CuidadoVisualActivity : AppCompatActivity() {

    private val TOTAL_TIME_MS: Long = 20500
    // Punto de cambio: a los 10 segundos cambiamos de horizontal a vertical
    private val SWITCH_TIME_MS: Long = 10000

    private var timeLeftMS: Long = TOTAL_TIME_MS
    private lateinit var countDownTimer: CountDownTimer
    private var isTimerRunning = false

    // Referencias de UI
    private lateinit var btnPausePlay: ImageButton
    private lateinit var ivPupila: ImageView // La parte móvil del ojo

    // Variable para controlar la animación
    private var currentAnimator: ObjectAnimator? = null
    private var currentPhase = 1 // 1 = Horizontal, 2 = Vertical

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuidado_visual)

        // Inicializar vistas
        ivPupila = findViewById(R.id.iv_pupila_movil) // Asegúrate que este ID exista en tu XML nuevo

        // 1. Configurar el botón de retroceso
        findViewById<View>(R.id.btn_back).setOnClickListener {
            showExitConfirmationDialog()
        }

        // 2. Configurar el botón de Ayuda
        findViewById<View>(R.id.btn_help).setOnClickListener {
            val dialog = AyudaCuidadoVisualDialog()
            dialog.show(supportFragmentManager, "AyudaCuidadoVisual")
        }

        // 3. Lógica inicial
        updateTimerText()
        setupTimerControls()
    }

    override fun onBackPressed() {
        showExitConfirmationDialog()
    }

    private fun showExitConfirmationDialog() {
        if (isTimerRunning) {
            pauseTimer()
        }
        val dialog = ConfirmExitDialog()
        dialog.show(supportFragmentManager, "ConfirmExit")
    }

    /**
     * Inicia la animación de la pupila según la fase actual (1=Horizontal, 2=Vertical).
     */
    private fun startEyeAnimation(phase: Int) {
        // Si ya hay una animación corriendo, la cancelamos para empezar la nueva
        currentAnimator?.cancel()

        // Rango de movimiento en píxeles (ajusta según el tamaño de tu ojo)
        val range = 50f

        if (phase == 1) {
            // FASE 1: Movimiento Horizontal (Izquierda <-> Derecha)
            // Mueve translationX desde 0 a -range, luego a +range
            currentAnimator = ObjectAnimator.ofFloat(ivPupila, "translationX", 0f, -range, range, 0f).apply {
                duration = 2000 // 2 segundos por ciclo completo
                repeatCount = ValueAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator() // Movimiento suave
                start()
            }
        } else {
            // FASE 2: Movimiento Vertical (Arriba <-> Abajo)
            // Aseguramos que X esté en 0 antes de empezar Y
            ivPupila.translationX = 0f

            currentAnimator = ObjectAnimator.ofFloat(ivPupila, "translationY", 0f, -range, range, 0f).apply {
                duration = 2000
                repeatCount = ValueAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        }
        currentPhase = phase
    }

    /**
     * Detiene la animación y centra la pupila.
     */
    private fun stopEyeAnimation() {
        currentAnimator?.cancel()
        currentAnimator = null
        // Reseteamos posición al centro
        ivPupila.translationX = 0f
        ivPupila.translationY = 0f
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftMS, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeftMS = millisUntilFinished
                updateTimerText()

                // Lógica de cambio de fase de animación
                if (millisUntilFinished <= SWITCH_TIME_MS && currentPhase == 1) {
                    startEyeAnimation(2) // Cambiar a Vertical
                }
            }

            override fun onFinish() {
                timeLeftMS = 0
                updateTimerText()
                isTimerRunning = false

                stopEyeAnimation() // Detener ojos
                btnPausePlay.setImageResource(R.drawable.ic_play)

                Toast.makeText(this@CuidadoVisualActivity, "Rutina finalizada", Toast.LENGTH_LONG).show()
                currentPhase = 1 // Resetear fase para la próxima vez
            }
        }.start()

        isTimerRunning = true
        btnPausePlay.setImageResource(R.drawable.ic_pause)

        // Iniciar la animación correspondiente al tiempo actual
        if (timeLeftMS > SWITCH_TIME_MS) {
            startEyeAnimation(1) // Horizontal
        } else {
            startEyeAnimation(2) // Vertical
        }
    }

    private fun updateTimerText() {
        val seconds = (timeLeftMS / 1000) % 60
        val minutes = (timeLeftMS / 1000) / 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        findViewById<TextView>(R.id.tv_timer).text = timeFormatted
    }

    private fun pauseTimer() {
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        isTimerRunning = false
        stopEyeAnimation() // Detenemos la animación visualmente
        btnPausePlay.setImageResource(R.drawable.ic_play)
    }

    private fun setupTimerControls() {
        btnPausePlay = findViewById(R.id.btn_pause_play)
        val btnRestart = findViewById<ImageButton>(R.id.btn_restart)

        btnPausePlay.setImageResource(R.drawable.ic_play)

        btnPausePlay.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        btnRestart.setOnClickListener {
            pauseTimer()

            timeLeftMS = TOTAL_TIME_MS
            updateTimerText()
            currentPhase = 1 // Volver a fase 1

            // Asegurar que la pupila esté centrada
            ivPupila.translationX = 0f
            ivPupila.translationY = 0f

            btnPausePlay.setImageResource(R.drawable.ic_play)
        }
    }
}