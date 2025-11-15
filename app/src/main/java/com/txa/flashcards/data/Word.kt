package com.txa.flashcards.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val english: String,
    val vietnamese: String,
    val example: String? = null,
    val category: String? = null,
    val difficulty: Int = 1, // 1: Easy, 2: Medium, 3: Hard
    val createdAt: Long = System.currentTimeMillis()
)

