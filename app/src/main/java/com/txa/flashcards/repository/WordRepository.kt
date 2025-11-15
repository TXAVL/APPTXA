package com.txa.flashcards.repository

import com.txa.flashcards.data.Word
import com.txa.flashcards.data.WordDao
import kotlinx.coroutines.flow.Flow

class WordRepository(private val wordDao: WordDao) {
    fun getAllWords(): Flow<List<Word>> = wordDao.getAllWords()
    
    suspend fun getWordById(id: Long): Word? = wordDao.getWordById(id)
    
    fun searchWords(query: String): Flow<List<Word>> = wordDao.searchWords("%$query%")
    
    suspend fun insertWord(word: Word): Long = wordDao.insertWord(word)
    
    suspend fun insertWords(words: List<Word>) = wordDao.insertWords(words)
    
    suspend fun updateWord(word: Word) = wordDao.updateWord(word)
    
    suspend fun deleteWord(word: Word) = wordDao.deleteWord(word)
    
    suspend fun deleteAllWords() = wordDao.deleteAllWords()
}

