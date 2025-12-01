package com.example.dsmproyecto.ui.trivia

data class Question(
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)