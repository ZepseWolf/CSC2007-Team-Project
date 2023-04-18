package edu.singaporetech.csc2007team06.repositories

import com.google.firebase.Timestamp
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.singaporetech.csc2007team06.models.Event
import edu.singaporetech.csc2007team06.utils.Resource
import edu.singaporetech.csc2007team06.utils.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class ScheduleRepository {
    private val db = Firebase.firestore
    private val COLLECTION: String = "events"
    private val TAG: String = "Schedule Repository: "

    suspend fun addEvent(event: Event) {
        return withContext(Dispatchers.IO) {
            safeCall {
                val id = db.collection(COLLECTION).document().id
                event.id = id
                val callback = db.collection(COLLECTION).document(id).set(event)
                Resource.Success(callback)
            }
        }
    }

    suspend fun events(): Resource<QuerySnapshot> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = db.collection(COLLECTION).get().await()
                Resource.Success(result)
            }
        }
    }

    suspend fun deleteEventById(id: String) {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = db.collection(COLLECTION).document(id).delete()
                Resource.Success(result)
            }
        }
    }

    suspend fun eventsByStartDate(date: Calendar): Resource<QuerySnapshot> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val dayEnd = date.clone() as Calendar
                dayEnd.set(Calendar.HOUR_OF_DAY, 23) // Set the hour to 0 (midnight)
                dayEnd.set(Calendar.MINUTE, 59) // Set the minute to 0
                dayEnd.set(Calendar.SECOND, 59) // Set the second to 0
                dayEnd.set(Calendar.MILLISECOND, 0)
                val result = db.collection(COLLECTION)
                    .whereGreaterThanOrEqualTo("startDate", Timestamp(date.time))
                    .whereLessThanOrEqualTo("startDate", Timestamp(dayEnd.time)).get().await()
                Resource.Success(result)
            }
        }
    }

    suspend fun eventsByReturnDate(date: Calendar): Resource<QuerySnapshot> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val dayEnd = date.clone() as Calendar
                dayEnd.set(Calendar.HOUR_OF_DAY, 23) // Set the hour to 0 (midnight)
                dayEnd.set(Calendar.MINUTE, 59) // Set the minute to 0
                dayEnd.set(Calendar.SECOND, 59) // Set the second to 0
                dayEnd.set(Calendar.MILLISECOND, 0)
                val result = db.collection(COLLECTION)
                    .whereGreaterThanOrEqualTo("returnDate", Timestamp(date.time))
                    .whereLessThanOrEqualTo("returnDate", Timestamp(dayEnd.time)).get().await()
                Resource.Success(result)
            }
        }
    }
}