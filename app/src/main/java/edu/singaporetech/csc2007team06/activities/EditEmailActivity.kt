package edu.singaporetech.csc2007team06.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.singaporetech.csc2007team06.databinding.ActivityEditEmailBinding
import edu.singaporetech.csc2007team06.viewmodels.AuthViewModel

class EditEmailActivity : BaseActivity() {
    private lateinit var binding: ActivityEditEmailBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authViewModel: AuthViewModel
    var emailValidity = false
    var passwordValidity = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        firebaseAuth = Firebase.auth

        // Initialize ViewModel
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Bind text change listeners to email and password input fields
        binding.emailInput.addTextChangedListener(emailTextWatcher)
        binding.passwordInput.addTextChangedListener(passwordTextWatcher)

        // Initialize back button
        binding.imageViewBack.setOnClickListener {
            finish()
        }

        // Initialize sign up button
        binding.signUpButton.setOnClickListener {

            // Get email from log on user
            val email: String = firebaseAuth.currentUser?.email.toString()

            val credential =
                EmailAuthProvider.getCredential(email, binding.passwordInput.text.toString())
            val newEmail = binding.emailInput.text.toString()

            // Reauthenticate user and update email
            firebaseAuth.currentUser?.reauthenticate(credential)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    firebaseAuth.currentUser?.updateEmail(newEmail)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            authViewModel.updateEmail(newEmail)
                            showToast("Email changed successfully.")
                            finish()
                        } else {
                            showToast("Email change failed.")
                        }
                    }
                } else {
                    showToast("Incorrect Password.")
                }
            }
        }
    }

    /**
     * TextWatcher for email input field
     */
    private val emailTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val email = s.toString()
            emailValidity = false

            // Check if email is valid
            if (email.matches(Regex("[a-zA-Z0-9._+-]+@[a-z]+\\.+[a-z]+"))) {
                emailValidity = true
            }

            // Check if both email and password are valid
            if (emailValidity && passwordValidity) {
                updateCreateButton(true)
            } else {
                updateCreateButton(false)
            }
        }
    }


    private val passwordTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Check if password is empty
            passwordValidity = s.toString().isNotEmpty()

            if (emailValidity && passwordValidity) {
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