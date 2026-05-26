package com.example.edugeeksquiz.data.local



import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.edugeeksquiz.data.local.dao.CachedQuestionDao
import com.example.edugeeksquiz.data.local.dao.QuizResultDao
import com.example.edugeeksquiz.data.local.entities.CachedQuestionEntity
import com.example.edugeeksquiz.data.local.entities.QuizResultEntity


@Database(
    entities = [QuizResultEntity::class, CachedQuestionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class QuizDatabase : RoomDatabase() {

    abstract fun quizResultDao(): QuizResultDao
    abstract fun cachedQuestionDao(): CachedQuestionDao

    companion object {
        @Volatile private var INSTANCE: QuizDatabase? = null

        fun getInstance(context: Context): QuizDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java,
                    "quiz_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
