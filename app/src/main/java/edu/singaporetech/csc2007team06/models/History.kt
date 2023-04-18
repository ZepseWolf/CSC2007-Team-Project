package edu.singaporetech.csc2007team06.models

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*


/**
 * Data class for endoscope and washer Firestore sub-document
 */
@Parcelize
data class History(
    var description: String? = null,
    @ServerTimestamp
    var createdAt: Date? = null,
) : Parcelable

