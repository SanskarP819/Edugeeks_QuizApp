package com.example.edugeeksquiz.presentation.result



import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edugeeksquiz.domain.model.QuizResult
import com.example.edugeeksquiz.presentation.components.GradientButton
import com.example.edugeeksquiz.presentation.components.ScoreCircle
import com.example.edugeeksquiz.presentation.components.StatCard
import com.example.edugeeksquiz.ui.theme.CorrectGreen
import com.example.edugeeksquiz.ui.theme.GradientEnd
import com.example.edugeeksquiz.ui.theme.GradientStart
import com.example.edugeeksquiz.ui.theme.NeutralOrange
import com.example.edugeeksquiz.ui.theme.Primary
import com.example.edugeeksquiz.ui.theme.SkippedBlue
import com.example.edugeeksquiz.ui.theme.Tertiary
import com.example.edugeeksquiz.ui.theme.WrongRed

@Composable
fun ResultScreen(
    result: QuizResult,
    onRetake: () -> Unit,
    onHome: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(300)
        showContent = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            // Trophy / Fail emoji with bounce
            AnimatedVisibility(
                visible = showContent,
                enter = scaleIn(spring(Spring.DampingRatioLowBouncy)) + fadeIn()
            ) {
                val emoji = when {
                    result.percentage >= 90f -> "🏆"
                    result.percentage >= 80f -> "🥇"
                    result.percentage >= 60f -> "✅"
                    result.percentage >= 40f -> "📚"
                    else -> "💪"
                }
                Text(emoji, fontSize = 72.sp)
            }

            Spacer(Modifier.height(8.dp))

            // Grade badge
            AnimatedVisibility(visible = showContent, enter = fadeIn(tween(400, 200))) {
                val gradeColor = when {
                    result.percentage >= 80f -> CorrectGreen
                    result.percentage >= 60f -> NeutralOrange
                    else -> WrongRed
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    if (result.passed) GradientStart else WrongRed,
                                    if (result.passed) GradientEnd else Tertiary
                                )
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        if (result.passed) "🎉 Quiz Passed!" else "Better Luck Next Time",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Score Circle
            AnimatedVisibility(visible = showContent, enter = fadeIn(tween(500, 300)) + scaleIn(tween(500, 300))) {
                ScoreCircle(percentage = result.percentage)
            }

            Spacer(Modifier.height(8.dp))

            // Grade Text
            AnimatedVisibility(visible = showContent, enter = fadeIn(tween(400, 400))) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Grade: ${result.grade}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Score: ${result.score} points",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // Stat Cards Grid
            AnimatedVisibility(visible = showContent, enter = fadeIn(tween(500, 500)) + slideInVertically(tween(500, 500)) { it / 2 }) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            icon = Icons.Default.CheckCircle,
                            value = "${result.correctAnswers}",
                            label = "Correct",
                            color = CorrectGreen,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            icon = Icons.Default.Cancel,
                            value = "${result.wrongAnswers}",
                            label = "Wrong",
                            color = WrongRed,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            icon = Icons.Default.RemoveCircle,
                            value = "${result.skippedAnswers}",
                            label = "Skipped",
                            color =SkippedBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            icon = Icons.Default.QuestionAnswer,
                            value = "${result.totalQuestions}",
                            label = "Total Qs",
                            color = Primary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            icon = Icons.Default.Timer,
                            value = formatTime(result.timeTakenMs),
                            label = "Time Taken",
                            color = NeutralOrange,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // Motivational message
            AnimatedVisibility(visible = showContent, enter = fadeIn(tween(400, 700))) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Primary.copy(0.08f))
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("💬", fontSize = 24.sp)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            getMotivationalMessage(result.percentage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.8f),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // Action Buttons
            AnimatedVisibility(visible = showContent, enter = fadeIn(tween(400, 900)) + slideInVertically(tween(400, 900)) { it / 2 }) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    GradientButton(
                        text = "🔄 Retake Quiz",
                        onClick = onRetake,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedButton(
                        onClick = onHome,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Home, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Back to Home", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

private fun formatTime(ms: Long): String {
    val seconds = ms / 1000
    return if (seconds < 60) "${seconds}s" else "${seconds / 60}m ${seconds % 60}s"
}

private fun getMotivationalMessage(percentage: Float) = when {
    percentage >= 90f -> "Outstanding! You're a true expert in this topic! 🌟"
    percentage >= 80f -> "Excellent work! You have a strong grasp of the material!"
    percentage >= 70f -> "Good job! A little more practice and you'll be at the top!"
    percentage >= 60f -> "Not bad! Review the topics you missed and try again!"
    percentage >= 40f -> "Keep going! Every attempt makes you smarter. You've got this!"
    else -> "Don't give up! Learning is a journey. Review the material and try again! 💪"
}
