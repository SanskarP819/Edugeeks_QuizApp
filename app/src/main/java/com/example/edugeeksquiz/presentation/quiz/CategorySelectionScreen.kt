package com.example.edugeeksquiz.presentation.quiz

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edugeeksquiz.domain.model.QuizCategory
import com.example.edugeeksquiz.ui.theme.GradientEnd
import com.example.edugeeksquiz.ui.theme.GradientStart

@Composable
fun CategorySelectScreen(
    categories: List<QuizCategory>,
    userName: String,
    onSelectCategory: (String) -> Unit,
    onSignOut: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surfaceVariant)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            HomeHeader(userName = userName, onSignOut = onSignOut)

            // Stats bar
            StatsRow()

            Spacer(Modifier.height(8.dp))

            Text(
                "Choose a Topic",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Text(
                "Pick your challenge for today",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(0.6f),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.heightIn(max = 800.dp)
            ) {
                itemsIndexed(categories) { index, category ->
                    CategoryCard(
                        category = category,
                        animDelay = index * 80,
                        onStart = { onSelectCategory(category.id) }
                    )
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }

}

@Composable
private fun HomeHeader(userName: String, onSignOut: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Hello, ${userName.split(" ").firstOrNull() ?: "Learner"}! 👋",
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Ready to quiz?", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(0.6f))
        }
        IconButton(onClick = onSignOut,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)) {
            Icon(Icons.Default.Logout, contentDescription = "Sign out")
        }
    }
}

@Composable
private fun StatsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd)))
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        StatItem("📚", "5", "Topics")
        Divider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.White.copy(0.3f))
        StatItem("❓", "50", "Questions")
        Divider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.White.copy(0.3f))
        StatItem("🤖", "AI", "Powered")
    }
}

@Composable
private fun StatItem(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 20.sp)
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(0.8f))
    }
}

@Composable
private fun CategoryCard(
    category: QuizCategory,
    animDelay: Int,
    onStart: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )
    val catColor = Color(category.colorHex)

    Card(
        modifier = Modifier
            .scale(scale)
            .fillMaxWidth()
            .clickable { isPressed = true; onStart() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = catColor.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, catColor.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(category.icon, fontSize = 32.sp)
            Spacer(Modifier.height(8.dp))
            Text(category.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = catColor)
            Text(category.description, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(0.7f), maxLines = 2)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(catColor.copy(0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.QuestionAnswer, null, tint = catColor, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("${category.questionCount} Qs", style = MaterialTheme.typography.labelMedium, color = catColor)
            }
        }
    }
}

