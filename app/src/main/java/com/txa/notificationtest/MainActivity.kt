package com.txa.notificationtest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    
    private lateinit var btnSendNotification: Button
    private lateinit var btnInstallRingtones: Button
    private lateinit var btnSettings: Button
    private lateinit var spinnerRingtone: Spinner
    
    private val NOTIFICATION_PERMISSION_CODE = 100
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Log activity startup
        LogHelper.logStartup("MainActivity onCreate called")
        
        // Load saved language preference
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val language = prefs.getString("language", "vi") ?: "vi"
        currentLanguage = language
        setAppLanguage(language)
        
        setContentView(R.layout.activity_main)
        
        requestNotificationPermission()
        
        initViews()
        setupSpinner()
        setupListeners()
        
        LogHelper.logStartup("MainActivity initialized successfully")
    }
    
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE
                )
            }
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun initViews() {
        btnSendNotification = findViewById(R.id.btnSendNotification)
        btnInstallRingtones = findViewById(R.id.btnInstallRingtones)
        btnSettings = findViewById(R.id.btnSettings)
        spinnerRingtone = findViewById(R.id.spinnerRingtone)
    }
    
    private fun setupSpinner() {
        val ringtones = arrayOf(
            getString(R.string.txa_default_ringtone),
            getString(R.string.txa_ringtone_chuong),
            getString(R.string.txa_ringtone_onlol)
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ringtones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRingtone.adapter = adapter
    }
    
    private fun setupListeners() {
        btnSendNotification.setOnClickListener {
            val selectedRingtone = spinnerRingtone.selectedItemPosition
            NotificationHelper.sendTestNotification(
                this,
                selectedRingtone
            )
            Toast.makeText(
                this,
                getString(R.string.txa_notification_sent),
                Toast.LENGTH_SHORT
            ).show()
        }
        
        btnInstallRingtones.setOnClickListener {
            val success = RingtoneManagerHelper.installRingtones(this)
            if (success) {
                Toast.makeText(
                    this,
                    getString(R.string.txa_ringtone_installed),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.txa_ringtone_install_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
    
    private fun setAppLanguage(language: String) {
        val locale = when (language) {
            "en" -> java.util.Locale("en")
            else -> java.util.Locale("vi")
        }
        val config = resources.configuration
        config.setLocale(locale)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }
    
    private var currentLanguage: String = "vi"
    
    override fun onResume() {
        super.onResume()
        // Reload language when returning from settings - only recreate if language changed
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val language = prefs.getString("language", "vi") ?: "vi"
        if (language != currentLanguage) {
            currentLanguage = language
            recreate() // Only recreate if language actually changed
        }
    }
}

