package com.example.trivtastic
import java.io.Serializable
data class QuizResponse(
    val responseCode: Int,
    val results: List<Question>
): Serializable

data class Question(
    val type: String,
    val difficulty: String,
    val category: String,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
): Serializable