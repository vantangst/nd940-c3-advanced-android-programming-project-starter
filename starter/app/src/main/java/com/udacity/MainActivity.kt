package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private lateinit var downloadManager: DownloadManager
    private lateinit var btnDownload: LoadingButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnDownload = findViewById(R.id.custom_button)
        setSupportActionBar(findViewById(R.id.toolbar))
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        btnDownload.setOnClickListener {
            download(URL_GLIDE, "glide")
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e("MainActivity", "onReceive EXTRA_DOWNLOAD: DONE")
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun download(url: String, fileName: String) {
        btnDownload.setState(ButtonState.Loading)
        btnDownload.setProgress(1f)
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalFilesDir(
                    this,
                    Environment.DIRECTORY_DOWNLOADS,
                    "$fileName.zip"
                )
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL_GLIDE =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val URL_UDACITY =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_RETROFIT =
            "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
