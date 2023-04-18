package edu.singaporetech.csc2007team06

import edu.singaporetech.csc2007team06.utils.Constant
import edu.singaporetech.csc2007team06.utils.Constant.getEndoscopeEventCategory
import edu.singaporetech.csc2007team06.utils.Constant.getEventCategoryColor
import edu.singaporetech.csc2007team06.utils.Constant.getWasherEventCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class ConstantUnitTest {

    @Test
    fun assetGetEndoscopeEventCategory() {
        assertEquals(Constant.EventCategory.ENDOSCOPE_REPAIR, getEndoscopeEventCategory("Repair"))
        assertEquals(Constant.EventCategory.ENDOSCOPE_LOAN, getEndoscopeEventCategory("Loan"))
        assertEquals(Constant.EventCategory.ENDOSCOPE_SAMPLE, getEndoscopeEventCategory("Sample"))
    }

    @Test
    fun assetGetWasherEventCategory() {
        assertEquals(Constant.EventCategory.WASHER_SAMPLE, getWasherEventCategory("Sample"))
        assertEquals(Constant.EventCategory.WASHER_REPAIR, getWasherEventCategory("Repair"))
    }

    @Test
    fun assetGetEventCategoryColor() {
        assertEquals(R.color.orange, getEventCategoryColor(Constant.EventCategory.ENDOSCOPE_REPAIR))
        assertEquals(R.color.green, getEventCategoryColor(Constant.EventCategory.ENDOSCOPE_LOAN))
        assertEquals(R.color.blue, getEventCategoryColor(Constant.EventCategory.ENDOSCOPE_SAMPLE))
        assertEquals(R.color.blue, getEventCategoryColor(Constant.EventCategory.WASHER_SAMPLE))
        assertEquals(R.color.orange, getEventCategoryColor(Constant.EventCategory.WASHER_REPAIR))
    }
}