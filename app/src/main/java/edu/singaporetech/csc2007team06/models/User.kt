package edu.singaporetech.csc2007team06.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class for user Firestore document
 */
@Parcelize
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val fcmToken: List<String> = listOf(),
) : Parcelable