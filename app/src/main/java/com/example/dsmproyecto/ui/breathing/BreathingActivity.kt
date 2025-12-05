package com.example.dsmproyecto.ui.breathing

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.example.dsmproyecto.R
import com.example.dsmproyecto.databinding.ActivityBreathingBinding
import android.widget.ArrayAdapter
import android.view.View
import android.widget.AdapterView

class BreathingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBreathingBinding
    private var isPlaying = false
    private var timeLeftInMillis = 60000L
    private var totalTimeInMillis = 60000L
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
        binding.progressTimer.progress = 0
    }

    private fun setupListeners() {

        binding.btnBack.setOnClickListener { finish() }

        binding.btnHelp.setOnClickListener { showHelpDialog() }

        binding.btnPlayPause.setOnClickListener {
            if (isPlaying) pauseExercise() else startExercise()
        }

        // Cambiar tiempo desde el spinner
        binding.spnTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val minutes = binding.spnTime.selectedItem.toString().split(" ")[0].toInt()

                timeLeftInMillis = minutes * 60_000L
                totalTimeInMillis = timeLeftInMillis
                binding.progressTimer.progress = 0

                updateTimerText()

                if (isPlaying) pauseExercise()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun startExercise() {

        // Leer minutos del spinner
        val minutes = binding.spnTime.selectedItem.toString().split(" ")[0].toInt()

        timeLeftInMillis = minutes * 60_000L
        totalTimeInMillis = timeLeftInMillis
        binding.progressTimer.progress = 0

        isPlaying = true
        binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
        binding.tvStatus.text = getString(R.string.breathe)

        binding.breathingView.startBreathing()
        startTimer()
    }

    private fun pauseExercise() {
        isPlaying = false
        binding.btnPlayPause.setImageResource(R.drawable.ic_play)
        binding.tvStatus.text = getString(R.string.paused)

        binding.breathingView.pauseBreathing()
        countDownTimer?.cancel()
    }

    private fun startTimer() {
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText()

                val progress =
                    (((totalTimeInMillis - timeLeftInMillis).toFloat() / totalTimeInMillis) * 100).toInt()

                binding.progressTimer.progress = progress
            }

            override fun onFinish() {
                timeLeftInMillis = 0
                updateTimerText()
                binding.progressTimer.progress = 100

                finishExercise()
            }
        }.start()
    }

    private fun finishExercise() {
        isPlaying = false
        binding.btnPlayPause.setImageResource(R.drawable.ic_play)
        binding.tvStatus.text = getString(R.string.exercise_complete)

        binding.breathingView.stopBreathing()
    }

    private fun updateTimerText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        binding.tvTimer.text = String.format("%d:%02d", minutes, seconds)
    }

    private fun showHelpDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(R.string.help)
            .setMessage(R.string.breathing_help_message)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    override fun onPause() {
        super.onPause()
        if (isPlaying) pauseExercise()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        binding.breathingView.stopBreathing()
    }
}
