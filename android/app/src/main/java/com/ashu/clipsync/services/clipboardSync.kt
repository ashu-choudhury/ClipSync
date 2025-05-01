package com.ashu.clipsync.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ClipboardSync: Service() {
    private lateinit var clipboardManager: ClipboardManager
    private val channelId = "ClipboardSyncChannel"
    private val notificationId = 1

    override fun onCreate() {
        super.onCreate()
//        setNotification()
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.addPrimaryClipChangedListener {
            val clipData = clipboardManager.primaryClip
            if (clipData != null && clipData.itemCount > 0) {
                val text = clipData.getItemAt(0).text.toString()
                sendDataInPc(text)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
private fun setNotification() {
    val channel = NotificationChannel(
        channelId,
        "Clipboard Sync Service",
        NotificationManager.IMPORTANCE_DEFAULT
    )
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
    val notification = NotificationCompat.Builder(this, channelId)
        .setContentTitle("Clipboard Syncing")
        .setContentText("Syncing clipboard data in the background")
        .setSmallIcon(android.R.drawable.ic_notification_overlay)
        .build()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        startForeground(notificationId, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC)
    } else {
        startForeground(notificationId, notification)
    }
}

    override fun onDestroy() {
        clipboardManager.removePrimaryClipChangedListener { }
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setNotification()
        return START_STICKY
    }

    private fun sendDataInPc(text: String) {
        // currently not implemented
    }
}
