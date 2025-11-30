package com.example.dsmproyecto.ui.trivia

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dsmproyecto.R

class TriviaActivity : AppCompatActivity() {

    // --- Elementos de UI ---
    private lateinit var layoutTemas: LinearLayout
    private lateinit var layoutPregunta: LinearLayout
    private lateinit var layoutResultados: LinearLayout

    private lateinit var txtPregunta: TextView
    private lateinit var txtContadorPreguntas: TextView
    private lateinit var txtNivelDificultad: TextView // <--- NUEVA VARIABLE DECLARADA AQUÍ
    private lateinit var btnOpciones: List<Button>
    private lateinit var btnSiguiente: Button

    // Componentes de resultados
    private lateinit var txtResultado: TextView
    private lateinit var txtPorcentaje: TextView
    private lateinit var progressCircular: ProgressBar
    private lateinit var progressHorizontal: ProgressBar
    private lateinit var txtFeedback: TextView
    private lateinit var txtRecomendacion: TextView
    private lateinit var btnReiniciar: Button
    private lateinit var btnInicio: Button

    private lateinit var btnBack: ImageButton
    private lateinit var btnHelp: ImageButton

    // --- Lógica del Juego ---
    // Actualizamos el molde de la pregunta para incluir dificultad
    data class Pregunta(
        val enunciado: String,
        val opciones: List<String>,
        val indiceCorrecto: Int,
        val dificultad: String // <--- CAMPO NUEVO
    )

