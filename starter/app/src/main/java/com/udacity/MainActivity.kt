package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.*
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var downloadManager: DownloadManager

    private lateinit var btnDownload: LoadingButton
    private lateinit var rgDownloadOptions: RadioGroup
    private var currentOptionName = ""

    private val pushNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Log.d(TAG, "Request Notification Permission: Granted: $granted")
    }

    private fun initData() {
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
    }

    private fun setupAction() {
        btnDownload.setOnClickListener {
            val selectedId = rgDownloadOptions.checkedRadioButtonId
            var fileName = ""
            var url = ""
            when (selectedId) {
                R.id.glide_option -> {
                    fileName = "glide_repo"
                    currentOptionName = getString(R.string.glide_option)
                    url = URL_GLIDE
                }
                R.id.glide_option_error -> {
                    fileName = "glide_repo"
                    currentOptionName = getString(R.string.glide_option_error)
                    url = URL_GLIDE_FAILED
                }
                R.id.udacity_option -> {
                    fileName = "udacity_repo"
                    currentOptionName = getString(R.string.udacity_option)
                    url = URL_UDACITY
                }
                R.id.retrofit_option -> {
                    fileName = "retrofit_repo"
                    currentOptionName = getString(R.string.retrofit_option)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnDownload = findViewById(R.id.custom_button)
        rgDownloadOptions = findViewById(R.id.rgDownloadOptions)
        setSupportActionBar(findViewById(R.id.toolbar))

        initData()

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        askNotificationPermissionIfNeeded()

        setupAction()
    }

    private fun askNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pushNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun sendNotification(notification: NotificationBody) {
        notificationManager.sendNotification(
            notification,
            getString(R.string.notification_description),
            applicationContext
        )
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, downloadID)
            id?.let {
                Log.d(TAG, "onReceive EXTRA_DOWNLOAD: Finished with Success: ${getDownloadStatus(id)}")
                val notification = NotificationBody(
                    currentOptionName,
                    getDownloadStatus(id)
                )
                sendNotification(notification)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
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
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun getDownloadStatus(downloadId: Long): Boolean {
        val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
        return if (cursor?.moveToFirst() == true) {
            val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            when (cursor.getInt(columnIndex)) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    true
                }
                else -> {
                    false
                }
            }
        } else {
            false
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val URL_GLIDE = "https://github.com/bumptech/glide/archive/master.zip"
        private const val URL_GLIDE_FAILED = "https://github.com/bumptech/glide/archive/master_error.zip"
        private const val URL_UDACITY =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_RETROFIT = "https://github.com/square/retrofit/archive/master.zip"
    }

}
