package edu.singaporetech.csc2007team06.utils

import edu.singaporetech.csc2007team06.R

object Constant {
    enum class EndoscopeStatus {
        REPAIR, SAMPLING, WASHING, LOAN, READY
    }

    enum class WasherStatus {
        REPAIR, SAMPLING, WASHING, READY
    }

    enum class NotificationCategory {

        // Created at edit equipment activities
        ENDOSCOPE_UPCOMING_REPAIR,
        ENDOSCOPE_UPCOMING_SAMPLE,
        WASHER_UPCOMING_SAMPLE,
        WASHER_UPCOMING_REPAIR,

        // Created at create schedule event activities
        ENDOSCOPE_REPAIR,
        ENDOSCOPE_LOAN,
        ENDOSCOPE_SAMPLE,
        WASHER_SAMPLE,
        WASHER_REPAIR,

        // Created at push notification script for schedule events
        ENDOSCOPE_RETURN_SAMPLE,
        ENDOSCOPE_RETURN_LOAN,
        ENDOSCOPE_RETURN_REPAIR,
        WASHER_RETURN_SAMPLE,
        WASHER_RETURN_REPAIR,
    }

    enum class EventCategory {
        ENDOSCOPE_REPAIR,
        ENDOSCOPE_LOAN,
        ENDOSCOPE_SAMPLE,
        WASHER_SAMPLE,
        WASHER_REPAIR,
    }

    val endoscopeEventsCategories = arrayOf(
        "Repair",
        "Loan",
        "Sample",
    )

    val washerEventCategories = arrayOf(
        "Sample",
        "Repair",
    )

    fun getEndoscopeEventCategory(category: String): EventCategory {
        return when (category) {
            "Repair" -> EventCategory.ENDOSCOPE_REPAIR
            "Loan" -> EventCategory.ENDOSCOPE_LOAN
            "Sample" -> EventCategory.ENDOSCOPE_SAMPLE
            else -> EventCategory.ENDOSCOPE_REPAIR
        }
    }

    fun getWasherEventCategory(category: String): EventCategory {
        return when (category) {
            "Sample" -> EventCategory.WASHER_SAMPLE
            "Repair" -> EventCategory.WASHER_REPAIR
            else -> EventCategory.ENDOSCOPE_REPAIR
        }
    }

    fun getEventCategoryColor(category: EventCategory): Int {
        // repair = orange
        // loan = green
        // sample = blue
        return when (category) {
            EventCategory.ENDOSCOPE_REPAIR -> {
                R.color.orange
            }
            EventCategory.ENDOSCOPE_LOAN -> {
                R.color.green
            }
            EventCategory.ENDOSCOPE_SAMPLE -> {
                R.color.blue
            }
            EventCategory.WASHER_SAMPLE -> {
                R.color.blue
            }
            EventCategory.WASHER_REPAIR -> {
                R.color.orange
            }
        }
    }
}