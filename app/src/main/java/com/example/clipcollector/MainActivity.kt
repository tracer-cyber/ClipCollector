1  package com.example.clipcollector
     2
     3  import android.content.Intent
     4  import android.net.Uri
     5  import android.os.Bundle
     6  import android.provider.Settings
     7  import android.widget.TextView
     8  import android.widget.Button
     9  import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
    10

    11  class MainActivity : AppCompatActivity() {
    12
    13      private val logPath = "/sdcard/ClipCollector/cliplog.txt"

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    checkStoragePermission()
}
    14
private fun checkStoragePermission() {
    if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            1001
        )
    }
}

    15      override fun onCreate(savedInstanceState: Bundle?) {
    16          super.onCreate(savedInstanceState)
    17          setContentView(R.layout.activity_main)
    18
    19          val tvStatus = findViewById<TextView>(R.id.tvStatus)
    20          val btnStart = findViewById<Button>(R.id.btnStart)
    21
    22          tvStatus.text = "Log file:\n$logPath"
    23
    24          btnStart.setOnClickListener {
    25              if (!Settings.canDrawOverlays(this)) {
    26                  val intent = Intent(
    27                      Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
    28                      Uri.parse("package:$packageName")
    29                  )
    30                  startActivity(intent)
    31              } else {
    32                  startService(Intent(this, FloatingService::class.java))
    33                  tvStatus.text = "Сервис запущен\nЛог: $logPath"
    34              }
    35          }
    36      }
    37  }
    38
