package edu.singaporetech.csc2007team06.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.google.android.material.textfield.TextInputEditText
import edu.singaporetech.csc2007team06.R
import edu.singaporetech.csc2007team06.databinding.ActivityNewEndoBinding
import edu.singaporetech.csc2007team06.models.Endoscope
import edu.singaporetech.csc2007team06.models.Event
import edu.singaporetech.csc2007team06.models.User
import edu.singaporetech.csc2007team06.utils.Constant
import edu.singaporetech.csc2007team06.utils.Resource
import edu.singaporetech.csc2007team06.viewmodels.EndoscopeViewModel
import edu.singaporetech.csc2007team06.viewmodels.ScheduleViewModel
import edu.singaporetech.csc2007team06.viewmodels.UserViewModel
import java.text.SimpleDateFormat
import java.util.*


class AddNewEnoActivity : MainBaseActivity() {
    private lateinit var binding: ActivityNewEndoBinding

    // view models
    private lateinit var userViewModel: UserViewModel
    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var endoscopeViewModel: EndoscopeViewModel

    private var event: Event = Event()
    private lateinit var users: List<User>
    private lateinit var endoscopes: List<Endoscope>
    private val TAG: String = "Endoscope Activity"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this@AddNewEnoActivity, R.layout.activity_new_endo)

        // init view models
        scheduleViewModel = ScheduleViewModel()
        userViewModel = UserViewModel()
        endoscopeViewModel = EndoscopeViewModel()

        // set up back button
        binding.imageViewBack.setOnClickListener {
            finish()
        }

        // set up spinner for categories
        val categoriesAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            Constant.endoscopeEventsCategories
        )
        binding.spinnerCategories.adapter = categoriesAdapter
        binding.spinnerCategories.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                event.category =
                    Constant.getEndoscopeEventCategory(Constant.endoscopeEventsCategories[position])
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        // select first category by default
        binding.spinnerCategories.setSelection(0)

        // set up item selected listener for endoscopes spinner
        binding.spinnerEndoscopes.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                // get endoscope by position and set id, model and label
                event.equipmentId = endoscopes[position].id
                event.equipmentModel = endoscopes[position].model
                event.equipmentLabel = endoscopes[position].label
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        //Gget endoscopes from database and set up spinner
        endoscopeViewModel.endoscopesStatus.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    Log.d(TAG, "Loading ... ")
                }
                is Resource.Success -> {
                    endoscopes = it.data!!
                    // get {model}-{label} of endoscopes and map to array
                    val nameArray = endoscopes.map { endoscope ->
                        endoscope.model + "-" + endoscope.label
                    }

                    val endoscopeNameAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        nameArray
                    )
                    binding.spinnerEndoscopes.adapter = endoscopeNameAdapter
                    // select first endoscope by default
                    binding.spinnerEndoscopes.setSelection(0)
                }
                is Resource.Error -> {
                    Log.e(TAG, it.message.toString())
                }
            }
        }

        // set up spinner for nurses
        binding.spinnerNurses.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                // get user by position and set id
                event.userId = users[position].id
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        // get users from database and set up spinner
        userViewModel.usersStatus.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    Log.d(TAG, "Loading ... ")
                }
                is Resource.Success -> {
                    users = it.data!!
                    // get name of users and map to array
                    val nameArray = users.map { user -> user.name }

                    val userNameAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        nameArray
                    )
                    binding.spinnerNurses.adapter = userNameAdapter
                    // select first user by default
                    binding.spinnerNurses.setSelection(0)
                }
                is Resource.Error -> {
                    Log.e(TAG, it.message.toString())
                }
            }
        }

        // Show date or time picker dialog when user click on the start or return date or time field
        binding.startDateField.setOnClickListener {
            showDatePickerDialog(binding.startDateField)
        }
        binding.startTimeField.setOnClickListener {
            showTimePickerDialog(binding.startTimeField)
        }
        binding.returnDateField.setOnClickListener {
            showDatePickerDialog(binding.returnDateField)
        }
        binding.returnTimeField.setOnClickListener {
            showTimePickerDialog(binding.returnTimeField)
        }

        // Submit event button
        binding.submitButton.setOnClickListener {
            // check if all fields are filled
            if (binding.startDateField.text.toString().isEmpty() ||
                binding.startTimeField.text.toString().isEmpty() ||
                binding.returnDateField.text.toString().isEmpty() ||
                binding.returnTimeField.text.toString().isEmpty()
            ) {
                showToast("Please fill in start and end date and time.")
                return@setOnClickListener
            }
            val startDate = binding.startDateField.text.toString()
            val startTime = binding.startTimeField.text.toString()
            val returnDate = binding.returnDateField.text.toString()
            val returnTime = binding.returnTimeField.text.toString()

            // parse start and return date and time
            val dateFormat_yyyyMMddHHmmss = SimpleDateFormat(
                "dd/MM/yyyy hh:mm a", Locale.getDefault()
            )
            val startDateTime = dateFormat_yyyyMMddHHmmss.parse("$startDate $startTime")
            val returnDateTime = dateFormat_yyyyMMddHHmmss.parse("$returnDate $returnTime")

            // check if start or return date is before today
            val today = Date()
            if (startDateTime!!.before(today) || returnDateTime!!.before(today)) {
                showToast("Start and return date must be after today.")
                return@setOnClickListener
            }

            // check if start date and time is before return date and time
            if (startDateTime.after(returnDateTime)) {
                showToast("Start date and time must be before return date and time.")
                return@setOnClickListener
            }

            event.startDate = startDateTime
            event.returnDate = returnDateTime
            event.hasNotifyStart = false
            event.hasNotifyReturn = false
            event.note = binding.editTextNote.text.toString()
            event.name =
                "Endoscope " + event.equipmentModel + "-" + event.equipmentLabel + " has " + binding.spinnerCategories.selectedItem.toString() + " event"
            scheduleViewModel.addEvent(event)
            scheduleViewModel.addEventStatus.observe(this) {
                when (it) {
                    is Resource.Loading -> {
                        // disable button to prevent multiple clicks
                        binding.submitButton.isEnabled = false
                        // set button text to loading
                        binding.submitButton.text = "Loading..."
                        Log.d(TAG, "Loading ... ")
                    }
                    is Resource.Success -> {
                        binding.submitButton.isEnabled = true
                        binding.submitButton.text = "ADD EVENT"
                        showToast("Event added successfully.")
                        finish()
                    }
                    is Resource.Error -> {
                        binding.submitButton.isEnabled = true
                        binding.submitButton.text = "ADD EVENT"
                        showToast("Failed to add event.")
                        Log.e(TAG, it.message.toString())
                    }
                }
            }
        }
    }


    /**
     * Helper function to show time picker dialog
     */
    private fun showTimePickerDialog(view: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog =
            TimePickerDialog(this, { _, hourSelected, minuteSelected ->
                calendar.set(Calendar.HOUR_OF_DAY, hourSelected)
                calendar.set(Calendar.MINUTE, minuteSelected)
                val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
                view.setText(format.format(calendar.time))
            }, hour, minute, false)
        timePickerDialog.show()
    }


    /**
     * Helper function to show date picker dialog
     */
    private fun showDatePickerDialog(view: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(this, { _, yearSelected, monthSelected, daySelected ->
                calendar.set(Calendar.YEAR, yearSelected)
                calendar.set(Calendar.MONTH, monthSelected)
                calendar.set(Calendar.DAY_OF_MONTH, daySelected)
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                view.setText(format.format(calendar.time))
            }, year, month, day)
        datePickerDialog.show()
    }
}

