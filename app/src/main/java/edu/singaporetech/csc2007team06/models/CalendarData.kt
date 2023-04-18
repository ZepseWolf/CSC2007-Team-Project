package edu.singaporetech.csc2007team06.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class for calendar library
 */
@Parcelize
data class CalendarData(
    val tag_color: String? = null,
    val time: String? = null,
    val period: Int? = null,
    val title: String? = null,
    val action: String? = null,
    val description: String? = null,
    val last_location: String? = null,
    val date: String? = null,
) : Parcelable