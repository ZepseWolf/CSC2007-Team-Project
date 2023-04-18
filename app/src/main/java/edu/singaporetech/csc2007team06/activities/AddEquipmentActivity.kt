package edu.singaporetech.csc2007team06.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import edu.singaporetech.csc2007team06.R
import edu.singaporetech.csc2007team06.databinding.ActivityAddEquipmentBinding
import edu.singaporetech.csc2007team06.models.Endoscope
import edu.singaporetech.csc2007team06.models.User
import edu.singaporetech.csc2007team06.models.Washer
import edu.singaporetech.csc2007team06.utils.Constant
import edu.singaporetech.csc2007team06.utils.Resource
import edu.singaporetech.csc2007team06.viewmodels.AuthViewModel
import edu.singaporetech.csc2007team06.viewmodels.EndoscopeViewModel
import edu.singaporetech.csc2007team06.viewmodels.UserViewModel
import edu.singaporetech.csc2007team06.viewmodels.WasherViewModel

class AddEquipmentActivity : MainBaseActivity() {
    private lateinit var binding: ActivityAddEquipmentBinding
    private lateinit var washer: Washer
    private lateinit var endoscope: Endoscope
    private lateinit var userViewModel: UserViewModel
    private lateinit var authViewModel: AuthViewModel
    private var labelFilled = false
    private var modelFilled = false

