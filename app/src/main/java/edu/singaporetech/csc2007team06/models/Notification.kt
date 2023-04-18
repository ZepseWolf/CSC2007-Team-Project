package edu.singaporetech.csc2007team06.models

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import edu.singaporetech.csc2007team06.utils.Constant
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Data class for notification Firestore document
 */
@Parcelize
data class Notification(
    var id: String? = null,
    var userId: String? = null,
    var category: Constant.NotificationCategory? = null,
    var equipmentId: String? = null,
    var equipmentModel: String? = null,
    var equipmentLabel: String? = null,
    var eventId: String? = null,
    var acknowledge: Boolean? = null,
    @ServerTimestamp
    var timestamp: Date? = null,
) : Parcelable