package com.txa.notificationtest

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import java.io.File

class TXAApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize logging
        LogHelper.init(this)
        
        // Log app startup
        LogHelper.logStartup("Application started")
        LogHelper.logStartup("Android Version: ${Build.VERSION.RELEASE}")
        LogHelper.logStartup("SDK Version: ${Build.VERSION.SDK_INT}")
        LogHelper.logStartup("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
        
        // Set up crash handler
        setupCrashHandler()
        
        // Request storage permission for logging (Android 11+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestStoragePermission()
        }
    }
    
    /**
     * Set up global crash handler
     */
    private fun setupCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                // Log crash
                LogHelper.logCrash(throwable, "Uncaught exception in thread: ${thread.name}")
            } catch (e: Exception) {
                Log.e("TXAApplication", "Failed to log crash", e)
            }
            
            // Call default handler
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
    
    /**
     * Request storage permission for Android 11+
     */
    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                LogHelper.logApp("Storage permission not granted. Logs will be saved to app-specific directory.")
            } else {
                LogHelper.logApp("Storage permission granted.")
            }
        }
    }
}

