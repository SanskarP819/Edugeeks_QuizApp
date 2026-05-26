package com.example.edugeeksquiz.presentation.quiz


import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edugeeksquiz.domain.model.Difficulty
import com.example.edugeeksquiz.domain.model.Question
import com.example.edugeeksquiz.presentation.components.TimerRing
import com.example.edugeeksquiz.ui.theme.CorrectGreen
import com.example.edugeeksquiz.ui.theme.GradientEnd
import com.example.edugeeksquiz.ui.theme.GradientStart
import com.example.edugeeksquiz.ui.theme.Primary
import com.example.edugeeksquiz.ui.theme.WrongRed

@Composable
fun QuizScreen(
    state: QuizUiState,
    onSelectAnswer: (Int) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSkip: () -> Unit,
    onQuit: () -> Unit
) {
    var showQuitDialog by remember { mutableStateOf(false) }

    BackHandler { showQuitDialog = true }

    val question = state.questions.getOrNull(state.currentIndex) ?: return
    val progress = (state.currentIndex + 1).toFloat() / state.questions.size.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        // Top Bar
        QuizTopBar(
            currentIndex = state.currentIndex,
            totalQuestions = state.questions.size,
            progress = progress,
            timeLeft = state.timeLeft,
            totalTime = question.timeLimit,
            category = state.selectedCategory,
            onQuit = { showQuitDialog = true }
        )

        // Question content
        AnimatedContent(
            targetState = state.currentIndex,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                } else {
                    slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                }
            },
            modifier = Modifier.weight(1f),
            label = "question_anim"
        ) {currentIndex->
            QuestionContent(
                question = state.questions.get(currentIndex),
                selectedOption = state.selectedOption,
                isAnswerRevealed = state.isAnswerRevealed,
                onSelectAnswer = onSelectAnswer
            )
        }

        // Bottom navigation
        QuizBottomBar(
            currentIndex = state.currentIndex,
            totalQuestions = state.questions.size,
            isAnswerRevealed = state.isAnswerRevealed,
            onPrevious = onPrevious,
            onNext = onNext,
            onSkip = onSkip
        )
    }

    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text("Quit Quiz?", fontWeight = FontWeight.Bold) },
            text = { Text("Your progress will be lost. Are you sure you want to quit?") },
            confirmButton = {
                Button(
                    onClick = { showQuitDialog = false; onQuit() },
                    colors = ButtonDefaults.buttonColors(containerColor = WrongRed)
                ) { Text("Quit", color = Color.White) }
            },
            dismissButton = {
                OutlinedButton(onClick = { showQuitDialog = false }) { Text("Continue Quiz") }
            }
        )
    }
}

@Composable
private fun QuizTopBar(
    currentIndex: Int, totalQuestions: Int, progress: Float,
    timeLeft: Int, totalTime: Int, category: String,
    onQuit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd)))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    category.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Question ${currentIndex + 1} of $totalQuestions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(0.8f)
                )
            }
            TimerRing(timeLeft = timeLeft, totalTime = totalTime)
        }

        Spacer(Modifier.height(12.dp))

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(0.3f))
        ) {
            val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(300), label = "progress")
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White)
            )
        }

        // Question number dots (scrollable)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(totalQuestions) { index ->
                val isActive = index == currentIndex
                val isPast = index < currentIndex
                Box(
                    modifier = Modifier
                        .size(if (isActive) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (isActive) Color.White else if (isPast) Color.White.copy(0.6f) else Color.White.copy(0.3f))
                )
            }
        }
    }
}

@Composable
private fun QuestionContent(
    question: Question,
    selectedOption: Int?,
    isAnswerRevealed: Boolean,
    onSelectAnswer: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Difficulty badge
        DifficultyBadge(question.difficulty)
        Spacer(Modifier.height(16.dp))

        // Question text
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Text(
                text = question.text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(20.dp),
                lineHeight = 26.sp
            )
        }

        Spacer(Modifier.height(20.dp))

        // Options
        question.options.forEachIndexed { index, option ->
            AnswerOption(
                text = option,
                index = index,
                selectedOption = selectedOption,
                correctIndex = question.correctAnswerIndex,
                isRevealed = isAnswerRevealed,
                onSelect = { onSelectAnswer(index) }
            )
            Spacer(Modifier.height(10.dp))
        }

        // Explanation
        if (isAnswerRevealed && question.explanation.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            AnimatedVisibility(visible = true, enter = fadeIn() + expandVertically()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Primary.copy(0.08f)),
                    border = BorderStroke(1.dp,Primary.copy(0.3f))
                ) {
                    Row(Modifier.padding(16.dp)) {
                        Text("💡", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Explanation", fontWeight = FontWeight.Bold, color = Primary,
                                style = MaterialTheme.typography.labelLarge)
                            Spacer(Modifier.height(4.dp))
                            Text(question.explanation, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnswerOption(
    text: String,
    index: Int,
    selectedOption: Int?,
    correctIndex: Int,
    isRevealed: Boolean,
    onSelect: () -> Unit
) {
    val isSelected = selectedOption == index
    val isCorrect = index == correctIndex

    val backgroundColor = when {
        !isRevealed && isSelected -> Primary.copy(0.15f)
        !isRevealed -> MaterialTheme.colorScheme.surface
        isCorrect -> CorrectGreen.copy(0.15f)
        isSelected && !isCorrect -> WrongRed.copy(0.15f)
        else -> MaterialTheme.colorScheme.surface
    }
    val borderColor = when {
        !isRevealed && isSelected -> Primary
        !isRevealed -> MaterialTheme.colorScheme.outline.copy(0.3f)
        isCorrect -> CorrectGreen
        isSelected && !isCorrect -> WrongRed
        else -> MaterialTheme.colorScheme.outline.copy(0.3f)
    }

    val scale by animateFloatAsState(
        targetValue = if (isSelected && !isRevealed) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "option_scale"
    )

    val labels = listOf("A", "B", "C", "D")

    Row(
        modifier = Modifier
            .scale(scale)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = !isRevealed, onClick = onSelect)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Label circle
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    when {
                        !isRevealed && isSelected -> Primary
                        isRevealed && isCorrect -> CorrectGreen
                        isRevealed && isSelected && !isCorrect ->WrongRed
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isRevealed) {
                when {
                    isCorrect -> Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    isSelected -> Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    else -> Text(labels[index], fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                }
            } else {
                Text(labels[index], fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            }
        }

        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isSelected || (isRevealed && isCorrect)) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DifficultyBadge(difficulty: Difficulty) {
    val color = Color(difficulty.color)
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Star, null, tint = color, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(difficulty.label, style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun QuizBottomBar(
    currentIndex: Int, totalQuestions: Int, isAnswerRevealed: Boolean,
    onPrevious: () -> Unit, onNext: () -> Unit, onSkip: () -> Unit
) {
    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous
            OutlinedButton(
                onClick = onPrevious,
                enabled = currentIndex > 0,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.size(52.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
            }

            // Skip
            if (!isAnswerRevealed) {
                OutlinedButton(
                    onClick = onSkip,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Skip")
                }
            }

            // Next
            Button(
                onClick = onNext,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                modifier = Modifier.weight(if (isAnswerRevealed) 2f else 1f)
            ) {
                Text(if (currentIndex == totalQuestions - 1) "Finish" else "Next")
                Spacer(Modifier.width(4.dp))
                Icon(
                    if (currentIndex == totalQuestions - 1) Icons.Default.Flag
                    else Icons.AutoMirrored.Filled.ArrowForward,
                    null, modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
