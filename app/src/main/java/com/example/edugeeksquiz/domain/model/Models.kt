package com.example.edugeeksquiz.domain.model




// ── Quiz Question ──────────────────────────────────────────────────────────
data class Question(
    val id: String = "",
    val text: String = "",
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Int = 0,
    val explanation: String = "",
    val category: String = "",
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val timeLimit: Int = 30 // seconds per question
)

enum class Difficulty(val label: String, val color: Long) {
    EASY("Easy", 0xFF4CAF50),
    MEDIUM("Medium", 0xFFFF9800),
    HARD("Hard", 0xFFF44336)
}

// ── Quiz Session ───────────────────────────────────────────────────────────
data class QuizSession(
    val id: String = "",
    val userId: String = "",
    val category: String = "",
    val questions: List<Question> = emptyList(),
    val userAnswers: Map<Int, Int> = emptyMap(), // questionIndex -> selectedOptionIndex
    val startTimeMs: Long = 0L,
    val endTimeMs: Long = 0L,
    val totalTimeTakenMs: Long = 0L
)

// ── Quiz Result ────────────────────────────────────────────────────────────
data class QuizResult(
    val sessionId: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val skippedAnswers: Int,
    val score: Int,
    val percentage: Float,
    val timeTakenMs: Long,
    val category: String,
    val passed: Boolean,
    val grade: String
) {
    companion object {
        fun from(session: QuizSession): QuizResult {
            val total = session.questions.size
            var correct = 0
            var wrong = 0
            session.questions.forEachIndexed { index, question ->
                val answer = session.userAnswers[index]
                when {
                    answer == null -> { /* skipped */ }
                    answer == question.correctAnswerIndex -> correct++
                    else -> wrong++
                }
            }
            val skipped = total - correct - wrong
            val percentage = if (total > 0) (correct.toFloat() / total) * 100f else 0f
            val score = correct * 10
            val passed = percentage >= 60f
            val grade = when {
                percentage >= 90f -> "A+"
                percentage >= 80f -> "A"
                percentage >= 70f -> "B"
                percentage >= 60f -> "C"
                percentage >= 50f -> "D"
                else -> "F"
            }
            return QuizResult(
                sessionId = session.id,
                totalQuestions = total,
                correctAnswers = correct,
                wrongAnswers = wrong,
                skippedAnswers = skipped,
                score = score,
                percentage = percentage,
                timeTakenMs = session.totalTimeTakenMs,
                category = session.category,
                passed = passed,
                grade = grade
            )
        }
    }
}

// ── User ───────────────────────────────────────────────────────────────────
data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val totalQuizzesTaken: Int = 0,
    val averageScore: Float = 0f
)

// ── Quiz Category ──────────────────────────────────────────────────────────
data class QuizCategory(
    val id: String,
    val name: String,
    val icon: String,
    val description: String,
    val questionCount: Int,
    val colorHex: Long
)
