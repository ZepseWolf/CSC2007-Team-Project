package edu.singaporetech.csc2007team06.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import edu.singaporetech.csc2007team06.R
import edu.singaporetech.csc2007team06.activities.NotificationActivity

// Notification ID.
private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(title: String, body: String, applicationContext: Context) {

    val contentIntent = Intent(applicationContext, NotificationActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.default_notification_channel_id)
    )
        .setContentTitle(title)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentText(body)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}
