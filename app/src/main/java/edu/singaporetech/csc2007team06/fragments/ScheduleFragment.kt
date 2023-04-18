package edu.singaporetech.csc2007team06.fragments

import CalendarAdapter
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.text.style.LineBackgroundSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import edu.singaporetech.csc2007team06.R
import edu.singaporetech.csc2007team06.activities.AddNewEnoActivity
import edu.singaporetech.csc2007team06.activities.AddNewWasherActivity
import edu.singaporetech.csc2007team06.models.Event
import edu.singaporetech.csc2007team06.utils.Constant
import edu.singaporetech.csc2007team06.utils.Resource
import edu.singaporetech.csc2007team06.viewmodels.ScheduleViewModel
import java.text.SimpleDateFormat
import java.util.*


/**
 * MultiDotSpan is a span that draws multiple dots on the line.
 */
class MultiDotSpan(
    private val radius: Float = 3f,
    private val colors: List<Int>,
) : LineBackgroundSpan {

    override fun drawBackground(
        canvas: Canvas, paint: Paint,
        left: Int, right: Int, top: Int, baseline: Int, bottom: Int,
        charSequence: CharSequence,
        start: Int, end: Int, lineNum: Int,
    ) {

        // Calculate the position of the dots
        var position = (left + right) / 2 - (colors.size - 1) * radius

        // If there are more than 1 dots, shift the position to the left
        if (colors.size > 1) {
            position - 2
        }

        // Draw the dots
        colors.forEach { color ->
            val oldColor = paint.color
            if (color != 0) {
                paint.color = color
            }
            canvas.drawCircle(position, bottom + radius + 24, radius, paint)
            paint.color = oldColor

            position += radius * 2 + 6 // +2 to +6
        }
    }
}

/**
 * EventDecorator is a decorator that draws a dot on the line.
 */
class EventDecorator(
    private val spanColors: List<Int>?,
    private val fontColor: Int?,
    private var dates: List<CalendarDay> = emptyList(),
) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay) = dates.contains(day)

    override fun decorate(view: DayViewFacade) = with(view) {
        if (spanColors != null) {
            addSpan(MultiDotSpan(8f, spanColors))
        }

        if (fontColor != null) {
            addSpan(ForegroundColorSpan(fontColor))
        }

    }
}

class ScheduleFragment : BaseFragment() {
    private val TAG: String = "Schedule fragment: "
    private var views: View? = null
    private var arrCal: ArrayList<Event> = ArrayList()
    private lateinit var viewModel: ScheduleViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = views?.findViewById<RecyclerView>(R.id.schedule_view)!!
        val calendarView = views?.findViewById<MaterialCalendarView>(R.id.calendar_view)!!

