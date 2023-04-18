package edu.singaporetech.csc2007team06.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.singaporetech.csc2007team06.databinding.ActivityEditNameBinding
import edu.singaporetech.csc2007team06.viewmodels.AuthViewModel

class EditNameActivity : BaseActivity() {
    private lateinit var binding: ActivityEditNameBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        firebaseAuth = Firebase.auth

        // Initialize ViewModel
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Set up text watcher for name input
        binding.nameInput.addTextChangedListener(textWatcher)

        // Set up back button
        binding.imageViewBack.setOnClickListener {
            finish()
        }

        // Set up sign up button
        binding.signUpButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            authViewModel.updateName(name)
            showToast("Name changed successfully.")
            finish()
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Check if name is empty
            val nameValidity = s.toString().isNotEmpty()

            if (nameValidity) {
                updateCreateButton(true)
            } else {
                updateCreateButton(false)
            }
        }
    }

    //update create button
    fun updateCreateButton(onOff: Boolean) {
        binding.signUpButton.isEnabled = onOff
        binding.signUpButton.isClickable = onOff
    }
}