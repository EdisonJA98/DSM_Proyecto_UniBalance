package com.example.dsmproyecto.ui.emotions

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.dsmproyecto.R

class RegistroEmocionalActivity : AppCompatActivity() {

    // --- Definiciones de datos (Adaptadas de la estructura de React Native) ---
    private val EMOJIS = listOf(
        Emoji("😄", "Muy feliz", "very_happy"),
        Emoji("🙂", "Feliz", "happy"),
        Emoji("😐", "Neutral", "neutral"),
        Emoji("😟", "Preocupado", "worried"),
        Emoji("😡", "Molesto", "angry"),
        Emoji("😴", "Cansado", "tired")
    )

    // Diccionario para el texto de retroalimentación
    private val FEEDBACK_TEXT = mapOf(
        "very_happy" to "¡Genial! Sigue así.",
        "happy" to "¡Qué bien! Disfruta tu día.",
        "neutral" to "Está bien sentirse así.",
        "worried" to "Respira hondo, estamos contigo.",
        "angry" to "Tómate un momento para calmarte.",
        "tired" to "Descansa un poco si puedes."
    )

    // Lista de recursos de apoyo
    private val RESOURCES = listOf(
        Resource("📘", "Mini-guía de relajación"),
        Resource("🎧", "Playlist calma"),
        Resource("📝", "Artículo paso a paso")
    )

    // --- Estado de la Activity: almacena la clave del emoji seleccionado ---
    private var selectedKey: String? = null

    // --- Referencias a Vistas (Para acceder a los elementos del XML) ---
    private lateinit var gridEmociones: androidx.gridlayout.widget.GridLayout
    private lateinit var cardFeedback: CardView
    private lateinit var tvFeedbackEmoji: TextView
    private lateinit var tvFeedbackText: TextView
    private lateinit var btnRegister: Button
    private lateinit var btnResources: Button
    private lateinit var btnHelp: ImageButton
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Establece el layout principal de la Activity (activity_registro_emocional.xml)
        setContentView(R.layout.activity_registro_emocional)

        // Ocultar la barra de acciones por defecto de Android
        supportActionBar?.hide()

