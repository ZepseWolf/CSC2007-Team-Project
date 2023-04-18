package edu.singaporetech.csc2007team06.models

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import edu.singaporetech.csc2007team06.utils.Constant
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Data class for washer Firestore document
 */
@Parcelize
data class Washer(
    var id: String? = null,
    var status: Constant.WasherStatus? = null,
    var label: String? = null,
    var model: String? = null,
    var userId: String? = null,
    var userName: String? = null,
    var note: String? = null,
    @ServerTimestamp
    var upcomingSample: Date? = null,
    @ServerTimestamp
    var upcomingRepair: Date? = null,
    var hasNotifyRepair: Boolean? = null,
    var hasNotifySample: Boolean? = null,
    var history: List<History>? = null,
) : Parcelable