package com.txa.notificationtest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper {
    
    private const val CHANNEL_ID = "txa_notification_channel"
    private const val NOTIFICATION_ID = 1
    
    fun sendTestNotification(context: Context, ringtoneIndex: Int) {
        // Get ringtone URI based on selection
        val ringtoneUri = when (ringtoneIndex) {
            0 -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) // Default
            1 -> Uri.parse("android.resource://${context.packageName}/${R.raw.chuong}")
            2 -> Uri.parse("android.resource://${context.packageName}/${R.raw.onlol}")
            else -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
        
        // Tạo hoặc cập nhật channel với sound mới
        createOrUpdateNotificationChannel(context, ringtoneUri)
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(context.getString(R.string.txa_notification_title))
            .setContentText(context.getString(R.string.txa_notification_content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID, notification)
        
        // Log notification sent
        LogHelper.logApp("Notification sent with ringtone index: $ringtoneIndex, URI: $ringtoneUri")
    }
    
    private fun createOrUpdateNotificationChannel(context: Context, soundUri: Uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Kiểm tra channel đã tồn tại chưa
            var channel = notificationManager.getNotificationChannel(CHANNEL_ID)
            
            if (channel == null) {
                // Tạo channel mới
                channel = NotificationChannel(
                    CHANNEL_ID,
                    "TXA Notification Channel",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Channel for TXA test notifications"
                    enableVibration(true)
                    setSound(soundUri, null)
                }
                notificationManager.createNotificationChannel(channel)
            } else {
                // Cập nhật sound cho channel hiện có
                // Note: Trên Android 8.0+, phải xóa và tạo lại channel để thay đổi sound
                notificationManager.deleteNotificationChannel(CHANNEL_ID)
                
                channel = NotificationChannel(
                    CHANNEL_ID,
                    "TXA Notification Channel",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Channel for TXA test notifications"
                    enableVibration(true)
                    setSound(soundUri, null)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}

