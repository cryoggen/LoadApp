package com.udacity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

// Notification ID.
private const val NOTIFICATION_ID = 0
private const val REQUEST_CODE = 0

@SuppressLint("WrongConstant", "UnspecifiedImmutableFlag")
fun NotificationManager.sendNotification(
    messageBody: String,
    applicationContext: Context,
    status: String ,
    fileName: String
) {

    val intent = Intent(applicationContext, DetailActivity::class.java)
        .putExtra(STATUS, status)
        .putExtra(FILE_NAME, fileName)

    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        applicationContext,
        REQUEST_CODE,
        intent,
        PendingIntent.FLAG_ONE_SHOT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(
            applicationContext
                .getString(R.string.notification_title)
        )
        .setContentText(messageBody)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.check_the_status),
            pendingIntent
        )
        .setAutoCancel(true)
    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}

