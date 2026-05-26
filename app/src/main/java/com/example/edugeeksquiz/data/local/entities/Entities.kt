package com.example.edugeeksquiz.data.local.entities



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey val sessionId: String,
    val userId: String,
    val category: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val skippedAnswers: Int,
    val score: Int,
    val percentage: Float,
    val timeTakenMs: Long,
    val passed: Boolean,
    val grade: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_questions")
data class CachedQuestionEntity(
    @PrimaryKey val id: String,
    val text: String,
    val optionsJson: String,       // JSON array of strings
    val correctAnswerIndex: Int,
    val explanation: String,
    val category: String,
    val difficulty: String,
    val timeLimit: Int,
    val cachedAt: Long = System.currentTimeMillis()
)
