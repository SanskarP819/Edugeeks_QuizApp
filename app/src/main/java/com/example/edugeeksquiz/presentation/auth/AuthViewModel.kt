package com.example.edugeeksquiz.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edugeeksquiz.data.repository.AuthRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState(isAuthenticated = authRepository.isLoggedIn))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val currentUser get() = authRepository.currentUser

    fun signIn(email: String, password: String) {
        if (!validateCredentials(email, password)) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.signIn(email, password)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isAuthenticated = true) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message ?: "Sign in failed") } }
        }
    }

    fun signUp(email: String, password: String, name: String, confirmPassword: String) {
        if (name.isBlank()) {
            _uiState.update { it.copy(error = "Name cannot be empty") }; return
        }
        if (password != confirmPassword) {
            _uiState.update { it.copy(error = "Passwords do not match") }; return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(error = "Password must be at least 6 characters") }; return
        }
        if (!validateCredentials(email, password)) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.signUp(email, password, name)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isAuthenticated = true) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message ?: "Sign up failed") } }
        }
    }

    fun resetPassword(email: String) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(error = "Invalid email address") }; return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.resetPassword(email)
                .onSuccess { _uiState.update { it.copy(isLoading = false, successMessage = "Reset email sent!") } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.update { AuthUiState(isAuthenticated = false) }
    }

    fun clearError() = _uiState.update { it.copy(error = null, successMessage = null) }

    private fun validateCredentials(email: String, password: String): Boolean {
        return when {
            email.isBlank() -> { _uiState.update { it.copy(error = "Email cannot be empty") }; false }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> { _uiState.update { it.copy(error = "Invalid email address") }; false }
            password.isBlank() -> { _uiState.update { it.copy(error = "Password cannot be empty") }; false }
            else -> true
        }
    }
}
