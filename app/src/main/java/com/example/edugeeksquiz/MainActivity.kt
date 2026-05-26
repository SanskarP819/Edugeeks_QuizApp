package com.example.edugeeksquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import com.example.edugeeksquiz.presentation.EduGeeksApp

import com.example.edugeeksquiz.ui.theme.EduGeeksQuizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as QuizApplication
        setContent {
            EduGeeksQuizTheme {
                EduGeeksApp(app = app)
            }
        }
    }
}




