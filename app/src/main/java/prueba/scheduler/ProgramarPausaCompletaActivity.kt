package com.example.dsmproyecto.ui.activebreak.scheduler

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dsmproyecto.ui.MainActivity
import com.example.dsmproyecto.R

class ProgramarPausaCompletaActivity : AppCompatActivity() {

    private lateinit var etTiempo: EditText
    private lateinit var spinnerPausas: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programar_pausa_completa)

        // Referencias
        etTiempo = findViewById(R.id.et_tiempo_minutos)
        spinnerPausas = findViewById(R.id.spinner_pausas)

        // Configurar Botones Superiores
        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<View>(R.id.btn_help).setOnClickListener {
            val dialog = AyudaProgramacionDialog()
            dialog.show(supportFragmentManager, "AyudaProgramacion")
        }

        // Configurar Spinner (Lista Desplegable)
        setupSpinner()

        // Botón Confirmar
        findViewById<Button>(R.id.btn_confirmar).setOnClickListener {
            validarYProgramar()
        }
    }

    private fun setupSpinner() {
        // Opciones de la lista
        val opciones = listOf("Ejercicios de estiramiento", "Descanso visual", "Decidir después")

        // Adaptador para mostrar las opciones
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerPausas.adapter = adapter
    }

    private fun validarYProgramar() {
        val tiempoTexto = etTiempo.text.toString()

        if (tiempoTexto.isEmpty()) {
            etTiempo.error = "Ingresa el tiempo"
            return
        }

        val minutos = tiempoTexto.toIntOrNull()

        if (minutos == null || minutos < 1) {
            etTiempo.error = "Mínimo 1 minuto"
            return
        }

        val tipoSeleccionado = spinnerPausas.selectedItem.toString()

        // Lógica de programación (Simulada por ahora)
        programarNotificacion(minutos, tipoSeleccionado)
    }

    private fun programarNotificacion(minutos: Int, tipo: String) {
        // AQUÍ IRÁ LA LÓGICA DE ALARMMANAGER / WORKMANAGER

        Toast.makeText(this, "Programado: $tipo en $minutos min", Toast.LENGTH_LONG).show()

        // Volver al menú principal limpiando la pila
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}