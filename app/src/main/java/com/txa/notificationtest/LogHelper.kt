package com.txa.notificationtest

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

object LogHelper {
    private const val TAG = "TXAApp"
    private const val LOG_DIR = "TXAAppLogs"
    private const val CRASH_LOG_FILE = "crash_log.txt"
    private const val STARTUP_LOG_FILE = "startup_log.txt"
    private const val APP_LOG_FILE = "app_log.txt"
    
    private var logDir: File? = null
    
    /**
     * Initialize log directory
     */
    fun init(context: Context) {
        try {
            logDir = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                    // Android 14+ (API 34+) - use Downloads/TXAAppLogs
                    try {
                        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        val dir = File(downloadsDir, LOG_DIR)
                        // Tự động tạo folder nếu chưa có
                        if (!dir.exists()) {
                            dir.mkdirs()
                        }
                        // Kiểm tra có thể ghi được không
                        if (dir.exists() && dir.canWrite()) {
                            dir
                        } else {
                            // Fallback về app-specific directory nếu không ghi được
                            Log.w(TAG, "Cannot write to Downloads, using app-specific directory")
                            File(context.getExternalFilesDir(null), LOG_DIR)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to access Downloads directory, using fallback", e)
                        // Fallback về app-specific directory
                        File(context.getExternalFilesDir(null), LOG_DIR)
                    }
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    // Android 10-13 - use app-specific directory
                    File(context.getExternalFilesDir(null), LOG_DIR)
                }
                else -> {
                    // Android 9 and below - use external storage root
                    File(Environment.getExternalStorageDirectory(), LOG_DIR)
                }
            }
            
            // Tự động tạo folder nếu chưa có
            if (logDir != null && !logDir!!.exists()) {
                val created = logDir!!.mkdirs()
                if (!created) {
                    Log.e(TAG, "Failed to create log directory: ${logDir?.absolutePath}")
                } else {
                    Log.d(TAG, "Created log directory: ${logDir?.absolutePath}")
                }
            }
            
            logStartup("LogHelper initialized. Log directory: ${logDir?.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize LogHelper", e)
            // Fallback cuối cùng - sử dụng internal storage
            try {
                logDir = File(context.filesDir, LOG_DIR)
                logDir?.mkdirs()
                Log.e(TAG, "Using fallback internal storage: ${logDir?.absolutePath}")
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to use fallback storage", e2)
            }
        }
    }
    
    /**
     * Get log file path
     */
    private fun getLogFile(fileName: String): File? {
        return logDir?.let { File(it, fileName) }
    }
    
    /**
     * Write text to log file
     */
    private fun writeToFile(fileName: String, message: String) {
        try {
            val logFile = getLogFile(fileName) ?: return
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
            val logMessage = "[$timestamp] $message\n"
            
            FileWriter(logFile, true).use { writer ->
                writer.append(logMessage)
                writer.flush()
            }
            
            Log.d(TAG, "Logged to $fileName: $message")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write log to file: $fileName", e)
        }
    }
    
    /**
     * Log app startup
     */
    fun logStartup(message: String) {
        val fullMessage = "STARTUP: $message"
        Log.d(TAG, fullMessage)
        writeToFile(STARTUP_LOG_FILE, fullMessage)
        writeToFile(APP_LOG_FILE, fullMessage)
    }
    
    /**
     * Log crash/error
     */
    fun logCrash(throwable: Throwable, additionalInfo: String = "") {
        try {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            throwable.printStackTrace(pw)
            val stackTrace = sw.toString()
            
            val crashInfo = StringBuilder()
            crashInfo.append("CRASH: ${throwable.javaClass.simpleName}\n")
            crashInfo.append("Message: ${throwable.message}\n")
            if (additionalInfo.isNotEmpty()) {
                crashInfo.append("Additional Info: $additionalInfo\n")
            }
            crashInfo.append("Stack Trace:\n$stackTrace")
            crashInfo.append("\n--- Device Info ---\n")
            crashInfo.append("Android Version: ${Build.VERSION.RELEASE}\n")
            crashInfo.append("SDK Version: ${Build.VERSION.SDK_INT}\n")
            crashInfo.append("Device: ${Build.MANUFACTURER} ${Build.MODEL}\n")
            crashInfo.append("--- End Crash Log ---\n\n")
            
            Log.e(TAG, "CRASH: ${throwable.message}", throwable)
            writeToFile(CRASH_LOG_FILE, crashInfo.toString())
            writeToFile(APP_LOG_FILE, crashInfo.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log crash", e)
        }
    }
    
    /**
     * Log general app events
     */
    fun logApp(message: String) {
        val fullMessage = "APP: $message"
        Log.d(TAG, fullMessage)
        writeToFile(APP_LOG_FILE, fullMessage)
    }
    
    /**
     * Get all log files
     */
    fun getLogFiles(): List<File> {
        return logDir?.listFiles()?.filter { it.isFile && it.name.endsWith(".txt") } ?: emptyList()
    }
    
    /**
     * Clear all logs
     */
    fun clearLogs() {
        try {
            logDir?.listFiles()?.forEach { it.delete() }
            logApp("All logs cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear logs", e)
        }
    }
}

