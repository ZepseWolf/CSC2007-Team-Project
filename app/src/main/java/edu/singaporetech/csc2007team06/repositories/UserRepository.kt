package edu.singaporetech.csc2007team06.repositories

import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.singaporetech.csc2007team06.utils.Resource
import edu.singaporetech.csc2007team06.utils.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository {
    private val db = Firebase.firestore
    val COLLECTION: String = "users"

    /**
     * Get users from users collection
     */
    suspend fun users(): Resource<QuerySnapshot> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = db.collection(COLLECTION).get().await()
                Resource.Success(result)
            }
        }
    }
}