        // Update recycler view when date is selected
        viewModel.eventsByDateStatus.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val data = it.data!!
                    arrCal = ArrayList(data)
                    val adapter = CalendarAdapter(data)
                    adapter.setOnDeleteClickListener(object : CalendarAdapter.OnClickListener {
                        override fun onClick(position: Int, event: Event) {
                            viewModel.deleteEventById(event.id!!)
                            viewModel.events()
                        }
                    })
                    recyclerView.adapter = adapter
                }
                else -> {
                    Log.d(TAG, "Error , data in view model was not properly observed")
                }
            }
        }

        // Update calendar view when events are updated
        viewModel.eventsStatus.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    calendarView.removeDecorators()
                    // Reset dots
                    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                    val groupedDocuments = mutableMapOf<String, MutableList<Event>>()
                    val events = it.data!!

                    // Group the documents by their start and return dates
                    for (event in events) {
                        // convert the Date to a String in the format "yyyyMMdd"
                        val formattedStartDate = dateFormat.format(event.startDate!!)
                        val formattedReturnDate = dateFormat.format(event.returnDate!!)

                        // Add the document to the list of documents for the start date
                        if (groupedDocuments[formattedStartDate] == null) {
                            groupedDocuments[formattedStartDate] = mutableListOf(event)
                        } else {
                            groupedDocuments[formattedStartDate]?.add(event)
                        }

                        // Add the document to the list of documents for the return date
                        if (groupedDocuments[formattedReturnDate] == null) {
                            groupedDocuments[formattedReturnDate] = mutableListOf(event)
                        } else {
                            groupedDocuments[formattedReturnDate]?.add(event)
                        }
                    }

                    for ((day, values) in groupedDocuments.entries) {
                        val selectedDate = convertDate(day)
                        val colors =
                            values.map { resources.getColor(Constant.getEventCategoryColor(it.category!!)) }
                        val dot = EventDecorator(colors, null, selectedDate)
                        calendarView.addDecorator(dot)
                    }
                }
                else -> {
                    Log.d(TAG, "Error , data in view model was not properly observed")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        views = inflater.inflate(R.layout.fragment_schedule, container, false)
        val calendarView = views?.findViewById<MaterialCalendarView>(R.id.calendar_view)!!

        // Set up Recycler view
        val llm = LinearLayoutManager(activity)
        llm.orientation = LinearLayoutManager.VERTICAL
        val recyclerView = views?.findViewById<RecyclerView>(R.id.schedule_view)
        recyclerView?.layoutManager = llm
        recyclerView?.setHasFixedSize(true)

        // Set Up Saved instance generation
        val data = savedInstanceState?.getSerializable("currentCal")
        if (data != null) {
            arrCal = data as ArrayList<Event>
            recyclerView?.adapter = activity?.let { CalendarAdapter(arrCal) }
        }

        // Interaction UI control
        calendarView.setOnDateChangedListener { widget, date, selected ->
            arrCal = ArrayList()
            val calendar = Calendar.getInstance() // Get the current calendar instance
            calendar.set(date.year, date.month - 1, date.day)
            calendar.set(Calendar.HOUR_OF_DAY, 0) // Set the hour to 0 (midnight)
            calendar.set(Calendar.MINUTE, 0) // Set the minute to 0
            calendar.set(Calendar.SECOND, 0) // Set the second to 0
            calendar.set(
                Calendar.MILLISECOND,
                0
            ) // Convert to Unix timestamp (in seconds)

            viewModel.eventsByDate(calendar)
            val today: CalendarDay = CalendarDay.today()
            if (today == date) {
                calendarView.addDecorator(EventDecorator(null, Color.WHITE, listOf(today)))
            } else {
                calendarView.addDecorator(EventDecorator(null, Color.BLUE, listOf(today)))
            }
        }

        // Select today's date
        val today: CalendarDay = CalendarDay.today()
        calendarView.setDateSelected(today, true)

        // Set up click listeners for add item button
        views?.findViewById<ImageView>(R.id.add_item)?.setOnClickListener {
            openWindow(views)
        }

        // Set up click listeners for pop up drawer
        views?.findViewById<FrameLayout>(R.id.bg_taint)?.setOnClickListener {
            closeWindow(views)
        }

        // Set up click add endoscope schedule button
        views?.findViewById<Button>(R.id.endo_button)?.setOnClickListener {
            endoClick()
            closeWindow(views)
        }

        // Set up click add washer schedule button
        views?.findViewById<Button>(R.id.washer_button)?.setOnClickListener {
            washClick()
            closeWindow(views)
        }

        viewModel = ScheduleViewModel()

        return views
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (arrCal.size > 0) {
            Log.d("to check  ss", arrCal[0].toString())
        }
        outState.putSerializable("currentCal", arrCal)
    }

    override fun onResume() {
        super.onResume()
        viewModel.events()
    }


    /**
     * Open the pop up drawer
     */
    private fun openWindow(views: View?) {
        val backgroundTaint = views?.findViewById<FrameLayout>(R.id.bg_taint)
        val popOut = views?.findViewById<FrameLayout>(R.id.bottom_popout)
        backgroundTaint?.visibility = View.VISIBLE
        popOut?.visibility = View.VISIBLE
    }

    /**
     * Close the pop up drawer
     */
    private fun closeWindow(views: View?) {
        val backgroundTaint = views?.findViewById<FrameLayout>(R.id.bg_taint)
        val popOut = views?.findViewById<FrameLayout>(R.id.bottom_popout)
        backgroundTaint?.visibility = View.INVISIBLE
        popOut?.visibility = View.INVISIBLE
    }

    /**
     * Start the add new endoscope activity
     */
    private fun endoClick() {
        val intent = Intent(context, AddNewEnoActivity::class.java)
        intent.putExtra("myData", "Hello World!")
        startActivity(intent)
    }

    /**
     * Start the add new washer activity
     */
    private fun washClick() {
        val intent = Intent(context, AddNewWasherActivity::class.java)
        intent.putExtra("myData", "Hello World!")
        startActivity(intent)
    }

    /**
     * Convert a date string to a CalendarDay object
     */
    private fun convertDate(date: String): List<CalendarDay> {
        return listOf(
            CalendarDay.from(
                date.substring(0, 4).toInt(),
                date.substring(4, 6).toInt(),
                date.substring(6, 8).toInt()
            )
        )
    }
}