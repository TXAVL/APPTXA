package com.txa.notificationtest

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var radioGroupLanguage: RadioGroup
    private lateinit var prefs: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.txa_settings)
        
        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        
        initViews()
        loadLanguagePreference()
        setupListeners()
    }
    
    private fun initViews() {
        radioGroupLanguage = findViewById(R.id.radioGroupLanguage)
    }
    
    private fun loadLanguagePreference() {
        val currentLanguage = prefs.getString("language", "vi") ?: "vi"
        val radioId = if (currentLanguage == "en") {
            R.id.radioEnglish
        } else {
            R.id.radioVietnamese
        }
        radioGroupLanguage.check(radioId)
    }
    
    private fun setupListeners() {
        radioGroupLanguage.setOnCheckedChangeListener { _, checkedId ->
            val language = when (checkedId) {
                R.id.radioEnglish -> "en"
                else -> "vi"
            }
            prefs.edit().putString("language", language).apply()
            // Recreate activity to apply language change
            recreate()
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