        // Inicializa las referencias de vista y listeners
        setupViews()
        // Configura la cuadrícula de emojis con sus textos
        setupEmojiGrid()
    }

    private fun setupViews() {
        // Enlaza las variables con los IDs definidos en activity_registro_emocional.xml
        gridEmociones = findViewById(R.id.grid_emojis)
        cardFeedback = findViewById(R.id.card_feedback)
        tvFeedbackEmoji = findViewById(R.id.tv_feedback_emoji)
        tvFeedbackText = findViewById(R.id.tv_feedback_text)
        btnRegister = findViewById(R.id.btn_register)
        btnResources = findViewById(R.id.btn_resources)
        btnHelp = findViewById(R.id.btn_help)
        btnBack = findViewById(R.id.btn_back)

        // Asignar listeners a los botones del encabezado y acciones
        btnBack.setOnClickListener { finish() }
        btnHelp.setOnClickListener { showHelpModal() }
        btnRegister.setOnClickListener { showConfirmModal() }
        btnResources.setOnClickListener { showResourcesModal() }

    }

    private fun setupEmojiGrid() {
        // Itera sobre la lista de datos y configura el contenido y listener de cada botón incluido
        EMOJIS.forEach { emoji ->
            // Encontrar la vista incluida por su ID (ej: R.id.btn_emoji_very_happy)
            val includeId = resources.getIdentifier("btn_emoji_${emoji.key}", "id", packageName)
            val emojiView = findViewById<View>(includeId)

            // Acceder a los elementos internos de la plantilla item_emoji_button
            val symbolView = emojiView.findViewById<TextView>(R.id.tv_emoji_symbol)
            val labelView = emojiView.findViewById<TextView>(R.id.tv_emoji_label)

            // Asigna el contenido
            symbolView.text = emoji.symbol
            labelView.text = emoji.label

            // Configuración inicial del estilo (no seleccionado)
            emojiView.setBackgroundResource(R.drawable.bg_emoji_unselected)
            labelView.setTextColor(ContextCompat.getColor(this, R.color.black))

            // Asigna el listener de clic para manejar la selección
            emojiView.setOnClickListener {
                setSelectedEmoji(emoji.key)
            }
        }
    }

    // Lógica principal de selección
    private fun setSelectedEmoji(key: String) {
        selectedKey = key

        // 1. Itera sobre todos los botones para actualizar el estado visual
        EMOJIS.forEach { emoji ->
            val includeId = resources.getIdentifier("btn_emoji_${emoji.key}", "id", packageName)
            val emojiView = findViewById<View>(includeId)
            val labelView = emojiView.findViewById<TextView>(R.id.tv_emoji_label)

            if (emoji.key == key) {
                // Si es el seleccionado: fondo de acento y texto blanco
                emojiView.setBackgroundResource(R.drawable.bg_emoji_selected)
                labelView.setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                // Si NO es el seleccionado: fondo blanco y texto negro
                emojiView.setBackgroundResource(R.drawable.bg_emoji_unselected)
                labelView.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
        }

        // 2. Muestra y configura el panel de Feedback y botones de acción
        updateFeedbackPanel()
    }

    // Lógica para mostrar feedback y botones condicionales
    private fun updateFeedbackPanel() {
        val key = selectedKey ?: return

        val isCritical = key == "angry" || key == "worried"

        // Configura y muestra el CardView de Feedback
        tvFeedbackEmoji.text = EMOJIS.find { it.key == key }?.symbol
        tvFeedbackText.text = FEEDBACK_TEXT[key]
        cardFeedback.visibility = View.VISIBLE

        // Configurar Botones de Acción
        btnRegister.visibility = View.VISIBLE

        if (isCritical) {
            btnResources.visibility = View.VISIBLE
            // Aplicar color crítico al botón de recursos (rojo suave)
            btnResources.setBackgroundColor(ContextCompat.getColor(this, R.color.color_critical_accent))
        } else {
            btnResources.visibility = View.GONE
            // Aplicar color primario al botón de registro
            btnRegister.setBackgroundColor(ContextCompat.getColor(this, R.color.color_primary_emotion))
        }
    }

    // --- Implementación de Modales (Diálogos de Alerta en Android) ---

    private fun showHelpModal() {
        // Diálogo simple para la ayuda
        AlertDialog.Builder(this)
            .setTitle("Ayuda")
            .setMessage("Para registrar tu emoción, elige la opción que mejor refleje cómo te sientes en este momento. Si tu emoción es intensa o negativa, considera leer la recomendación o acceder a los recursos antes de continuar. Luego, presiona \"Registrar emoción\", tu registro se guardará y podrás consultarlo luego pulsando el gráfico al costado del título.")
            .setPositiveButton("Entendido") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showConfirmModal() {
        // Diálogo para confirmar el registro
        val selectedEmoji = EMOJIS.find { it.key == selectedKey }
        val message = "Vas a registrar: ${selectedEmoji?.symbol} ${selectedEmoji?.label}"

        AlertDialog.Builder(this)
            .setTitle("Confirmar registro")
            .setMessage(message)
            .setPositiveButton("Confirmar") { dialog, _ ->
                // Lógica para guardar el registro (Aquí iría el Firebase Firestore o Base de Datos)
                Toast.makeText(this, "Emoción ${selectedEmoji?.label} registrada con éxito.", Toast.LENGTH_SHORT).show()

                // Resetear el estado de la UI
                selectedKey = null
                cardFeedback.visibility = View.GONE
                btnRegister.visibility = View.GONE
                btnResources.visibility = View.GONE
                setupEmojiGrid() // Vuelve a aplicar el estilo no seleccionado a todos
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showResourcesModal() {
        // Diálogo para mostrar la lista de recursos (requiere crear una vista dinámica)
        val resourcesLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }

        RESOURCES.forEach { resource ->
            val resourceItem = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 16, 0, 16)
                }
                text = "${resource.icon} ${resource.label}"
                textSize = 16f
                setTextColor(ContextCompat.getColor(context, R.color.black))
                // Listener simulado para el recurso
                setOnClickListener {
                    Toast.makeText(context, "Abriendo ${resource.label}", Toast.LENGTH_SHORT).show()
                }
            }
            resourcesLayout.addView(resourceItem)
        }

        AlertDialog.Builder(this)
            .setTitle("Recursos de apoyo")
            .setView(resourcesLayout)
            .setPositiveButton("Contactar consejería") { _, _ ->
                Toast.makeText(this, "Redirigiendo a contacto...", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cerrar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // --- Clases de datos inmutables (data classes) ---
    data class Emoji(val symbol: String, val label: String, val key: String)
    data class Resource(val icon: String, val label: String)
}