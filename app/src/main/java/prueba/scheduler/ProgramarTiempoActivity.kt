package com.example.dsmproyecto.ui.activebreak.scheduler

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dsmproyecto.R

class ProgramarTiempoActivity : AppCompatActivity() {

    private var selectedTimeMinutes: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programar_tiempo)

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        val radioGroup = findViewById<RadioGroup>(R.id.rg_time_selection)
        val btnNext = findViewById<Button>(R.id.btn_next)

        // Listener para guardar la selección
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_1_min -> selectedTimeMinutes = 1
                R.id.rb_3_min -> selectedTimeMinutes = 3
                R.id.rb_5_min -> selectedTimeMinutes = 5
            }
        }

        btnNext.setOnClickListener {
            if (selectedTimeMinutes == 0) {
                Toast.makeText(this, "Por favor selecciona un tiempo", Toast.LENGTH_SHORT).show()
            } else {
                // Pasamos a la siguiente pantalla enviando el tiempo seleccionado
                val intent = Intent(this, ProgramarTipoPausaActivity::class.java)
                intent.putExtra("EXTRA_TIME_MINUTES", selectedTimeMinutes)
                startActivity(intent)
            }
        }
    }
}