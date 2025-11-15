package com.txa.flashcards.utils

import android.content.Context
import android.content.SharedPreferences

object ApiKeyManager {
    private const val PREFS_NAME = "txa_flashcards_prefs"
    private const val KEY_GEMINI_API_KEY = "AIzaSyDwfaklshEmmMAdVYWBJeVUEyt9q6rvXDY"
    
    // Default free API key (user should replace with their own)
    private const val DEFAULT_API_KEY = "AIzaSyDwfaklshEmmMAdVYWBJeVUEyt9q6rvXDY"
    
    fun getApiKey(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_GEMINI_API_KEY, DEFAULT_API_KEY) ?: DEFAULT_API_KEY
    }
    
    fun setApiKey(context: Context, apiKey: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_GEMINI_API_KEY, apiKey).apply()
    }
    
    fun hasApiKey(context: Context): Boolean {
        val key = getApiKey(context)
        return key.isNotEmpty() && key != DEFAULT_API_KEY
    }
}

