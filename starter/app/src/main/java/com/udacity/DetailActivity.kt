package com.udacity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(findViewById(R.id.toolbar))
        initData()

        findViewById<Button>(R.id.btnOk).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initData() {
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(ARGUMENT_KEY, NotificationBody::class.java)
        } else {
            intent.getParcelableExtra(ARGUMENT_KEY)
        }
        Log.d(TAG, "notification data: $notification")

        val downloadStatus = if (notification?.status == true) "Success" else "Fail"
        val downloadStatusColor = if (notification?.status == true) R.color.green else R.color.red
        val downloadName = notification?.name ?: ""

        findViewById<TextView>(R.id.tvTitle).text = downloadName
        findViewById<TextView>(R.id.tvStatus).apply {
            text = downloadStatus
            setTextColor(resources.getColor(downloadStatusColor, null))
        }
    }

    companion object {
        const val TAG = "DetailActivity"
        const val ARGUMENT_KEY = "notification_body"
    }

}
