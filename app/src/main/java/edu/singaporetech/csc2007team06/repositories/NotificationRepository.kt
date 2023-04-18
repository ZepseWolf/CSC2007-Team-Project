package edu.singaporetech.csc2007team06.repositories

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.singaporetech.csc2007team06.utils.Resource
import edu.singaporetech.csc2007team06.utils.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NotificationRepository {
    private val db = Firebase.firestore
    val COLLECTION: String = "notifications"

    /**
     * Get all notifications by user id
     */
    suspend fun notifications(userId: String): Resource<QuerySnapshot> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = db.collection(COLLECTION).whereEqualTo("userId", userId)
                    .orderBy("timestamp", Query.Direction.DESCENDING).get().await()
                Resource.Success(result)
            }
        }
    }

    // update the notification to acknowledged
    fun updateNotification(id: String, acknowledge: Boolean) {
        val notificationRef = db.collection(COLLECTION).document(id)
        notificationRef.update("acknowledge", acknowledge)
    }
}