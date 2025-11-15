package com.txa.notificationtest

import android.content.ContentValues
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object RingtoneManagerHelper {
    
    fun installRingtones(context: Context): Boolean {
        return try {
            // Install chuong.mp3
            installRingtone(context, R.raw.chuong, "chuong", context.getString(R.string.txa_ringtone_chuong))
            
            // Install onlol.mp3
            installRingtone(context, R.raw.onlol, "onlol", context.getString(R.string.txa_ringtone_onlol))
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    private fun installRingtone(
        context: Context,
        rawResId: Int,
        fileName: String,
        title: String
    ): Boolean {
        return try {
            val inputStream: InputStream = context.resources.openRawResource(rawResId)
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ (API 29+)
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, title)
                    put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Ringtones")
                    put(MediaStore.Audio.Media.IS_RINGTONE, true)
                    put(MediaStore.Audio.Media.IS_NOTIFICATION, true)
                    put(MediaStore.Audio.Media.IS_ALARM, false)
                    put(MediaStore.Audio.Media.IS_MUSIC, false)
                }
                
                val uri = context.contentResolver.insert(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    RingtoneManager.setActualDefaultRingtoneUri(
                        context,
                        RingtoneManager.TYPE_RINGTONE,
                        it
                    )
                }
            } else {
                // Android 9 and below
                val ringtonesDir = File(context.getExternalFilesDir(null), "Ringtones")
                if (!ringtonesDir.exists()) {
                    ringtonesDir.mkdirs()
                }
                
                val ringtoneFile = File(ringtonesDir, "$fileName.mp3")
                FileOutputStream(ringtoneFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DATA, ringtoneFile.absolutePath)
                    put(MediaStore.MediaColumns.TITLE, title)
                    put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
                    put(MediaStore.Audio.Media.IS_RINGTONE, true)
                    put(MediaStore.Audio.Media.IS_NOTIFICATION, true)
                }
                
                context.contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
            }
            
            inputStream.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

