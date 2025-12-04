package com.example.clipcollector

import android.app.*
import android.content.*
import android.graphics.PixelFormat
import android.os.*
import android.view.*
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import java.io.File
import android.content.ClipboardManager

class FloatingService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatButton: ImageView
    private val logFile = "/sdcard/ClipCollector/cliplog.txt"

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()

        startForegroundServiceNotification()
        createFloatingButton()

        File("/sdcard/ClipCollector").mkdirs()
        File(logFile).createNewFile()
    }

    private fun startForegroundServiceNotification() {
        val channelId = "clipcollector_channel"
        val nm = getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                channelId,
                "Clip Collector",
                NotificationManager.IMPORTANCE_LOW
            )
            nm.createNotificationChannel(chan)
        }

        val notif = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Clip Collector запущен")
            .setContentText("Лог сохраняется в: $logFile")
            .setSmallIcon(android.R.drawable.ic_menu_edit)
            .build()

        startForeground(1, notif)
    }

    private fun createFloatingButton() {
        floatButton = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_input_add)
            setBackgroundColor(0x55ffffff)
            setPadding(20, 20, 20, 20)
        }

        val params = WindowManager.LayoutParams(
            120, 120,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 100
        params.y = 300

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.addView(floatButton, params)

        floatButton.setOnTouchListener(object : View.OnTouchListener {
            var dx = 0
            var dy = 0

            override fun onTouch(v: View, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dx = params.x - e.rawX.toInt()
                        dy = params.y - e.rawY.toInt()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = dx + e.rawX.toInt()
                        params.y = dy + e.rawY.toInt()
                        windowManager.updateViewLayout(floatButton, params)
                    }
                    MotionEvent.ACTION_UP -> {
                        readClipboardAndWriteToFile()
                    }
                }
                return true
            }
        })
    }

    private fun readClipboardAndWriteToFile() {
        val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = cm.primaryClip

        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).coerceToText(this).toString()
            File(logFile).appendText("\n$text\n-----\n")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(floatButton)
    }
}

