package com.txa.flashcards.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY createdAt DESC")
    fun getAllWords(): Flow<List<Word>>
    
    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Long): Word?
    
    @Query("SELECT * FROM words WHERE english LIKE :query OR vietnamese LIKE :query")
    fun searchWords(query: String): Flow<List<Word>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<Word>)
    
    @Update
    suspend fun updateWord(word: Word)
    
    @Delete
    suspend fun deleteWord(word: Word)
    
    @Query("DELETE FROM words")
    suspend fun deleteAllWords()
}

