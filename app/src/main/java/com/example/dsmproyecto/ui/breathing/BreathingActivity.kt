package com.example.dsmproyecto.ui.breathing


import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.example.dsmproyecto.R
import com.example.dsmproyecto.databinding.ActivityBreathingBinding
import android.widget.ArrayAdapter

class BreathingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBreathingBinding
    private var isPlaying = false
    private var currentRound = 1
    private val maxRounds = 5
    private var timeLeftInMillis = 60000L // 1 minuto
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBreathingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.breathing_values,
            R.layout.item_spinner_time
        )
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        binding.spnTime.adapter = adapter
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        updateTimerText()
    }

    private fun setupListeners() {
        // Botón atrás
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Botón ayuda
        binding.btnHelp.setOnClickListener {
            // Mostrar diálogo de ayuda
            showHelpDialog()
        }

        // Botón play/pause
        binding.btnPlayPause.setOnClickListener {
            if (isPlaying) {
                pauseExercise()
            } else {
                startExercise()
            }
        }
    }

    private fun startExercise() {
        // Obtener tiempo desde el spinner
        val selectedTimeText = binding.spnTime.selectedItem.toString()
        val minutes = selectedTimeText.split(" ")[0].toInt()
        timeLeftInMillis = minutes * 60_000L

        isPlaying = true
        binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
        binding.tvStatus.text = getString(R.string.breathe)

        // Animación
        binding.breathingView.startBreathing()

        // Iniciar timer
        startTimer()
    }

    private fun pauseExercise() {
        isPlaying = false
        binding.btnPlayPause.setImageResource(R.drawable.ic_play)
        binding.tvStatus.text = getString(R.string.paused)

        // Pausar animación
        binding.breathingView.pauseBreathing()

        // Pausar timer
        countDownTimer?.cancel()
    }

    private fun startTimer() {
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText()
            }

            override fun onFinish() {
                timeLeftInMillis = 0
                updateTimerText()
                completeRound()
            }
        }.start()
    }

    private fun completeRound() {
        if (currentRound < maxRounds) {
            currentRound++
            val selectedTimeText = binding.spnTime.selectedItem.toString()
            val minutes = selectedTimeText.split(" ")[0].toInt()
            timeLeftInMillis = minutes * 60_000L
            updateTimerText()
            startTimer()
        } else {
            finishExercise()
        }
    }

    private fun finishExercise() {
        isPlaying = false
        binding.btnPlayPause.setImageResource(R.drawable.ic_play)
        binding.tvStatus.text = getString(R.string.exercise_complete)
        binding.breathingView.stopBreathing()

        // Mostrar diálogo de completado o navegar a otra pantalla
    }

    private fun updateTimerText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        binding.tvTimer.text = String.format("%d:%02d", minutes, seconds)
    }

    private fun showHelpDialog() {
        // Implementar diálogo de ayuda
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(R.string.help)
            .setMessage(R.string.breathing_help_message)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    override fun onPause() {
        super.onPause()
        if (isPlaying) {
            pauseExercise()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        binding.breathingView.stopBreathing()
    }
}
