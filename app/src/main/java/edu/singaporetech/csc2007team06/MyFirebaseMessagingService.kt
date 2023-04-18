package edu.singaporetech.csc2007team06

import android.app.NotificationManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import edu.singaporetech.csc2007team06.utils.sendNotification

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "From: ${message.from}")

        message.data.let {
            Log.d(TAG, "Message data payload: " + message.data)
        }


        message.notification.let {
            Log.d(TAG, "Message Notification Body: ${it?.body}")
            sendNotification(it?.title!!, it.body!!)
        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }
    // [END on_new_token]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param title Title of the notification.
     * @param body Body of the notification.
     */
    private fun sendNotification(title: String, body: String) {
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification(title, body, applicationContext)
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}