    private lateinit var fieldSelected: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_equipment)

        // set up view models
        val endoscopeViewModel = ViewModelProvider(this)[EndoscopeViewModel::class.java]
        val washerViewModel = ViewModelProvider(this)[WasherViewModel::class.java]
        authViewModel = AuthViewModel()
        userViewModel = UserViewModel()

        val equipmentType = intent.getStringExtra("EQUIPMENT_TYPE")
        var selectedItem: String? = null
        var users: List<User>? = null
        var currentName: String? = null


        // set up title
        binding.titleTextView.text = "Add " + equipmentType.toString()
        binding.addEquipmentBtn.text = "Add " + equipmentType.toString()

        // set up back button
        binding.imageViewBack.setOnClickListener {
            finish()
        }

        // Update label input form validity
        binding.labelEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val pattern = Regex("^\\d{2,}[a-zA-Z]\$")
                if (s != null && s.matches(pattern)) {
                    labelFilled = true
                    binding.labelErrorTextView.isVisible = false
                } else {
                    binding.labelErrorTextView.text =
                        "Label must have at least 2 numbers and a character"
                    binding.labelErrorTextView.isVisible = true
                }
                checkFormValidity()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        //
        // Uupdate model input form validity
        binding.modelEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length >= 8) {
                    modelFilled = true
                    binding.modelErrorTextView.isVisible = false
                } else {
                    binding.modelErrorTextView.text = "Model must have at least 8 characters"
                    binding.modelErrorTextView.isVisible = true
                }
                checkFormValidity()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        // Change equipment status spinner
        binding.statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                selectedItem = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        // Get current user data
        authViewModel.user()
        authViewModel.userStatus.observe(this) {
            when (it) {
                is Resource.Success -> {
                    currentName = it.data?.name.toString()
                }
                else -> {}
            }
        }

        // Bind users to select staff spinner
        userViewModel.usersStatus.observe(this) {
            when (it) {
                is Resource.Success -> {
                    users = it.data!!
                    val userNames = mutableListOf<String>().apply {
                        addAll(users!!.map { user -> user.name })
                    }
                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        userNames
                    )
                    // Set the layout for the dropdown menu
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.staffSpinner.adapter = adapter
                    // Set the spinner dropdown layout to appear at the bottom
                    binding.staffSpinner.setDropDownVerticalOffset(100)
                    binding.staffSpinner.setSelection(userNames.indexOf(currentName.toString()))
                    Log.d("AEA", userNames.indexOf(currentName.toString()).toString())
                    Log.d("AEA", currentName.toString())
                    //selectedUser = users!![userNames.indexOf(currentName.toString())]
                }
                else -> {}
            }
        }

        // Submit button
        binding.addEquipmentBtn.setOnClickListener {
            if (equipmentType == "Washer") {
                // Get last washer ID
                var lastWasherId: Int? = 0
                washerViewModel.washersStatus.observe(this) {
                    when (it) {
                        is Resource.Success -> {
                            lastWasherId = it.data?.last()?.id?.toInt()
                        }
                        else -> {}
                    }
                }

                washer = Washer()
                washer.label = binding.labelEditText.text.toString()
                washer.model = binding.modelEditText.text.toString()
                washer.note = binding.noteEditText.text.toString()
                when (selectedItem) {
                    Constant.WasherStatus.READY.toString() -> washer.status =
                        Constant.WasherStatus.READY
                    Constant.WasherStatus.REPAIR.toString() -> washer.status =
                        Constant.WasherStatus.REPAIR
                    Constant.WasherStatus.SAMPLING.toString() -> washer.status =
                        Constant.WasherStatus.SAMPLING
                    Constant.WasherStatus.WASHING.toString() -> washer.status =
                        Constant.WasherStatus.WASHING
                }
                val selectedUser =
                    users!!.find { user -> user.name == binding.staffSpinner.selectedItem.toString() }
                washer.userId = selectedUser!!.id
                washer.userName = selectedUser.name
                washer.id = (lastWasherId?.plus(1)).toString()
                washer.hasNotifyRepair = true
                washer.hasNotifySample = true
                washerViewModel.addWasher(washer)
                showToast("Washer Added!")
                finish()
            } else if (equipmentType == "Endoscope") {

                // Get last endoscope ID
                var lastEndoscopeId: Int? = 0
                endoscopeViewModel.endoscopesStatus.observe(this) {
                    when (it) {
                        is Resource.Success -> {
                            lastEndoscopeId = it.data?.last()?.id?.toInt()
                        }
                        else -> {}
                    }
                }

                endoscope = Endoscope()
                endoscope.label = binding.labelEditText.text.toString()
                endoscope.model = binding.modelEditText.text.toString()
                endoscope.note = binding.noteEditText.text.toString()

                when (selectedItem) {
                    Constant.EndoscopeStatus.READY.toString() -> endoscope.status =
                        Constant.EndoscopeStatus.READY
                    Constant.EndoscopeStatus.REPAIR.toString() -> endoscope.status =
                        Constant.EndoscopeStatus.REPAIR
                    Constant.EndoscopeStatus.SAMPLING.toString() -> endoscope.status =
                        Constant.EndoscopeStatus.SAMPLING
                    Constant.EndoscopeStatus.WASHING.toString() -> endoscope.status =
                        Constant.EndoscopeStatus.WASHING
                }

                val selectedUser =
                    users!!.find { user -> user.name == binding.staffSpinner.selectedItem.toString() }
                endoscope.userId = selectedUser!!.id
                endoscope.userName = selectedUser!!.name
                endoscope.hasNotifySample = true
                endoscope.hasNotifyRepair = true
                endoscope.id = (lastEndoscopeId?.plus(1)).toString()
                endoscopeViewModel.addEndoscope(endoscope)
                showToast("Endoscope Added!")
                finish()
            }
        }

        // Launch QR/barcode scanner when clicking on the icon
        binding.labelTextField.setEndIconOnClickListener {
            val options = ScanOptions().setOrientationLocked(false)
            fieldSelected = "label"
            barcodeLauncher.launch(options)
        }

        // Launch QR/barcode scanner when clicking on the icon
        binding.modelTextField.setEndIconOnClickListener {
            val options = ScanOptions().setOrientationLocked(false)
            fieldSelected = "model"
            barcodeLauncher.launch(options)
        }
    }

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents != null) {

            // Set the text of the field to the scanned result
            if (fieldSelected == "label") {
                binding.labelEditText.setText(result.contents.trim())
            } else if (fieldSelected == "model") {
                binding.modelEditText.setText(result.contents.trim())
            }
        }
    }

    /**
     * Check if the form is valid
     * and enable/disable the submit button
     */
    private fun checkFormValidity() {
        if (labelFilled && modelFilled) {
            binding.addEquipmentBtn.isEnabled = true
            binding.addEquipmentBtn.isClickable = true
        } else {
            binding.addEquipmentBtn.isEnabled = false
            binding.addEquipmentBtn.isClickable = false
        }
    }
}

