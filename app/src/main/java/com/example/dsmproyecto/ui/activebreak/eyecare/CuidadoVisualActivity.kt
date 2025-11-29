package com.example.dsmproyecto.ui.activebreak.eyecare

import AyudaCuidadoVisualDialog
import ConfirmExitDialog
import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dsmproyecto.R
import com.example.dsmproyecto.ui.activebreak.scheduler.PausasStorage // Importar el Storage

class CuidadoVisualActivity : AppCompatActivity() {

    // TIEMPO TOTAL: 65 segundos (32s Horizontal + 32s Vertical + buffer)
    private val TOTAL_TIME_MS: Long = 65000
    // Cambio a la mitad (32s aprox)
    private val SWITCH_TIME_MS: Long = 32000

    private var timeLeftMS: Long = TOTAL_TIME_MS
    private lateinit var countDownTimer: CountDownTimer
    private var isTimerRunning = false

    private lateinit var btnPausePlay: ImageButton
    private lateinit var ivPupila: ImageView

    private var currentAnimator: ObjectAnimator? = null
    private var currentPhase = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuidado_visual)

        // 💡 LÓGICA DE ELIMINACIÓN AUTOMÁTICA
        // Verificamos si venimos de una notificación
        val notificationId = intent.getIntExtra("NOTIFICATION_ID_TO_DELETE", 0)
        if (notificationId != 0) {
            // Si hay un ID válido, borramos esta pausa de la lista de programadas
            PausasStorage.eliminarPausa(this, notificationId)
            // Opcional: Mostrar un pequeño mensaje o log
            // Toast.makeText(this, "Pausa iniciada desde notificación", Toast.LENGTH_SHORT).show()
        }

        ivPupila = findViewById(R.id.iv_pupila_movil)

        findViewById<View>(R.id.btn_back).setOnClickListener {
            showExitConfirmationDialog()
        }

        findViewById<View>(R.id.btn_help).setOnClickListener {
            val dialog = AyudaCuidadoVisualDialog()
            dialog.show(supportFragmentManager, "AyudaCuidadoVisual")
        }

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
     * Inicia la animación con velocidad constante usando Keyframes.
     * Ciclo: Centro -> Extremo A (3s) -> Pausa (2s) -> Extremo B (6s) -> Pausa (2s) -> Centro (3s)
     * Total Ciclo: 16 segundos.
     */
    private fun startEyeAnimation(phase: Int) {
        if (currentAnimator != null && currentAnimator!!.isPaused && currentPhase == phase) {
            currentAnimator?.resume()
            return
        }
        currentAnimator?.cancel()

        val range = 50f
        val propertyName = if (phase == 1) "translationX" else "translationY"

        // Aseguramos la otra propiedad en 0
        if (phase == 1) ivPupila.translationY = 0f else ivPupila.translationX = 0f

        val kf0 = Keyframe.ofFloat(0f, 0f)
        val kf1 = Keyframe.ofFloat(0.1875f, -range)
        val kf2 = Keyframe.ofFloat(0.3125f, -range)
        val kf3 = Keyframe.ofFloat(0.6875f, range)
        val kf4 = Keyframe.ofFloat(0.8125f, range)
        val kf5 = Keyframe.ofFloat(1f, 0f)

        val pvh = PropertyValuesHolder.ofKeyframe(propertyName, kf0, kf1, kf2, kf3, kf4, kf5)

        currentAnimator = ObjectAnimator.ofPropertyValuesHolder(ivPupila, pvh).apply {
            duration = 16000 // 16 segundos por ciclo completo
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
        currentPhase = phase
    }

    private fun stopEyeAnimation() {
        currentAnimator?.cancel()
        currentAnimator = null
        ivPupila.translationX = 0f
        ivPupila.translationY = 0f
    }

    private fun pauseEyeAnimation() {
        currentAnimator?.pause()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftMS, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeftMS = millisUntilFinished
                updateTimerText()

                // Cambio a la fase Vertical cuando quedan 32 segundos (aprox mitad)
                if (millisUntilFinished <= SWITCH_TIME_MS && currentPhase == 1) {
                    startEyeAnimation(2)
                }
            }

            override fun onFinish() {
                timeLeftMS = 0
                updateTimerText()
                isTimerRunning = false
                stopEyeAnimation()
                btnPausePlay.setImageResource(R.drawable.ic_play)
                Toast.makeText(this@CuidadoVisualActivity, "Rutina finalizada", Toast.LENGTH_LONG).show()
                currentPhase = 1
            }
        }.start()

        isTimerRunning = true
        btnPausePlay.setImageResource(R.drawable.ic_pause)

        if (timeLeftMS > SWITCH_TIME_MS) {
            startEyeAnimation(1)
        } else {
            startEyeAnimation(2)
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
        pauseEyeAnimation()
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
                if (timeLeftMS <= 0) {
                    timeLeftMS = TOTAL_TIME_MS
                    updateTimerText()
                    currentPhase = 1
                    stopEyeAnimation()
                }
                startTimer()
            }
        }

        btnRestart.setOnClickListener {
            pauseTimer()
            stopEyeAnimation()
            timeLeftMS = TOTAL_TIME_MS
            updateTimerText()
            currentPhase = 1
            btnPausePlay.setImageResource(R.drawable.ic_play)
        }
    }
}