    private var preguntasActuales: List<Pregunta> = emptyList()
    private var indexPregunta = 0
    private var puntaje = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trivia)
        inicializarVistas()
        configurarListeners()
    }

    private fun inicializarVistas() {
        layoutTemas = findViewById(R.id.layoutTemas)
        layoutPregunta = findViewById(R.id.layoutPregunta)
        layoutResultados = findViewById(R.id.layoutResultados)

        txtPregunta = findViewById(R.id.txtPregunta)
        txtContadorPreguntas = findViewById(R.id.txtContadorPreguntas)

        // AQUÍ ES DONDE DEBE IR LA ASIGNACIÓN (DENTRO DE LA FUNCIÓN)
        txtNivelDificultad = findViewById(R.id.txtNivelDificultad)

        // Resultados
        txtResultado = findViewById(R.id.txtResultado)
        txtPorcentaje = findViewById(R.id.txtPorcentaje)
        progressCircular = findViewById(R.id.progressCircular)
        progressHorizontal = findViewById(R.id.progressHorizontal)
        txtFeedback = findViewById(R.id.txtFeedback)
        txtRecomendacion = findViewById(R.id.txtRecomendacion)

        btnOpciones = listOf(
            findViewById(R.id.btnOpcion1),
            findViewById(R.id.btnOpcion2),
            findViewById(R.id.btnOpcion3),
            findViewById(R.id.btnOpcion4)
        )

        btnSiguiente = findViewById(R.id.btnSiguiente)
        btnReiniciar = findViewById(R.id.btnReiniciar)
        btnInicio = findViewById(R.id.btnInicio)
        btnBack = findViewById(R.id.btnBack)
        btnHelp = findViewById(R.id.btnHelp)
    }

    private fun configurarListeners() {
        val temaListener = View.OnClickListener { view ->
            val tema = when(view.id) {
                R.id.btnTemaAutocuidado -> "Autocuidado"
                R.id.btnTemaEjercicio -> "Ejercicio"
                R.id.btnTemaSaludMental -> "SaludMental"
                R.id.btnTemaNutricion -> "Nutricion"
                R.id.btnTemaHabitos -> "Habitos"
                else -> "General"
            }
            iniciarJuego(tema)
        }

        findViewById<Button>(R.id.btnTemaAutocuidado).setOnClickListener(temaListener)
        findViewById<Button>(R.id.btnTemaEjercicio).setOnClickListener(temaListener)
        findViewById<Button>(R.id.btnTemaSaludMental).setOnClickListener(temaListener)
        findViewById<Button>(R.id.btnTemaNutricion).setOnClickListener(temaListener)
        findViewById<Button>(R.id.btnTemaHabitos).setOnClickListener(temaListener)

        btnOpciones.forEachIndexed { index, boton ->
            boton.setOnClickListener { verificarRespuesta(index, boton) }
        }

        btnSiguiente.setOnClickListener {
            indexPregunta++
            if (indexPregunta < preguntasActuales.size) {
                mostrarPregunta()
            } else {
                mostrarResultados()
            }
        }

        btnReiniciar.setOnClickListener {
            layoutResultados.visibility = View.GONE
            layoutTemas.visibility = View.VISIBLE
        }

        btnInicio.setOnClickListener { finish() }

        btnBack.setOnClickListener {
            if (layoutTemas.visibility == View.VISIBLE) finish() else regresarAlMenu()
        }
        btnHelp.setOnClickListener { Toast.makeText(this, "Elige una opción", Toast.LENGTH_SHORT).show() }
    }

    private fun iniciarJuego(tema: String) {
        puntaje = 0
        indexPregunta = 0

        // Obtenemos TODAS las preguntas, las mezclamos y tomamos 5
        val todasLasPreguntas = obtenerBancoDePreguntas(tema)
        val preguntasSeleccionadas = todasLasPreguntas.shuffled().take(5)

        // Mezclamos las respuestas de cada una
        preguntasActuales = preguntasSeleccionadas.map { barajarRespuestas(it) }

        if (preguntasActuales.isEmpty()) {
            // Fallback de seguridad
            preguntasActuales = listOf(
                Pregunta("Error cargando preguntas", listOf("A", "B", "C", "D"), 0, "Error")
            )
        }

        layoutTemas.visibility = View.GONE
        layoutResultados.visibility = View.GONE
        layoutPregunta.visibility = View.VISIBLE
        mostrarPregunta()
    }

    // Función para mezclar las opciones A, B, C, D
    private fun barajarRespuestas(preguntaOriginal: Pregunta): Pregunta {
        val textoRespuestaCorrecta = preguntaOriginal.opciones[preguntaOriginal.indiceCorrecto]
        val opcionesBarajadas = preguntaOriginal.opciones.shuffled()
        val nuevoIndice = opcionesBarajadas.indexOf(textoRespuestaCorrecta)
        // Importante: copiamos también la dificultad
        return Pregunta(preguntaOriginal.enunciado, opcionesBarajadas, nuevoIndice, preguntaOriginal.dificultad)
    }

    private fun mostrarPregunta() {
        btnSiguiente.visibility = View.INVISIBLE

        txtContadorPreguntas.text = "Pregunta ${indexPregunta + 1} de ${preguntasActuales.size}"

        // Resetear botones
        btnOpciones.forEach { boton ->
            boton.isEnabled = true
            boton.backgroundTintList = null
            boton.setBackgroundResource(R.drawable.bg_answer_pill)
            boton.setTextColor(Color.parseColor("#505050"))
        }

        val preguntaActual = preguntasActuales[indexPregunta]
        txtPregunta.text = preguntaActual.enunciado

        // --- CONFIGURAR DIFICULTAD ---
        txtNivelDificultad.text = preguntaActual.dificultad.uppercase()

        val (colorFondo, colorTexto) = when (preguntaActual.dificultad) {
            "Fácil" -> "#E8F5E9" to "#2E7D32"   // Verde
            "Medio" -> "#FFF3E0" to "#EF6C00"   // Naranja
            "Difícil" -> "#FFEBEE" to "#C62828" // Rojo
            else -> "#EDE7F6" to "#673AB7"      // Default
        }
        txtNivelDificultad.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorFondo))
        txtNivelDificultad.setTextColor(Color.parseColor(colorTexto))
        // -----------------------------

        preguntaActual.opciones.forEachIndexed { index, texto ->
            if (index < btnOpciones.size) {
                btnOpciones[index].text = texto
                btnOpciones[index].visibility = View.VISIBLE
            }
        }
    }

    private fun verificarRespuesta(indiceSeleccionado: Int, botonSeleccionado: Button) {
        val preguntaActual = preguntasActuales[indexPregunta]
        btnOpciones.forEach { it.isEnabled = false }

        if (indiceSeleccionado == preguntaActual.indiceCorrecto) {
            botonSeleccionado.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#42FF58"))
            botonSeleccionado.setTextColor(Color.parseColor("#000000"))
            puntaje++
        } else {
            botonSeleccionado.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF5252"))
            botonSeleccionado.setTextColor(Color.WHITE)
            // Mostrar la correcta
            val botonCorrecto = btnOpciones[preguntaActual.indiceCorrecto]
            botonCorrecto.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#B9F6CA"))
        }
        btnSiguiente.visibility = View.VISIBLE
    }

    private fun mostrarResultados() {
        layoutPregunta.visibility = View.GONE
        layoutResultados.visibility = View.VISIBLE

        val total = preguntasActuales.size
        val porcentaje = if (total > 0) (puntaje * 100) / total else 0

        txtResultado.text = "$puntaje de $total respuestas correctas"
        txtPorcentaje.text = "$porcentaje%"
        progressCircular.progress = porcentaje
        progressHorizontal.progress = porcentaje

        if (porcentaje >= 80) {
            txtFeedback.text = "¡Excelente trabajo! Tienes un gran conocimiento."
            txtRecomendacion.text = "Sigue manteniendo estos hábitos saludables."
        } else if (porcentaje >= 50) {
            txtFeedback.text = "¡Buen trabajo! Pero aún puedes mejorar."
            txtRecomendacion.text = "Revisa los temas donde fallaste para reforzar."
        } else {
            txtFeedback.text = "Hay oportunidades para aprender más sobre este tema."
            txtRecomendacion.text = "Te recomendamos leer más sobre los fundamentos de salud."
        }
    }

    private fun regresarAlMenu() {
        layoutPregunta.visibility = View.GONE
        layoutResultados.visibility = View.GONE
        layoutTemas.visibility = View.VISIBLE
    }

    // --- BASE DE DATOS DE PREGUNTAS ---
    private fun obtenerBancoDePreguntas(tema: String): List<Pregunta> {
        return when (tema) {
            "Autocuidado" -> listOf(
                Pregunta("¿Cuántas horas de sueño se recomiendan?", listOf("7 a 9 horas", "4 a 5 horas", "10 a 12 horas", "2 horas"), 0, "Fácil"),
                Pregunta("¿Cuál es un signo de deshidratación?", listOf("Orina oscura", "Exceso de energía", "Piel brillante", "Hambre excesiva"), 0, "Medio"),
                Pregunta("¿Qué acción protege tu piel del sol?", listOf("Usar bloqueador", "Lavarla con jabón", "Tomar sol directo", "No usar crema"), 0, "Fácil"),
                Pregunta("La regla 20-20-20 sirve para...", listOf("Descansar la vista", "Hacer abdominales", "Comer rápido", "Beber agua"), 0, "Difícil"),
                Pregunta("¿Qué es el 'skincare' básico?", listOf("Limpieza, hidratación y sol", "Solo maquillaje", "Lavarse con agua", "Usar 20 productos"), 0, "Medio"),
                Pregunta("¿Por qué es importante la postura?", listOf("Evita dolores musculares", "Te hace más alto", "Quema calorías", "Mejora la visión"), 0, "Fácil"),
                Pregunta("Temperatura ideal de ducha:", listOf("Tibia", "Hirviendo", "Helada", "Vapor"), 0, "Fácil"),
                Pregunta("¿Qué hacer si te sientes agotado?", listOf("Tomar un descanso", "Tomar 5 cafés", "Seguir trabajando", "Gritar"), 0, "Fácil"),
                Pregunta("El bruxismo suele ser causa de...", listOf("Estrés", "Hambre", "Felicidad", "Sed"), 0, "Medio"),
                Pregunta("Forma de autocuidado emocional:", listOf("Poner límites", "Decir sí a todo", "Aislarse", "Ignorar lo que sientes"), 0, "Difícil")
            )
            "Ejercicio" -> listOf(
                Pregunta("¿Cuál es un ejercicio aeróbico?", listOf("Nadar", "Levantar pesas", "Abdominales", "Estiramientos"), 0, "Fácil"),
                Pregunta("Antes de una rutina intensa debes...", listOf("Calentar", "Comer pesado", "Dormir", "Estirar estático"), 0, "Fácil"),
                Pregunta("Nutriente para recuperar músculo:", listOf("Proteína", "Grasa", "Azúcar", "Fibra"), 0, "Medio"),
                Pregunta("Minutos actividad semanal sugeridos:", listOf("150 minutos", "20 minutos", "1000 minutos", "60 minutos"), 0, "Difícil"),
                Pregunta("¿Qué es el entrenamiento HIIT?", listOf("Intervalos alta intensidad", "Dormir mucho", "Correr lento", "Yoga suave"), 0, "Difícil"),
                Pregunta("Si sientes dolor agudo al entrenar...", listOf("Parar inmediatamente", "Seguir", "Aumentar peso", "Correr más"), 0, "Fácil"),
                Pregunta("El día de descanso sirve para...", listOf("Recuperación muscular", "Perder tiempo", "Engordar", "Nada"), 0, "Fácil"),
                Pregunta("Hidratarse en el deporte sirve para...", listOf("Reponer líquidos", "Ganar peso", "No tener hambre", "Sabor"), 0, "Fácil"),
                Pregunta("El entrenamiento de fuerza mejora...", listOf("Densidad ósea", "Visión", "Oído", "Cabello"), 0, "Medio"),
                Pregunta("Ejercicio anaeróbico ejemplo:", listOf("Pesas", "Caminar", "Yoga", "Trotar suave"), 0, "Medio")
            )
            "SaludMental" -> listOf(
                Pregunta("¿Qué es la resiliencia?", listOf("Superar adversidades", "No tener sentimientos", "Ser feliz siempre", "Ignorar problemas"), 0, "Medio"),
                Pregunta("Técnica para calmar ansiedad:", listOf("Respiración profunda", "Alcohol", "Azúcar", "Ver noticias"), 0, "Fácil"),
                Pregunta("¿Qué es el 'burnout'?", listOf("Agotamiento laboral", "Calor", "Ejercicio", "Emoción"), 0, "Medio"),
                Pregunta("¿Pedir ayuda psicológica es malo?", listOf("No, es valiente", "Sí, es de débiles", "Solo para locos", "Nunca"), 0, "Fácil"),
                Pregunta("¿Qué es Mindfulness?", listOf("Atención plena", "Mente en blanco", "Futuro", "Dormir"), 0, "Difícil"),
                Pregunta("La asertividad es...", listOf("Opinar con respeto", "Gritar", "Callar", "Agredir"), 0, "Medio"),
                Pregunta("El insomnio puede ser síntoma de...", listOf("Estrés/Ansiedad", "Felicidad", "Buena dieta", "Hidratación"), 0, "Fácil"),
                Pregunta("Redes sociales en exceso causan...", listOf("Comparación/Baja autoestima", "Felicidad", "Mejor sueño", "Paz"), 0, "Fácil"),
                Pregunta("¿Qué es un ataque de pánico?", listOf("Miedo intenso repentino", "Enojo", "Hambre", "Sueño"), 0, "Medio"),
                Pregunta("Actividad para mejorar el ánimo:", listOf("Actividad física", "Encerrarse", "Dormir 20h", "Comer mal"), 0, "Fácil")
            )
            "Nutricion" -> listOf(
                Pregunta("Carbohidrato complejo ejemplo:", listOf("Avena", "Azúcar", "Refresco", "Caramelo"), 0, "Medio"),
                Pregunta("Vitamina del sol:", listOf("Vitamina D", "Vitamina C", "Vitamina A", "Vitamina B12"), 0, "Fácil"),
                Pregunta("Grasa saludable ejemplo:", listOf("Aguacate", "Aceite quemado", "Margarina", "Tocino"), 0, "Fácil"),
                Pregunta("Exceso de sodio causa...", listOf("Hipertensión", "Diabetes", "Mareos", "Sueño"), 0, "Medio"),
                Pregunta("Macronutrientes son...", listOf("Carbos, prot, grasas", "Vitaminas", "Agua", "Frutas"), 0, "Difícil"),
                Pregunta("Alimento rico en fibra:", listOf("Brócoli", "Queso", "Carne", "Huevos"), 0, "Medio"),
                Pregunta("Azúcar añadido en exceso causa...", listOf("Obesidad/Diabetes", "Fuerza", "Piel bonita", "Limpieza"), 0, "Fácil"),
                Pregunta("Plato del buen comer es...", listOf("Guía balanceada", "Plato grande", "Solo carne", "No cenar"), 0, "Fácil"),
                Pregunta("Fuente de Omega-3:", listOf("Pescado azul", "Pollo frito", "Pan", "Refresco"), 0, "Medio"),
                Pregunta("Alimento ultraprocesado es...", listOf("Con químicos industriales", "Fruta", "Carne cruda", "Agua"), 0, "Medio")
            )
            "Habitos" -> listOf(
                Pregunta("Autor 'Hábitos Atómicos':", listOf("James Clear", "Harry Potter", "Stephen King", "García Márquez"), 0, "Difícil"),
                Pregunta("Para crear hábito empieza...", listOf("Pequeño", "Todo un día", "En enero", "Forzado"), 0, "Fácil"),
                Pregunta("¿Qué es un 'disparador'?", listOf("Señal de inicio", "Arma", "Premio", "Dormir"), 0, "Medio"),
                Pregunta("Días para automatizar conducta:", listOf("66 días", "21 días", "1 año", "1 semana"), 0, "Difícil"),
                Pregunta("Regla de los 2 minutos:", listOf("Si toma < 2 min, hazlo", "Espera 2 min", "Duerme 2 min", "Come en 2 min"), 0, "Medio"),
                Pregunta("Para mantener disciplina ten...", listOf("Un 'por qué' claro", "Miedo", "Dinero", "Suerte"), 0, "Medio"),
                Pregunta("Romper mal hábito requiere...", listOf("Identificar causa", "Fuerza voluntad", "Castigo", "Ignorar"), 0, "Medio"),
                Pregunta("¿Qué es 'habit stacking'?", listOf("Unir hábito nuevo a viejo", "Apilar libros", "Todo a la vez", "Dormir"), 0, "Difícil"),
                Pregunta("El entorno influye en...", listOf("Hábitos", "Altura", "Ojos", "Nada"), 0, "Fácil"),
                Pregunta("Llevar un registro sirve para...", listOf("Visualizar progreso", "Perder tiempo", "Estrés", "Nada"), 0, "Fácil")
            )
            else -> emptyList()
        }
    }
}