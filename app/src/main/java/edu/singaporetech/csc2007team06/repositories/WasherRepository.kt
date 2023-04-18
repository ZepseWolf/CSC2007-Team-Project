package edu.singaporetech.csc2007team06.repositories

import android.media.browse.MediaBrowser.ItemCallback
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.singaporetech.csc2007team06.models.Washer
import edu.singaporetech.csc2007team06.utils.Resource
import edu.singaporetech.csc2007team06.utils.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class WasherRepository {
    private val db = Firebase.firestore
    val COLLECTION: String = "washers"

    suspend fun washers(): Resource<QuerySnapshot> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = db.collection(COLLECTION).get().await()
                Resource.Success(result)
            }
        }
    }

    suspend fun washer(id: String): Resource<DocumentSnapshot> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = db.collection(COLLECTION).document(id).get().await()
                Resource.Success(result)
            }
        }
    }

    fun addWasher(washer: Washer){
        washer.id?.let { db.collection(COLLECTION).document(it).set(washer) }
    }

    // EditEquipmentActivity will use this function to update the washer
    fun updateWasher(washer: Washer) {
        // Update the washer
        val washerRef = washer.id?.let { db.collection(COLLECTION).document(it) }
        washerRef?.update(
            mapOf(
                "upcomingSample" to washer.upcomingSample,
                "upcomingRepair" to washer.upcomingRepair,
                "hasNotifyRepair" to washer.hasNotifyRepair,
                "hasNotifySample" to washer.hasNotifySample,
                "userId" to washer.userId,
                "userName" to washer.userName,
                "note" to washer.note
            )
        )
    }
}