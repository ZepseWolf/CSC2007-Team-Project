package edu.singaporetech.csc2007team06.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import edu.singaporetech.csc2007team06.R
import edu.singaporetech.csc2007team06.models.Endoscope
import edu.singaporetech.csc2007team06.models.User
import edu.singaporetech.csc2007team06.models.Washer
import edu.singaporetech.csc2007team06.utils.Resource
import edu.singaporetech.csc2007team06.viewmodels.EndoscopeViewModel
import edu.singaporetech.csc2007team06.viewmodels.UserViewModel
import edu.singaporetech.csc2007team06.viewmodels.WasherViewModel
import java.text.SimpleDateFormat
import java.util.*


class EditEquipmentActivity : MainBaseActivity() {
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    private var washer: Washer? = null
    private var endoscope: Endoscope? = null
    private var users: List<User>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_equipment)

        // Initialise the text fields
        val editTextSample = findViewById<EditText>(R.id.editTextSample)
        val imageViewEditEquipment = findViewById<ImageView>(R.id.imageViewEditEquipment)
        val editTextRepair = findViewById<EditText>(R.id.editTextRepair)
        val textViewLabel = findViewById<TextView>(R.id.textViewEndoscopeLabel)
        val textViewModelNo = findViewById<TextView>(R.id.textViewModelNo)
        val spinnerNurses = findViewById<Spinner>(R.id.spinnerNurses)
        val editTextNote = findViewById<EditText>(R.id.editTextNote)
        val textViewPastHistory = findViewById<TextView>(R.id.textViewPastHistory)
        val iconEdit = findViewById<ImageView>(R.id.iconEdit)
        val iconSave = findViewById<ImageView>(R.id.iconSave)
        val iconRepair = findViewById<ImageView>(R.id.iconCalendar_Repair)
        val iconWasher = findViewById<ImageView>(R.id.iconCalendar_Wash)
        val imageViewBack = findViewById<ImageView>(R.id.imageViewBack)
        val textViewTitle = findViewById<TextView>(R.id.textViewTitle)

        // Initialise the view models
        val endoscopeViewModel = ViewModelProvider(this)[EndoscopeViewModel::class.java]
        val washerViewModel = ViewModelProvider(this)[WasherViewModel::class.java]
        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // set up back button
        imageViewBack.setOnClickListener {
            finish()
        }

        // Set Past History link button to be underlined
        val pastHistorySpannableString = SpannableString(textViewPastHistory.text)
        pastHistorySpannableString.setSpan(
            UnderlineSpan(),
            0,
            pastHistorySpannableString.length,
            0
        )
        textViewPastHistory.text = pastHistorySpannableString


        // Get users to populate the spinner
        userViewModel.usersStatus.observe(this) {
            when (it) {
                is Resource.Success -> {
                    users = it.data!!
                    val userNames = users!!.map { user -> user.name }
                    // Create an adapter for the spinner
                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        userNames
                    )
                    // Set the layout for the dropdown menu
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerNurses.adapter = adapter
                    // Set the spinner dropdown layout to appear at the bottom
                    spinnerNurses.setDropDownVerticalOffset(100)

                    // Set spinnerNIC to the correct name
                    if (washer != null) {
                        spinnerNurses.setSelection(userNames.indexOf(washer!!.userName.toString()))
                    } else if (endoscope != null) {
                        spinnerNurses.setSelection(userNames.indexOf(endoscope!!.userName.toString()))
                    }
                }
                else -> {}
            }
        }


        // If Washer is selected, populate the text fields with the data from the previous activity
        // If Endoscope is selected, populate the text fields with the data from the previous activity
        val washerId = intent.getStringExtra("washerId")
        val endoscopeId = intent.getStringExtra("endoscopeId")


        // load washer or endoscope data
        if (washerId != null) {
            washerViewModel.washer(washerId)
            washerViewModel.washerStatus.observe(this) {
                when (it) {
                    is Resource.Success -> {
                        washer = it.data
                        if (washer != null) {
                            textViewTitle.text = washer!!.label
                            // Washer data
                            // Populate the text fields with the data from the previous activity
                            // Set the text fields with the data from the previous activity
                            editTextSample.setText(dateFormatter.format(washer!!.upcomingSample?.time))
                            editTextRepair.setText(
                                dateFormatter.format(washer!!.upcomingRepair?.time)
                            )
                            if (users != null) {
                                spinnerNurses.setSelection(users!!.map { user -> user.name }
                                    .indexOf(washer!!.userName.toString()))
                            }
                            textViewLabel.text = washer!!.label.toString()
                            textViewModelNo.text = washer!!.model.toString()
                            editTextNote.setText(washer!!.note.toString())
                            imageViewEditEquipment.setImageResource(R.drawable.washer)
                        }

                        // If textViewPastHistory is clicked, go to the Past History activity
                        textViewPastHistory.setOnClickListener {
                            val intent = Intent(this, ViewHistoryActivity::class.java)
                            intent.putExtra("washerId", washerId)
                            startActivity(intent)
                        }
                    }
                    else -> {}
                }
            }
        } else if (endoscopeId != null) {
            endoscopeViewModel.endoscope(endoscopeId)
            endoscopeViewModel.endoscopeStatus.observe(this) {
                when (it) {
                    is Resource.Success -> {
                        endoscope = it.data
                        if (endoscope != null) {
                            textViewTitle.text = endoscope!!.label
                            // Endoscopes data
                            // Populate the text fields with the data from the previous activity
                            // Set the text fields with the data from the previous activity
                            editTextSample.setText(dateFormatter.format(endoscope!!.upcomingSample?.time))
                            editTextRepair.setText(
                                dateFormatter.format(endoscope!!.upcomingRepair?.time)
                            )
                            if (users != null) {
                                spinnerNurses.setSelection(users!!.map { user -> user.name }
                                    .indexOf(endoscope!!.userName.toString()))
                            }
                            textViewLabel.text = endoscope!!.label.toString()
                            textViewModelNo.text = endoscope!!.model.toString()
                            editTextNote.setText(endoscope!!.note.toString())
                            imageViewEditEquipment.setImageResource(R.drawable.endoscope)
                        }
                        // If textViewPastHistory is clicked, go to the Past History activity
                        textViewPastHistory.setOnClickListener {
                            val intent = Intent(this, ViewHistoryActivity::class.java)
                            intent.putExtra("endoscopeId", endoscopeId)
                            startActivity(intent)
                        }
                    }
                    else -> {}
                }
            }
        }

        // Disable the washer and repair date fields
        iconWasher.isEnabled = false
        iconRepair.isEnabled = false

        // Set up click listeners for icon on the washer and repair date fields
        iconWasher.setOnClickListener {
            openDatePickerDialog(editTextSample)
        }
        iconRepair.setOnClickListener {
            openDatePickerDialog(editTextRepair)
        }

        // Disable the nurse in charge field
        spinnerNurses.isEnabled = false
        spinnerNurses.isClickable = false

        iconEdit.setOnClickListener {
            // Don't show the edit icon and show the save icon
            iconEdit.visibility = ImageView.INVISIBLE
            iconSave.visibility = ImageView.VISIBLE

            // Enable the calendar icons
            iconWasher.isEnabled = true
            iconRepair.isEnabled = true

            // Enable the text fields
            editTextSample.isEnabled = true
            editTextRepair.isEnabled = true
            spinnerNurses.isEnabled = true
            spinnerNurses.isClickable = true
            editTextNote.isEnabled = true

            // Enable the NIC and AN
            editTextNote.requestFocus()

        }
        // If iconSave is clicked, save the data
        iconSave.setOnClickListener {
            // Don't show the save icon and show the edit icon
            iconSave.visibility = ImageView.INVISIBLE
            iconEdit.visibility = ImageView.VISIBLE

            // Disable the calendar icons
            iconWasher.isEnabled = false
            iconRepair.isEnabled = false

            // Disable the text fields
            editTextSample.isEnabled = false
            editTextRepair.isEnabled = false
            spinnerNurses.isEnabled = false
            spinnerNurses.isClickable = false
            editTextNote.isEnabled = false
            val selectedUser =
                users!!.find { user -> user.name == spinnerNurses.selectedItem.toString() }

            // If Washer is selected, save the data
            // If Endoscope is selected, save the data
            if (washer != null) {
                // check if upcomingSample has been changed
                if (washer!!.upcomingSample != dateFormatter.parse(editTextSample.text.toString())) {
                    washer!!.upcomingSample = dateFormatter.parse(editTextSample.text.toString())
                    washer!!.hasNotifySample = false
                }

                // check if upcomingRepair has been changed
                if (washer!!.upcomingRepair != dateFormatter.parse(editTextRepair.text.toString())) {
                    washer!!.upcomingRepair = dateFormatter.parse(editTextRepair.text.toString())
                    washer!!.hasNotifyRepair = false
                }

                washer!!.userId = selectedUser!!.id
                washer!!.userName = selectedUser.name
                washer!!.note = editTextNote.text.toString()

                // Save the data to the database
                washerViewModel.updateWasher(washer!!)

                // Set Toast message
                showToast("Washer data Updated")
            } else if (endoscope != null) {
                // check if upcomingSample has been changed
                if (endoscope!!.upcomingSample != dateFormatter.parse(editTextSample.text.toString())) {
                    endoscope!!.upcomingSample = dateFormatter.parse(editTextSample.text.toString())
                    endoscope!!.hasNotifySample = false
                }

                // check if upcomingRepair has been changed
                if (endoscope!!.upcomingRepair != dateFormatter.parse(editTextRepair.text.toString())) {
                    endoscope!!.upcomingRepair = dateFormatter.parse(editTextRepair.text.toString())
                    endoscope!!.hasNotifyRepair = false
                }

                endoscope!!.userId = selectedUser!!.id
                endoscope!!.userName = selectedUser.name
                endoscope!!.note = editTextNote.text.toString()

                // Save the data to the database
                endoscopeViewModel.updateEndoscope(endoscope!!)

                // Set Toast message
                showToast("Endoscope data Updated")
            }
        }
    }

    // Open the date picker dialog
    private fun openDatePickerDialog(textView: TextView) {
        val newCalendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val newDate = Calendar.getInstance()
                newDate.set(year, month, dayOfMonth)
                textView.text = dateFormatter.format(newDate.time)
            },
            newCalendar.get(Calendar.YEAR),
            newCalendar.get(Calendar.MONTH),
            newCalendar.get(Calendar.DAY_OF_MONTH)
        )
        // Set the minimum date to today
        // - 1000 is for delay
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

}