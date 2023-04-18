package edu.singaporetech.csc2007team06.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.singaporetech.csc2007team06.R
import edu.singaporetech.csc2007team06.databinding.ActivityCreateAccountBinding
import edu.singaporetech.csc2007team06.utils.Resource
import edu.singaporetech.csc2007team06.utils.TestIdlingResource
import edu.singaporetech.csc2007team06.viewmodels.AuthViewModel

class CreateAccountActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateAccountBinding;
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authViewModel: AuthViewModel
    private val TAG = "CreateAccountActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this@CreateAccountActivity,
            R.layout.activity_create_account
        )

        // Initialize the AuthViewModel
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Initialize Firebase Auth
        firebaseAuth = Firebase.auth

        // Set up the text watcher for the password input
        binding.passwordInput.addTextChangedListener(textWatcher)

        // Set up the click listener for the sign up button
        binding.signUpButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            TestIdlingResource.increment() // Set app as busy. This is for testing purposes.
            registerUser(email, password, name)
        }
    }

    /**
     * Helper function to register a user with the given email and password
     */
    private fun registerUser(email: String, password: String, name: String) {

        // Call the registerUser function in the AuthViewModel
        authViewModel.registerUser(email, password, name)

        // Observe the userRegistrationStatus LiveData
        authViewModel.userRegistrationStatus.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    shouldEnableButton(false)
                }
                is Resource.Success -> {
                    Log.d(TAG, "registerUser: ${it.data}")
                    showToast("Account created successfully.")
                    TestIdlingResource.decrement() // Set app as idle. This is for testing purposes.
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    shouldEnableButton(true)
                }
                is Resource.Error -> {
                    Log.e(TAG, it.message.toString())
                    TestIdlingResource.decrement() // Set app as idle. This is for testing purposes.
                    showToast(it.message.toString())
                    shouldEnableButton(true)
                }
            }
        }
    }

    /**
     * TextWatcher for the password input
     * and update the UI accordingly
     */
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val passwordString = s.toString()
            var lengthRequirement = false
            var bothUpperLower = false
            var symbols = false

            if (passwordString.length > 7) {
                updateTintAndColor(binding.error8Char, true)
                lengthRequirement = true
            } else {
                updateTintAndColor(binding.error8Char, false)
            }

            // if the password contains both upper and lower case letters
            if (passwordString.matches(Regex(".*[a-z].*")) && passwordString.matches(Regex(".*[A-Z].*"))) {
                updateTintAndColor(binding.errorUpperLower, true)
                bothUpperLower = true
            } else {
                updateTintAndColor(binding.errorUpperLower, false)
            }

            // if the password contains at least one symbol or number
            if (passwordString.matches(Regex("(.*\\W.*)")) || passwordString.matches(Regex(".*[0-9].*"))) {
                updateTintAndColor(binding.errorNumSymbol, true)
                symbols = true
            } else {
                updateTintAndColor(binding.errorNumSymbol, false)
            }

            // if all the requirements are met, enable the button
            if (lengthRequirement && bothUpperLower && symbols) {
                updateCreateButton(true)
            } else {
                updateCreateButton(false)
            }
        }
    }


    /**
     * Helper function to update the create button
     */
    private fun shouldEnableButton(enabled: Boolean) {
        if (enabled) {
            binding.signUpButton.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_right,
                0
            )
            binding.signUpButton.isEnabled = true
            binding.signUpButton.text = getString(R.string.sign_up)
        } else {
            // hide drawable icon on the right of the button
            // and set the button to be disabled
            // and set the button text to signing up...
            binding.signUpButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            binding.signUpButton.isEnabled = false
            binding.signUpButton.text = getString(R.string.signing_up)
        }
    }


    /**
     * Helper function to update the tint and color of the password requirements
     */
    fun updateTintAndColor(target: TextView, success: Boolean) {
        val color = if (success) {
            ContextCompat.getColor(applicationContext, R.color.colorSuccess)
        } else {
            ContextCompat.getColor(applicationContext, R.color.colorPrimary)
        }
        val colorList = ColorStateList.valueOf(color);
        TextViewCompat.setCompoundDrawableTintList(target, colorList)
        target.setTextColor(color);

    }

    /**
     * Helper function to update the create button
     */
    fun updateCreateButton(onOff: Boolean) {
        binding.signUpButton.isEnabled = onOff
        binding.signUpButton.isClickable = onOff
    }
}