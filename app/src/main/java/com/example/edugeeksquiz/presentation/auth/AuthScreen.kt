package com.example.edugeeksquiz.presentation.auth



import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edugeeksquiz.presentation.components.EduTextField
import com.example.edugeeksquiz.presentation.components.GradientButton
import com.example.edugeeksquiz.ui.theme.CorrectGreen
import com.example.edugeeksquiz.ui.theme.GradientEnd
import com.example.edugeeksquiz.ui.theme.GradientStart
import com.example.edugeeksquiz.ui.theme.Primary
import com.example.edugeeksquiz.ui.theme.WrongRed


@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthenticated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var isLoginMode by remember { mutableStateOf(true) }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) onAuthenticated()
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
        // Decorative circles
        DecorativeBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))

            // Logo / Title
            AppLogo()

            Spacer(Modifier.height(40.dp))

            // Tab Switch
            AuthTabSwitch(isLoginMode = isLoginMode, onToggle = { isLoginMode = it })

            Spacer(Modifier.height(24.dp))

            // Form Card
            AnimatedContent(
                targetState = isLoginMode,
                transitionSpec = {
                    slideInHorizontally { if (targetState) -it else it } + fadeIn() togetherWith
                            slideOutHorizontally { if (targetState) it else -it } + fadeOut()
                },
                label = "auth_form"
            ) { loginMode ->
                if (loginMode) {
                    LoginForm(
                        onSignIn = { email, password -> viewModel.signIn(email, password) },
                        onForgotPassword = { email -> viewModel.resetPassword(email) },
                        isLoading = uiState.isLoading
                    )
                } else {
                    SignupForm(
                        onSignUp = { name, email, password, confirm -> viewModel.signUp(email, password, name, confirm) },
                        isLoading = uiState.isLoading
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Error / Success snackbar
            uiState.error?.let { error ->
                MessageBanner(message = error, isError = true, onDismiss = { viewModel.clearError() })
            }
            uiState.successMessage?.let { msg ->
                MessageBanner(message = msg, isError = false, onDismiss = { viewModel.clearError() })
            }
        }
    }
}

@Composable
private fun DecorativeBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_anim")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 20f,
        animationSpec = infiniteRepeatable(tween(3000), RepeatMode.Reverse),
        label = "bg_offset"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(color = Color(0xFF6C63FF).copy(alpha = 0.08f), radius = 200.dp.toPx(), center = Offset(size.width * 0.1f, size.height * 0.1f + offset))
        drawCircle(color = Color(0xFF00D4AA).copy(alpha = 0.06f), radius = 150.dp.toPx(), center = Offset(size.width * 0.9f, size.height * 0.3f - offset))
        drawCircle(color = Color(0xFFFF6B6B).copy(alpha = 0.05f), radius = 180.dp.toPx(), center = Offset(size.width * 0.5f, size.height * 0.85f + offset / 2))
    }
}

@Composable
private fun AppLogo() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(GradientStart, GradientEnd))),
            contentAlignment = Alignment.Center
        ) {
            Text("📚", fontSize = 40.sp)
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "EduGeeks Quiz",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            "Test your knowledge, level up your skills",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AuthTabSwitch(isLoginMode: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
    ) {
        listOf("Login" to true, "Sign Up" to false).forEach { (label, isLogin) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isLoginMode == isLogin) Brush.horizontalGradient(listOf(GradientStart, GradientEnd)) else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent)))
                    .clickable { onToggle(isLogin) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    fontWeight = if (isLoginMode == isLogin) FontWeight.Bold else FontWeight.Normal,
                    color = if (isLoginMode == isLogin) Color.White else MaterialTheme.colorScheme.onSurface.copy(0.6f)
                )
            }
        }
    }
}

@Composable
private fun LoginForm(
    onSignIn: (String, String) -> Unit,
    onForgotPassword: (String) -> Unit,
    isLoading: Boolean
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Welcome Back!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Sign in to continue", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
            Spacer(Modifier.height(24.dp))

            EduTextField(value = email, onValueChange = { email = it }, label = "Email Address", leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next))
            Spacer(Modifier.height(16.dp))

            EduTextField(value = password, onValueChange = { password = it }, label = "Password", leadingIcon = Icons.Default.Lock,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done))

            TextButton(onClick = { if (email.isNotBlank()) onForgotPassword(email) },
                modifier = Modifier.align(Alignment.End)) {
                Text("Forgot Password?", color = Primary)
            }

            Spacer(Modifier.height(8.dp))
            GradientButton(text = "Sign In", onClick = { onSignIn(email, password) }, modifier = Modifier.fillMaxWidth(), loading = isLoading)
        }
    }
}

@Composable
private fun SignupForm(
    onSignUp: (String, String, String, String) -> Unit,
    isLoading: Boolean
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(8.dp)) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Create Account", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Join thousands of learners!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
            Spacer(Modifier.height(24.dp))

            EduTextField(value = name, onValueChange = { name = it }, label = "Full Name", leadingIcon = Icons.Default.Person,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next))
            Spacer(Modifier.height(12.dp))
            EduTextField(value = email, onValueChange = { email = it }, label = "Email Address", leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next))
            Spacer(Modifier.height(12.dp))
            EduTextField(value = password, onValueChange = { password = it }, label = "Password", leadingIcon = Icons.Default.Lock,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { IconButton(onClick = { showPassword = !showPassword }) { Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next))
            Spacer(Modifier.height(12.dp))
            EduTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = "Confirm Password", leadingIcon = Icons.Default.LockOpen,
                isError = confirmPassword.isNotEmpty() && password != confirmPassword,
                errorMessage = "Passwords do not match",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done))

            Spacer(Modifier.height(20.dp))
            GradientButton("Create Account", onClick = { onSignUp(name, email, password, confirmPassword) }, modifier = Modifier.fillMaxWidth(), loading = isLoading)
        }
    }
}

@Composable
private fun MessageBanner(message: String, isError: Boolean, onDismiss: () -> Unit) {
    val bgColor = if (isError) WrongRed.copy(0.1f) else CorrectGreen.copy(0.1f)
    val iconColor = if (isError) WrongRed else CorrectGreen

    LaunchedEffect(message) {
        kotlinx.coroutines.delay(4000)
        onDismiss()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(if (isError) Icons.Default.Error else Icons.Default.CheckCircle, null, tint = iconColor)
        Spacer(Modifier.width(8.dp))
        Text(message, color = iconColor, modifier = Modifier.weight(1f))
        IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Close, null, tint = iconColor)
        }
    }
}
