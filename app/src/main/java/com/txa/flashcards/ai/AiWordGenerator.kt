package com.txa.flashcards.ai

import com.google.gson.Gson
import com.txa.flashcards.data.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AiWordGenerator(private val apiKey: String) {
    private val retrofit: Retrofit
    private val api: GeminiApi
    private val gson = Gson()

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(GeminiApi::class.java)
    }

    suspend fun generateWords(
        count: Int = 20,
        category: String? = null,
        difficulty: Int = 1
    ): List<Word> = withContext(Dispatchers.IO) {
        try {
            val prompt = buildPrompt(count, category, difficulty)
            
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(Part(text = prompt))
                    )
                )
            )

            val response = api.generateContent(apiKey, request)
            
            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                val text = responseBody.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: return@withContext emptyList()
                
                parseWordsFromResponse(text)
            } else {
                val errorBody = response.errorBody()?.string()
                println("Error: $errorBody")
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun buildPrompt(count: Int, category: String?, difficulty: Int): String {
        val difficultyText = when (difficulty) {
            1 -> "basic/easic"
            2 -> "intermediate"
            3 -> "advanced"
            else -> "basic"
        }
        
        val categoryText = category?.let { " in category: $it" } ?: ""
        
        return """
            Generate $count English-Vietnamese vocabulary words for flash cards.
            Difficulty level: $difficultyText$categoryText
            
            Format: Return ONLY a JSON array, each word as an object with these exact fields:
            {
                "english": "word in English",
                "vietnamese": "translation in Vietnamese",
                "example": "example sentence in English",
                "category": "category name",
                "difficulty": $difficulty
            }
            
            Example format:
            [
                {
                    "english": "Hello",
                    "vietnamese": "Xin chào",
                    "example": "Hello, how are you?",
                    "category": "Greetings",
                    "difficulty": 1
                }
            ]
            
            Return ONLY the JSON array, no other text. Make sure the JSON is valid.
        """.trimIndent()
    }

    private fun parseWordsFromResponse(text: String): List<Word> {
        return try {
            // Extract JSON from response (might have markdown code blocks)
            var jsonText = text.trim()
            
            // Remove markdown code blocks if present
            if (jsonText.startsWith("```")) {
                jsonText = jsonText.substringAfter("```")
                if (jsonText.contains("\n")) {
                    jsonText = jsonText.substringAfter("\n")
                }
            }
            if (jsonText.endsWith("```")) {
                jsonText = jsonText.substringBeforeLast("```").trim()
            }
            
            // Remove any leading/trailing non-JSON text
            val jsonStart = jsonText.indexOf('[')
            val jsonEnd = jsonText.lastIndexOf(']') + 1
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                jsonText = jsonText.substring(jsonStart, jsonEnd)
            }
            
            val wordDataList: List<WordData> = gson.fromJson(jsonText, Array<WordData>::class.java).toList()
            
            wordDataList.map { data ->
                Word(
                    english = data.english,
                    vietnamese = data.vietnamese,
                    example = data.example,
                    category = data.category,
                    difficulty = data.difficulty
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private data class WordData(
        val english: String,
        val vietnamese: String,
        val example: String?,
        val category: String?,
        val difficulty: Int
    )
}

