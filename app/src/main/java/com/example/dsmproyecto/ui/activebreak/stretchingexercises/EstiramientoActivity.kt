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

    // Duración de la pausa: 20 segundos. Dividida en 10s por lado (500ms extra para iniciar en 20s)
    private val TOTAL_TIME_MS: Long = 20500
    private val SWITCH_TIME_MS: Long = 10000 // El cambio ocurre a los 10 segundos

    private var timeLeftMS: Long = TOTAL_TIME_MS
    private lateinit var countDownTimer: CountDownTimer
    private var isTimerRunning = false
    private lateinit var btnPausePlay: ImageButton

    // Vistas de Imagen para el estiramiento
    private lateinit var ivPasoIzquierdo: ImageView
    private lateinit var ivPasoDerecho: ImageView

    // Tiempo de referencia para saber cuándo cambiar el paso
    private var currentStep: Int = 1 // 1 = Izquierda (Paso 1), 2 = Derecha (Paso 2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estiramiento)

        // Inicializar vistas de estiramiento
        ivPasoIzquierdo = findViewById(R.id.iv_paso_izquierdo)
        ivPasoDerecho = findViewById(R.id.iv_paso_derecho)

        // 1. Configurar el botón de retroceso (usando el diálogo reutilizable)
        findViewById<View>(R.id.btn_back).setOnClickListener {
            showExitConfirmationDialog()
        }

        // 2. Configurar el botón de Ayuda
        findViewById<View>(R.id.btn_help).setOnClickListener {
            // CONEXIÓN DE AYUDA: Creamos y mostramos el dialogo local de ayuda
            val dialog = AyudaEstiramientoDialog()
            dialog.show(supportFragmentManager, "AyudaEstiramiento")
        }

        // 3. Configurar Temporizador y Controles
        updateTimerText()
        setupTimerControls()
    }

    /**
     * Captura el botón "Atrás" del sistema (físico o gesto).
     */
    override fun onBackPressed() {
        showExitConfirmationDialog()
    }

    /**
     * Muestra el diálogo de confirmación de salida y pausa el temporizador.
     */
    private fun showExitConfirmationDialog() {
        if (isTimerRunning) {
            pauseTimer()
        }
        // Llamamos a la clase local
        val dialog = ConfirmExitEstiramientoDialog()
        dialog.show(supportFragmentManager, "ConfirmExitEstiramiento")
    }

    /**
     * Alterna la imagen activa (borde turquesa) y la inactiva (borde gris).
     */
    private fun switchStep(step: Int) {
        if (step == 1) {
            // Paso 1: Activar Izquierda, Desactivar Derecha
            ivPasoIzquierdo.setBackgroundResource(R.drawable.bg_paso_activo)
            ivPasoDerecho.setBackgroundResource(R.drawable.bg_paso_inactivo)
            findViewById<TextView>(R.id.tv_instruccion).text = "Inclina tu torso hacia el lado izquierdo suavemente durante 10 segundos"

        } else if (step == 2) {
            // Paso 2: Desactivar Izquierda, Activar Derecha
            ivPasoIzquierdo.setBackgroundResource(R.drawable.bg_paso_inactivo)
            ivPasoDerecho.setBackgroundResource(R.drawable.bg_paso_activo)
            findViewById<TextView>(R.id.tv_instruccion).text = "Cambio. Inclina tu torso hacia el lado derecho suavemente durante 10 segundos"
        }
        currentStep = step
    }

    /**
     * Crea y arranca el temporizador de cuenta regresiva.
     */
    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftMS, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeftMS = millisUntilFinished
                updateTimerText()

                // Lógica de ROTACIÓN DE PASOS:
                // Si el tiempo restante es <= 10.5 segundos, cambiamos al Paso 2
                // La lógica actual asume que 20s = Paso 1 y 10s = Paso 2
                if (millisUntilFinished <= SWITCH_TIME_MS && currentStep == 1) {
                    switchStep(2)
                }
            }

            override fun onFinish() {
                timeLeftMS = 0
                updateTimerText()
                isTimerRunning = false
                btnPausePlay.setImageResource(R.drawable.ic_play)
                Toast.makeText(this@EstiramientoActivity, "Rutina de estiramiento finalizada", Toast.LENGTH_LONG).show()
                // Al finalizar, restablecemos la vista al Paso 1
                switchStep(1)
            }
        }.start()

        isTimerRunning = true
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
     * Pausa el temporizador y actualiza el icono a Play.
     */
    private fun pauseTimer() {
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        isTimerRunning = false
        btnPausePlay.setImageResource(R.drawable.ic_play)
    }

    /**
     * Inicializa los controles de la interfaz (Play/Pausa/Reiniciar).
     */
    private fun setupTimerControls() {
        btnPausePlay = findViewById(R.id.btn_pause_play)
        val btnRestart = findViewById<ImageButton>(R.id.btn_restart)

        // Inicializamos la UI y el estado de rotación al Paso 1 (Play Inactivo)
        btnPausePlay.setImageResource(R.drawable.ic_play)
        switchStep(1) // Asegura que el Paso 1 esté activo y se muestre la instrucción correcta

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
            // Al reiniciar, volvemos al Paso 1
            switchStep(1)
            btnPausePlay.setImageResource(R.drawable.ic_play)
        }
    }
}