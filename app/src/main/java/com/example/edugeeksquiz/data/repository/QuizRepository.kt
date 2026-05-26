package com.example.edugeeksquiz.data.repository

import android.util.Log
import com.example.edugeeksquiz.data.local.dao.CachedQuestionDao
import com.example.edugeeksquiz.data.local.dao.QuizResultDao
import com.example.edugeeksquiz.data.local.entities.CachedQuestionEntity
import com.example.edugeeksquiz.data.local.entities.QuizResultEntity
import com.example.edugeeksquiz.data.remote.MockQuestionSource
import com.example.edugeeksquiz.domain.model.Difficulty
import com.example.edugeeksquiz.domain.model.Question
import com.example.edugeeksquiz.domain.model.QuizResult
import com.example.edugeeksquiz.domain.model.QuizSession
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

// ─────────────────────────────────────────────────────────────────────────────
// Quiz Repository
// ─────────────────────────────────────────────────────────────────────────────
class QuizRepository(
    private val questionDao: CachedQuestionDao,
    private val resultDao: QuizResultDao,
    private val firestore: FirebaseFirestore,
    private val gson: Gson = Gson()
) {
    companion object {
        private const val TAG = "QuizRepository"
        private const val CACHE_EXPIRY_MS = 24 * 60 * 60 * 1000L // 24 hours
    }

    val categories = MockQuestionSource.categories

    // ── Questions ─────────────────────────────────────────────────────────────
    suspend fun getQuestions(category: String): List<Question> {
        // Try cache first
        val cached = questionDao.getQuestionsByCategory(category)
        if (cached.isNotEmpty()) {
            return cached.map { it.toDomain(gson) }
        }
        // Fallback to mock data (replace with network/Firebase call in production)
        val questions = MockQuestionSource.getQuestionsForCategory(category)
        // Cache them
        questionDao.insertAll(questions.map { it.toEntity(gson) })
        return questions
    }

    // ── Results ───────────────────────────────────────────────────────────────
    suspend fun saveResult(result: QuizResult, userId: String) {
        val entity = QuizResultEntity(
            sessionId = result.sessionId,
            userId = userId,
            category = result.category,
            totalQuestions = result.totalQuestions,
            correctAnswers = result.correctAnswers,
            wrongAnswers = result.wrongAnswers,
            skippedAnswers = result.skippedAnswers,
            score = result.score,
            percentage = result.percentage,
            timeTakenMs = result.timeTakenMs,
            passed = result.passed,
            grade = result.grade
        )
        resultDao.insertResult(entity)

        // Also save to Firestore for sync
        try {
            firestore.collection("users").document(userId)
                .collection("results").document(result.sessionId)
                .set(entity).await()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore save failed, result saved locally", e)
        }
    }

    fun getResultsFlow(userId: String): Flow<List<QuizResult>> =
        resultDao.getResultsForUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }

    // ── Session helper ────────────────────────────────────────────────────────
    fun createSession(category: String, questions: List<Question>, userId: String) = QuizSession(
        id = UUID.randomUUID().toString(),
        userId = userId,
        category = category,
        questions = questions,
        startTimeMs = System.currentTimeMillis()
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Extension mappers
// ─────────────────────────────────────────────────────────────────────────────
private fun CachedQuestionEntity.toDomain(gson: Gson): Question {
    val optionsType = object : TypeToken<List<String>>() {}.type
    return Question(
        id = id, text = text,
        options = gson.fromJson(optionsJson, optionsType),
        correctAnswerIndex = correctAnswerIndex, explanation = explanation,
        category = category,
        difficulty = Difficulty.valueOf(difficulty),
        timeLimit = timeLimit
    )
}

private fun Question.toEntity(gson: Gson) = CachedQuestionEntity(
    id = id, text = text,
    optionsJson = gson.toJson(options),
    correctAnswerIndex = correctAnswerIndex, explanation = explanation,
    category = category, difficulty = difficulty.name, timeLimit = timeLimit
)

private fun QuizResultEntity.toDomain() = QuizResult(
    sessionId = sessionId, totalQuestions = totalQuestions,
    correctAnswers = correctAnswers, wrongAnswers = wrongAnswers,
    skippedAnswers = skippedAnswers, score = score, percentage = percentage,
    timeTakenMs = timeTakenMs, category = category, passed = passed, grade = grade
)
