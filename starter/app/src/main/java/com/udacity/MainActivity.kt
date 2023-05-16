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
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private lateinit var btnDownload: LoadingButton
    private lateinit var rgDownloadOptions: RadioGroup


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnDownload = findViewById(R.id.custom_button)
        rgDownloadOptions = findViewById(R.id.rgDownloadOptions)
        setSupportActionBar(findViewById(R.id.toolbar))
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        btnDownload.setOnClickListener {
            val selectedId = rgDownloadOptions.checkedRadioButtonId
            var fileName = ""
            var url = ""
            when (selectedId) {
                R.id.glide_option -> {
                    fileName = "glide_repo"
                    url = URL_GLIDE
                }
                R.id.udacity_option -> {
                    fileName = "udacity_repo"
                    url = URL_UDACITY
                }
                R.id.retrofit_option -> {
                    fileName = "retrofit_repo"
                    url = URL_RETROFIT
                }
            }
            if (url.isEmpty()) {
                Toast.makeText(this, getString(R.string.msg_select_file_to_download), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            download(url, fileName)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e("MainActivity", "onReceive EXTRA_DOWNLOAD: DONE")
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun download(url: String, fileName: String) {
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
