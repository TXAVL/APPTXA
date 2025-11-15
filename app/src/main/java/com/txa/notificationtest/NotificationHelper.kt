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
        createNotificationChannel(context)
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Get ringtone URI based on selection
        val ringtoneUri = when (ringtoneIndex) {
            0 -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) // Default
            1 -> Uri.parse("android.resource://${context.packageName}/${R.raw.chuong}")
            2 -> Uri.parse("android.resource://${context.packageName}/${R.raw.onlol}")
            else -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(context.getString(R.string.txa_notification_title))
            .setContentText(context.getString(R.string.txa_notification_content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(ringtoneUri)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "TXA Notification Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for TXA test notifications"
                enableVibration(true)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

