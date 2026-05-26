package com.example.edugeeksquiz.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edugeeksquiz.data.repository.QuizRepository
import com.example.edugeeksquiz.domain.model.Question
import com.example.edugeeksquiz.domain.model.QuizCategory
import com.example.edugeeksquiz.domain.model.QuizResult
import com.example.edugeeksquiz.domain.model.QuizSession

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.UUID

// ── State ──────────────────────────────────────────────────────────────────
data class QuizUiState(
    val categories: List<QuizCategory> = emptyList(),
    val selectedCategory: String = "",
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val userAnswers: MutableMap<Int, Int> = mutableMapOf(),
    val selectedOption: Int? = null,
    val isAnswerRevealed: Boolean = false,
    val timeLeft: Int = 30,
    val totalTimeTakenMs: Long = 0L,
    val result: QuizResult? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val sessionId: String = UUID.randomUUID().toString()
)

// ── ViewModel ──────────────────────────────────────────────────────────────
class QuizViewModel(
    private val quizRepository: QuizRepository,
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState(categories = quizRepository.categories))
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var quizStartTime = 0L

    // ── Category & Setup ───────────────────────────────────────────────────
    suspend fun loadQuestions(category: String) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                isLoading = true,
                error = null
            )
        }
        try {
            val questions = quizRepository.getQuestions(category)
            quizStartTime = System.currentTimeMillis()
            _uiState.update {
                it.copy(
                    questions = questions,
                    currentIndex = 0,
                    userAnswers = mutableMapOf(),
                    selectedOption = null,
                    isAnswerRevealed = false,
                    isLoading = false,
                    timeLeft = questions.firstOrNull()?.timeLimit ?: 30,
                    sessionId = UUID.randomUUID().toString()
                )
            }
            startTimer()
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, error = e.message) }
        }
    }

    // ── Answer Selection ───────────────────────────────────────────────────
    fun selectAnswer(optionIndex: Int) {
        val state = _uiState.value
        if (state.isAnswerRevealed) return
        timerJob?.cancel()

        val updatedAnswers = state.userAnswers.toMutableMap()
            .also { it[state.currentIndex] = optionIndex }
        _uiState.update {
            it.copy(
                selectedOption = optionIndex,
                isAnswerRevealed = true,
                userAnswers = updatedAnswers
            )
        }
    }

    // ── Question Navigation ────────────────────────────────────────────────
    fun navigateNext() {
        val state = _uiState.value
        if (state.currentIndex < state.questions.size - 1) {
            val nextIndex = state.currentIndex + 1
            _uiState.update {
                it.copy(
                    currentIndex = nextIndex,
                    selectedOption = it.userAnswers[nextIndex],
                    isAnswerRevealed = it.userAnswers.containsKey(nextIndex),
                    timeLeft = it.questions[nextIndex].timeLimit
                )
            }
            if (!_uiState.value.isAnswerRevealed) startTimer()
        }
        // Caller (NavGraph) handles finish navigation when on last question
    }

    fun navigatePrevious() {
        val state = _uiState.value
        if (state.currentIndex > 0) {
            timerJob?.cancel()
            val prevIndex = state.currentIndex - 1
            _uiState.update {
                it.copy(
                    currentIndex = prevIndex,
                    selectedOption = it.userAnswers[prevIndex],
                    isAnswerRevealed = it.userAnswers.containsKey(prevIndex),
                    timeLeft = it.questions[prevIndex].timeLimit
                )
            }
            if (!_uiState.value.isAnswerRevealed) startTimer()
        }
    }

    fun skipQuestion() {
        timerJob?.cancel()
        navigateNext()
    }

    fun isLastQuestion(): Boolean {
        val state = _uiState.value
        return state.currentIndex == state.questions.size - 1
    }

    // ── Timer ──────────────────────────────────────────────────────────────
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val timeLimit = _uiState.value.questions
                .getOrNull(_uiState.value.currentIndex)?.timeLimit ?: 30
            for (remaining in timeLimit downTo 0) {
                _uiState.update { it.copy(timeLeft = remaining) }
                if (remaining == 0) {
                    delay(300)
                    if (!_uiState.value.isAnswerRevealed) skipQuestion()
                    break
                }
                delay(1000)
            }
        }
    }

    // ── Finish ─────────────────────────────────────────────────────────────
    fun finishQuiz(): QuizResult {
        timerJob?.cancel()
        val state = _uiState.value
        val totalTime = System.currentTimeMillis() - quizStartTime
        val session = QuizSession(
            id = state.sessionId,
            userId = userId,
            category = state.selectedCategory,
            questions = state.questions,
            userAnswers = state.userAnswers,
            totalTimeTakenMs = totalTime
        )
        val result = QuizResult.from(session)
        _uiState.update { it.copy(result = result, totalTimeTakenMs = totalTime) }

        viewModelScope.launch {
            quizRepository.saveResult(result, userId)
        }
        return result
    }

    // ── Reset ──────────────────────────────────────────────────────────────
    fun resetQuiz() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                result = null,
                questions = emptyList(),
                selectedCategory = "",
                currentIndex = 0,
                userAnswers = mutableMapOf(),
                selectedOption = null,
                isAnswerRevealed = false
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
