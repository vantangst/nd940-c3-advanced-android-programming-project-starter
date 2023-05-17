package com.udacity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0

fun NotificationManager.sendNotification(notification: NotificationBody, messageBody: String, applicationContext: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val downloadChannel = createChannel(
            applicationContext.getString(R.string.download_notification_channel_id),
            applicationContext.getString(R.string.download_notification_channel_id),
            applicationContext.getString(R.string.download_notification_channel_id)
        )
        createNotificationChannel(downloadChannel)
    }

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra(DetailActivity.ARGUMENT_KEY, notification)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.download_notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_button),
            contentPendingIntent
        )

    notify(NOTIFICATION_ID, builder.build())
}

@RequiresApi(Build.VERSION_CODES.O)
private fun createChannel(
    channelId: String,
    channelName: String,
    channelDescription: String
): NotificationChannel {
    val notificationChannel = NotificationChannel(
        channelId,
        channelName,
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        setShowBadge(false)
    }
    notificationChannel.description = channelDescription
    return notificationChannel
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}