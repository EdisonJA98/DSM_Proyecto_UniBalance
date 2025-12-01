package com.example.dsmproyecto.ui.trivia

object TriviaData {
    val questions = listOf(
        Question(
            question = "¿Cuántos minutos se recomienda descansar del celular cada hora?",
            options = listOf("5 minutos", "15 minutos", "30 minutos", "No es necesario"),
            correctIndex = 1
        ),
        Question(
            question = "¿Qué postura es correcta al usar una laptop?",
            options = listOf("Jorobado", "Pantalla a la altura de los ojos", "Piernas cruzadas", "Acostado"),
            correctIndex = 1
        )
    )
}