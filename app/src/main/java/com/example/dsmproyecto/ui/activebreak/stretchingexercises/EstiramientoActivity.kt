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
import com.example.dsmproyecto.ui.activebreak.scheduler.PausasStorage // Importar el Storage

class EstiramientoActivity : AppCompatActivity() {

    private val TOTAL_TIME_MS: Long = 20500
    private val SWITCH_TIME_MS: Long = 10000

    private var timeLeftMS: Long = TOTAL_TIME_MS
    private lateinit var countDownTimer: CountDownTimer
    private var isTimerRunning = false

    // Referencias de UI
    private lateinit var btnPausePlay: ImageButton
    private lateinit var tvInstruccion: TextView
    private lateinit var ivPasoIzquierdo: ImageView
    private lateinit var ivPasoDerecho: ImageView

    private var currentStep: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estiramiento)

        // 💡 LÓGICA DE ELIMINACIÓN AUTOMÁTICA
        val notificationId = intent.getIntExtra("NOTIFICATION_ID_TO_DELETE", 0)
        if (notificationId != 0) {
            PausasStorage.eliminarPausa(this, notificationId)
        }

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

        // Mensaje inicial
        tvInstruccion.text = "Pulsa play para comenzar"
    }

    override fun onBackPressed() {
        showExitConfirmationDialog()
    }

    private fun showExitConfirmationDialog() {
        if (isTimerRunning) {
            pauseTimer()
        }
        val dialog = ConfirmExitEstiramientoDialog()
        dialog.show(supportFragmentManager, "ConfirmExitEstiramiento")
    }

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

    private fun updateInstructionWithTime() {
        val secondsRemainingInStep = if (currentStep == 1) {
            (timeLeftMS - SWITCH_TIME_MS) / 1000
        } else {
            timeLeftMS / 1000
        }

        val secondsDisplay = if (secondsRemainingInStep < 0) 0 else secondsRemainingInStep

        if (currentStep == 1) {
            tvInstruccion.text = "Inclina tu torso hacia el lado izquierdo suavemente durante ${secondsDisplay}s"
        } else {
            tvInstruccion.text = "Cambio. Inclina tu torso hacia el lado derecho suavemente durante ${secondsDisplay}s"
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftMS, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeftMS = millisUntilFinished
                updateTimerText()

                // Lógica de cambio de paso: Si quedan 10s o menos, pasamos al paso 2
                if (millisUntilFinished <= SWITCH_TIME_MS && currentStep == 1) {
                    switchStep(2)
                }

                updateInstructionWithTime()
            }

            override fun onFinish() {
                timeLeftMS = 0
                updateTimerText()
                isTimerRunning = false
                btnPausePlay.setImageResource(R.drawable.ic_play)

                tvInstruccion.text = "Rutina finalizada. ¡Bien hecho!"
                Toast.makeText(this@EstiramientoActivity, "Rutina finalizada", Toast.LENGTH_LONG).show()

                switchStep(1)
            }
        }.start()

        isTimerRunning = true
        btnPausePlay.setImageResource(R.drawable.ic_pause)

        updateInstructionWithTime()
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
        btnPausePlay.setImageResource(R.drawable.ic_play)

        tvInstruccion.text = "Entrenamiento pausado, pulse play para continuar"
    }

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
                if (timeLeftMS <= 0) {
                    timeLeftMS = TOTAL_TIME_MS
                    updateTimerText()
                    currentStep = 1
                    switchStep(1)
                }
                startTimer()
            }
        }

        // Botón REINICIAR
        btnRestart.setOnClickListener {
            pauseTimer()

            timeLeftMS = TOTAL_TIME_MS
            updateTimerText()
            switchStep(1)
            btnPausePlay.setImageResource(R.drawable.ic_play)

            tvInstruccion.text = "Pulsa play para comenzar"
        }
    }
}