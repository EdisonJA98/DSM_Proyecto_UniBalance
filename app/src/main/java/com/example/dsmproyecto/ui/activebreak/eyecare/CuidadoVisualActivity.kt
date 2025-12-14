package com.example.dsmproyecto.ui.activebreak.eyecare

import AyudaCuidadoVisualDialog
import ConfirmExitDialog
import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dsmproyecto.R
// Asegúrate de importar tus diálogos si están en el mismo paquete

class CuidadoVisualActivity : AppCompatActivity() {

    private val TOTAL_TIME_MS: Long = 40500
    private val SWITCH_TIME_MS: Long = 20000

    private var timeLeftMS: Long = TOTAL_TIME_MS
    private lateinit var countDownTimer: CountDownTimer
    private var isTimerRunning = false

    private lateinit var btnPausePlay: ImageButton
    private lateinit var ivPupila: ImageView

    private var currentAnimator: ObjectAnimator? = null
    private var currentPhase = 1

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuidado_visual)

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

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
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
     * Función genérica para reproducir cualquier recurso de audio en res/raw.
     */
    private fun playAudioResource(resId: Int) {
        // Liberar audio anterior si existe
        releaseMediaPlayer()

        try {
            mediaPlayer = MediaPlayer.create(this, resId)
            mediaPlayer?.setOnCompletionListener {
                // Opcional: lógica al terminar audio
            }
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun releaseMediaPlayer() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startEyeAnimation(phase: Int) {
        // Lógica de RESUMIR (Animación y Audio)
        if (currentAnimator != null && currentAnimator!!.isPaused && currentPhase == phase) {
            currentAnimator?.resume()

            // Reanudar audio solo si no ha terminado
            if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
                // Verificar posición para evitar errores
                try {
                    if (mediaPlayer!!.currentPosition < mediaPlayer!!.duration) {
                        mediaPlayer?.start()
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
            return
        }

        // Lógica de INICIO NUEVO
        currentAnimator?.cancel()

        // Reproducir audio de instrucción según fase
        val audioResId = if (phase == 1) R.raw.pa_visual_part1_movhorizontal else R.raw.pa_visual_part2_movvertical
        playAudioResource(audioResId)

        val range = 50f
        val propertyName = if (phase == 1) "translationX" else "translationY"

        if (phase == 1) ivPupila.translationY = 0f else ivPupila.translationX = 0f

        val kf0 = Keyframe.ofFloat(0f, 0f)
        val kf1 = Keyframe.ofFloat(0.1875f, -range)
        val kf2 = Keyframe.ofFloat(0.3125f, -range)
        val kf3 = Keyframe.ofFloat(0.6875f, range)
        val kf4 = Keyframe.ofFloat(0.8125f, range)
        val kf5 = Keyframe.ofFloat(1f, 0f)

        val pvh = PropertyValuesHolder.ofKeyframe(propertyName, kf0, kf1, kf2, kf3, kf4, kf5)

        currentAnimator = ObjectAnimator.ofPropertyValuesHolder(ivPupila, pvh).apply {
            duration = 16000
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

        // Detener audio de instrucción si está sonando
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
        }
    }

    private fun pauseEyeAnimation() {
        currentAnimator?.pause()
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftMS, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeftMS = millisUntilFinished
                updateTimerText()

                // CAMBIO DE FASE a los 20 segundos
                if (millisUntilFinished <= SWITCH_TIME_MS && currentPhase == 1) {
                    startEyeAnimation(2) // Esto iniciará el segundo audio de instrucción
                }
            }

            override fun onFinish() {
                timeLeftMS = 0
                updateTimerText()
                isTimerRunning = false

                // Detenemos la animación y el audio de instrucción actual
                stopEyeAnimation()

                // 💡 REPRODUCIR AUDIO FINALIZACIÓN
                playAudioResource(R.raw.pa_visual_finalizacion)

                btnPausePlay.setImageResource(R.drawable.ic_play)

                Toast.makeText(this@CuidadoVisualActivity, "Rutina finalizada", Toast.LENGTH_LONG).show()
                currentPhase = 1
            }
        }.start()

        isTimerRunning = true
        btnPausePlay.setImageResource(R.drawable.ic_pause)

        // Iniciar la animación y audio correspondiente
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
                    releaseMediaPlayer()
                }
                startTimer()
            }
        }

        btnRestart.setOnClickListener {
            pauseTimer()
            stopEyeAnimation()
            releaseMediaPlayer()

            timeLeftMS = TOTAL_TIME_MS
            updateTimerText()
            currentPhase = 1

            btnPausePlay.setImageResource(R.drawable.ic_play)
        }
    }
}