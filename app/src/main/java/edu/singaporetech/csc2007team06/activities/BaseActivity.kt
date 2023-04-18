package edu.singaporetech.csc2007team06.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.singaporetech.csc2007team06.R


/**
 * This class is used to create a base activity for all non-authenticated activities
 */
open class BaseActivity : AppCompatActivity() {
    private lateinit var toastMessage: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toastMessage = Toast.makeText(this, "", Toast.LENGTH_SHORT)

        // Set up push notification channel
        createChannel(
            getString(R.string.default_notification_channel_id),
            getString(R.string.default_notification_channel_name)
        )
    }

    private fun createChannel(channelId: String, channelName: String) {

        // Create channel only if SDK version is Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_channel_description)
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    // This function is used to replace and show a toast message
    fun showToast(message: String) {
        toastMessage.setText(message)
        toastMessage.show()
    }
}