package com.example.edugeeksquiz




import android.app.Application
import com.example.edugeeksquiz.data.local.QuizDatabase
import com.example.edugeeksquiz.data.repository.AuthRepository
import com.example.edugeeksquiz.data.repository.QuizRepository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson


class QuizApplication : Application() {

    // ── Firebase ───────────────────────────────────────────────────────────
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // ── Database ───────────────────────────────────────────────────────────
    private val database: QuizDatabase by lazy { QuizDatabase.getInstance(this) }
    private val gson: Gson by lazy { Gson() }

    // ── Repositories ───────────────────────────────────────────────────────
    val authRepository: AuthRepository by lazy { AuthRepository(firebaseAuth) }

    val quizRepository: QuizRepository by lazy {
        QuizRepository(
            questionDao = database.cachedQuestionDao(),
            resultDao = database.quizResultDao(),
            firestore = firestore,
            gson = gson
        )
    }



    override fun onCreate() {
        super.onCreate()
        // Configure Firestore offline persistence
        firestore.firestoreSettings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }
}
