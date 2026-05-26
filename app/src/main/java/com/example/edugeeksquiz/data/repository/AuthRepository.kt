package com.example.edugeeksquiz.data.repository



import android.util.Log

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

// ─────────────────────────────────────────────────────────────────────────────
// Auth Repository
// ─────────────────────────────────────────────────────────────────────────────
class AuthRepository(private val auth: FirebaseAuth) {

    val currentUser: FirebaseUser? get() = auth.currentUser
    val isLoggedIn: Boolean get() = currentUser != null

    suspend fun signUp(email: String, password: String, name: String): Result<FirebaseUser> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: error("User creation failed")
        // Update display name
        val profileUpdate = com.google.firebase.auth.UserProfileChangeRequest.Builder()
            .setDisplayName(name).build()
        user.updateProfile(profileUpdate).await()
        user
    }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> = runCatching {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        result.user ?: error("Sign in failed")
    }

    fun signOut() = auth.signOut()

    suspend fun resetPassword(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
    }
}

