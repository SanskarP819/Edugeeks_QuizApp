package com.example.edugeeksquiz.data.local.dao



import androidx.room.*
import com.example.edugeeksquiz.data.local.entities.CachedQuestionEntity
import com.example.edugeeksquiz.data.local.entities.QuizResultEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface QuizResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: QuizResultEntity)

    @Query("SELECT * FROM quiz_results WHERE userId = :userId ORDER BY timestamp DESC")
    fun getResultsForUser(userId: String): Flow<List<QuizResultEntity>>

    @Query("SELECT * FROM quiz_results WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastResult(userId: String): QuizResultEntity?

    @Query("SELECT AVG(percentage) FROM quiz_results WHERE userId = :userId")
    suspend fun getAverageScore(userId: String): Float?

    @Query("DELETE FROM quiz_results WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)
}

@Dao
interface CachedQuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<CachedQuestionEntity>)

    @Query("SELECT * FROM cached_questions WHERE category = :category")
    suspend fun getQuestionsByCategory(category: String): List<CachedQuestionEntity>

    @Query("SELECT COUNT(*) FROM cached_questions WHERE category = :category")
    suspend fun countByCategory(category: String): Int

    @Query("DELETE FROM cached_questions WHERE category = :category")
    suspend fun deleteByCateogry(category: String)

    @Query("DELETE FROM cached_questions WHERE cachedAt < :expiryTime")
    suspend fun deleteExpired(expiryTime: Long)
}
