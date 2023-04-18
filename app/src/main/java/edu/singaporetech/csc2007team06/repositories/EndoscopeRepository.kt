package edu.singaporetech.csc2007team06.repositories

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.singaporetech.csc2007team06.models.Endoscope
import edu.singaporetech.csc2007team06.utils.Resource
import edu.singaporetech.csc2007team06.utils.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EndoscopeRepository {
    private val db = Firebase.firestore
    val COLLECTION: String = "endoscopes"

    /**
     * Get all endoscopes
     */
    suspend fun endoscopes(): Resource<QuerySnapshot> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = db.collection(COLLECTION).get().await()
                Resource.Success(result)
            }
        }
    }

    /**
     * Get endoscope by id
     */
    suspend fun endoscope(id: String): Resource<DocumentSnapshot> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = db.collection(COLLECTION).document(id).get().await()
                Resource.Success(result)
            }
        }
    }

    /**
     * Add endoscope
     */
    fun addEndoscope(endoscope: Endoscope) {
        endoscope.id?.let { db.collection(COLLECTION).document(it).set(endoscope) }
    }

    // EditEquipmentActivity will use this function to update the endoscope
    fun updateEndoscope(endoscope: Endoscope) {
        // Update the endoscope
        val endoscopeRef = endoscope.id?.let { db.collection(COLLECTION).document(it) }
        endoscopeRef?.update(
            mapOf(
                "upcomingSample" to endoscope.upcomingSample,
                "upcomingRepair" to endoscope.upcomingRepair,
                "hasNotifyRepair" to endoscope.hasNotifyRepair,
                "hasNotifySample" to endoscope.hasNotifySample,
                "userId" to endoscope.userId,
                "userName" to endoscope.userName,
                "note" to endoscope.note
            )
        )
    }
}