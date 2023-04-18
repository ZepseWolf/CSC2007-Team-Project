package edu.singaporetech.csc2007team06.models

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import edu.singaporetech.csc2007team06.utils.Constant
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Data class for event Firestore document
 */
@Parcelize
data class Event(
    var id: String? = null,
    var userId: String? = null,
    var category: Constant.EventCategory? = null,
    var equipmentId: String? = null,
    var equipmentLabel: String? = null,
    var equipmentModel: String? = null,
    @ServerTimestamp
    var startDate: Date? = null,
    @ServerTimestamp
    var returnDate: Date? = null,
    var note: String? = null,
    var hasNotifyStart: Boolean? = null,
    var hasNotifyReturn: Boolean? = null,
    var name: String? = null,
) : Parcelable

