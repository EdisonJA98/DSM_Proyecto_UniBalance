package com.example.dsmproyecto.ui.emotions

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import android.widget.Toast
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.dsmproyecto.R

class EstadisticasActivity : AppCompatActivity() {

    // Simulación de Historial de Emociones (idealmente vendría de Firestore o DB)
    // El 'score' simula una escala de 1 a 5 para el gráfico: 5=Muy Feliz, 1=Molesto
    private val EMOTION_HISTORY = listOf(
        EmotionEntry("2025-11-01", "Muy feliz", "😄", 5),
        EmotionEntry("2025-11-02", "Feliz", "🙂", 4),
        EmotionEntry("2025-11-03", "Neutral", "😐", 3),
        EmotionEntry("2025-11-04", "Preocupado", "😟", 2),
        EmotionEntry("2025-11-05", "Molesto", "😡", 1),
        EmotionEntry("2025-11-06", "Feliz", "🙂", 4),
        EmotionEntry("2025-11-07", "Muy feliz", "😄", 5)
    )

    private lateinit var btnFilter7Days: Button
    private lateinit var btnFilter30Days: Button
    private lateinit var btnFilterAll: Button
    private lateinit var detailHistoryContainer: LinearLayout
    private lateinit var tvChartSummary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas_emocionales)
        supportActionBar?.hide()

        setupViews()
        applyFilter(7) // Mostrar 7 días por defecto al inicio
    }

    private fun setupViews() {
        // Enlace al botón de retroceso
        findViewById<ImageButton>(R.id.btn_back_stats).setOnClickListener {
            finish()
        }

        btnFilter7Days = findViewById(R.id.btn_filter_7days)
        btnFilter30Days = findViewById(R.id.btn_filter_30days)
        btnFilterAll = findViewById(R.id.btn_filter_all)
        detailHistoryContainer = findViewById(R.id.detail_history_container)
        tvChartSummary = findViewById(R.id.tv_chart_summary)

        // Asignar listeners a los filtros
        btnFilter7Days.setOnClickListener { applyFilter(7) }
        btnFilter30Days.setOnClickListener { applyFilter(30) }
        btnFilterAll.setOnClickListener { applyFilter(999) }
    }

    private fun applyFilter(days: Int) {
        val filteredHistory = if (days == 999) {
            EMOTION_HISTORY
        } else {
            // Simulación: toma los últimos 'days' registros.
            EMOTION_HISTORY.takeLast(days)
        }

        updateFilterButtons(days)
        renderChart(filteredHistory)
        renderDetailedHistory(filteredHistory)
    }

    private fun updateFilterButtons(activeDays: Int) {
        fun styleButton(button: Button, isActive: Boolean) {
            val colorRes = if (isActive) R.color.color_primary_emotion else R.color.gray_subtitle
            button.setBackgroundColor(ContextCompat.getColor(this, colorRes))
            button.setTextColor(ContextCompat.getColor(this, R.color.white))
        }

        styleButton(btnFilter7Days, activeDays == 7)
        styleButton(btnFilter30Days, activeDays == 30)
        styleButton(btnFilterAll, activeDays == 999)
    }

    private fun renderChart(history: List<EmotionEntry>) {
        if (history.isEmpty()) {
            tvChartSummary.text = "No hay datos de emoción para mostrar en este período."
            return
        }

        // Actualizar el Resumen de texto
        val totalScore = history.sumOf { it.score }
        val averageScore = if (history.isNotEmpty()) totalScore.toDouble() / history.size else 0.0
        val maxEntry = history.maxByOrNull { it.score }

        val summaryText = """
            Tendencia en ${history.size} días (del ${history.first().date} al ${history.last().date}):
            Puntuación Media: ${String.format("%.1f/5.0", averageScore)}
            Día más Positivo: ${maxEntry?.label} ${maxEntry?.icon}
        """.trimIndent()

        tvChartSummary.text = summaryText
    }

    private fun renderDetailedHistory(history: List<EmotionEntry>) {
        // Limpiar vistas anteriores
        detailHistoryContainer.removeAllViews()

        if (history.isEmpty()) {
            val tv = TextView(this)
            tv.text = "No hay registros en este período."
            detailHistoryContainer.addView(tv)
            return
        }

        // Crear una vista TextView por cada registro (el más reciente primero)
        history.reversed().forEach { entry ->
            val tv = TextView(this).apply {
                text = "${entry.date} • ${entry.icon} ${entry.label}"
                textSize = 16f
                setTextColor(ContextCompat.getColor(context, R.color.black))
                setPadding(0, 8, 0, 8)
            }
            detailHistoryContainer.addView(tv)
        }
    }

    // Clase de datos para el historial
    data class EmotionEntry(val date: String, val label: String, val icon: String, val score: Int)
}