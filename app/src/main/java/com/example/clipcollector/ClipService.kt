package com.example.clipcollector

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log

class ClipService : Service() {

    override fun onCreate() {
        super.onCreate()
        Log.d("ClipService", "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("ClipService", "onStartCommand CALLED")

        startForeground(
            1,
            Notification.Builder(this, createChannel())
                .setContentTitle("ClipCollector")
                .setContentText("Service running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()
        )

        // ПОКА БЕЗ overlay — просто проверяем запуск
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createChannel(): String {
        val id = "clip_channel"
        if (Build.VERSION.SDK_INT >= 26) {
            val ch = NotificationChannel(
                id,
                "ClipCollector",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
        }
        return id
    }
}
