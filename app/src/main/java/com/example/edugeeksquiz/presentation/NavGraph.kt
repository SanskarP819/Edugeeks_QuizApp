package com.example.edugeeksquiz.presentation



import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.edugeeksquiz.QuizApplication
import com.example.edugeeksquiz.presentation.auth.AuthScreen
import com.example.edugeeksquiz.presentation.auth.AuthViewModel
import com.example.edugeeksquiz.presentation.components.LoadingOverlay
import com.example.edugeeksquiz.presentation.quiz.CategorySelectScreen
import com.example.edugeeksquiz.presentation.quiz.QuizScreen
import com.example.edugeeksquiz.presentation.quiz.QuizViewModel
import com.example.edugeeksquiz.presentation.result.ResultScreen
import com.example.edugeeksquiz.ui.theme.EduGeeksQuizTheme

import kotlinx.coroutines.launch

// ── Route constants ────────────────────────────────────────────────────────
object Routes {
    const val AUTH = "auth"
    const val HOME = "home"
    const val QUIZ = "quiz/{category}"
    const val RESULT = "result"

    fun quiz(category: String) = "quiz/$category"
}

// ── Root composable ────────────────────────────────────────────────────────
@Composable
fun EduGeeksApp(app: QuizApplication) {
    EduGeeksQuizTheme {
        val navController = rememberNavController()
        val authViewModel = remember { AuthViewModel(app.authRepository) }
        val authState by authViewModel.uiState.collectAsState()

        // Single shared QuizViewModel — lives as long as EduGeeksApp is in composition
        val quizViewModel = remember {
            QuizViewModel(
                quizRepository = app.quizRepository,
                userId = authViewModel.currentUser?.uid ?: ""
            )
        }

        EduGeeksNavHost(
            navController = navController,
            authViewModel = authViewModel,
            quizViewModel = quizViewModel,
            isAuthenticated = authState.isAuthenticated,
            userName = authViewModel.currentUser?.displayName ?: "Learner"
        )
    }
}

// ── Nav Host ───────────────────────────────────────────────────────────────
@Composable
fun EduGeeksNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    quizViewModel: QuizViewModel,
    isAuthenticated: Boolean,
    userName: String
) {
    val startDestination = if (isAuthenticated) Routes.HOME else Routes.AUTH
    val scope = rememberCoroutineScope()
    val quizState by quizViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(tween(300)) { it } + fadeIn(tween(300))
        },
        exitTransition = {
            slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300))
        }
    ) {

        // ── Auth Screen ──────────────────────────────────────────────────
        composable(Routes.AUTH) {
            AuthScreen(
                viewModel = authViewModel,
                onAuthenticated = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        // ── Home / Category Select ───────────────────────────────────────
        composable(Routes.HOME) {
            CategorySelectScreen(
                categories = quizState.categories,
                userName = userName,
                onSelectCategory = { category ->
                    scope.launch {
                        quizViewModel.loadQuestions(category)
                        navController.navigate(Routes.quiz(category))
                    }
                },
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        // ── Quiz Screen ──────────────────────────────────────────────────
        composable(Routes.QUIZ) {
            // Show loading overlay while questions are being fetched
            if (quizState.isLoading) {
                LoadingOverlay("Loading questions...")
                return@composable
            }

            QuizScreen(
                state = quizState,
                onSelectAnswer = { quizViewModel.selectAnswer(it) },
                onNext = {
                    if (quizViewModel.isLastQuestion()) {
                        val result = quizViewModel.finishQuiz()
                        navController.navigate(Routes.RESULT) {
                            popUpTo(Routes.HOME) // keep HOME in back stack, remove QUIZ
                        }
                    } else {
                        quizViewModel.navigateNext()
                    }
                },
                onPrevious = { quizViewModel.navigatePrevious() },
                onSkip = {
                    if (quizViewModel.isLastQuestion()) {
                        quizViewModel.finishQuiz()
                        navController.navigate(Routes.RESULT) {
                            popUpTo(Routes.HOME)
                        }
                    } else {
                        quizViewModel.skipQuestion()
                    }
                },
                onQuit = {
                    quizViewModel.resetQuiz()
                    navController.popBackStack(Routes.HOME, inclusive = false)
                }
            )
        }

        // ── Result Screen ────────────────────────────────────────────────
        composable(Routes.RESULT) {
            val result = quizState.result

            if (result == null) {
                // Safety fallback — shouldn't normally happen
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
                return@composable
            }

            ResultScreen(
                result = result,
                onRetake = {
                    scope.launch {
                        quizViewModel.loadQuestions(quizState.selectedCategory)
                        navController.navigate(Routes.quiz(quizState.selectedCategory)) {
                            popUpTo(Routes.HOME)
                        }
                    }
                },
                onHome = {
                    quizViewModel.resetQuiz()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}
