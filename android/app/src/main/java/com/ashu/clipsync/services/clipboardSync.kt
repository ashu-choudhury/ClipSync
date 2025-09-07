package com.ashu.clipsync.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ashu.clipsync.AppSession
import com.ashu.clipsync.helpers.logToFile
import io.ably.lib.realtime.AblyRealtime
import io.ably.lib.types.ClientOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ClipboardSync: Service() {
    private lateinit var clipboardManager: ClipboardManager
    private val channelId = "ClipboardSyncChannel"
    private val notificationId = 1
    private lateinit var ably: AblyRealtime
    private lateinit var ablyChannel: io.ably.lib.realtime.Channel
    private var currentClipboardText = ""
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    override fun onCreate() {
        super.onCreate()
        val options = ClientOptions("__api-key__")
        ably = AblyRealtime(options)
         ablyChannel = ably.channels.get(AppSession.user?.email)
        ablyChannel.subscribe("clipboard") { text ->
            if (currentClipboardText == text.data.toString()) return@subscribe
            val clip = ClipData.newPlainText("Remote Clipboard", text.data.toString())

            clipboardManager.setPrimaryClip(clip)
            currentClipboardText = text.data.toString()
        }
        setNotification()

        logToFile("service start date! ",applicationContext)
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        serviceScope.launch {
            while (true) {
                val clipData = clipboardManager.primaryClip
                if (clipData != null && clipData.itemCount > 0) {
                    val item = clipData.getItemAt(0)
                    val newText = item?.text?.toString() ?: continue

                    if (newText != currentClipboardText) {
                        currentClipboardText = newText
                        logToFile("Coroutine Polling: $newText", applicationContext)
                        ablyChannel.publish("clipboard", newText)
                    }

                }
                delay(1000)
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
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setNotification()
        return START_STICKY
    }

}
