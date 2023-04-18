package edu.singaporetech.csc2007team06.activities

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.singaporetech.csc2007team06.R
import edu.singaporetech.csc2007team06.databinding.ActivityEditPasswordBinding
import edu.singaporetech.csc2007team06.viewmodels.AuthViewModel


class EditPasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityEditPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        firebaseAuth = Firebase.auth

        // Get the view model
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Set up the text watcher for the password input
        binding.newPasswordInput.addTextChangedListener(textWatcher)

        // Set up the back button
        binding.imageViewBack.setOnClickListener {
            finish()
        }

        // Set up the change password button
        binding.signUpButton.setOnClickListener {
            val newPassword = binding.newPasswordInput.text.toString()
            changePassword(newPassword)
        }
    }

    /**
     * Change the password of the current user.
     */
    private fun changePassword(password: String) {
        val email: String = firebaseAuth.currentUser?.email.toString()
        val credential =
            EmailAuthProvider.getCredential(email, binding.passwordInput.text.toString())
        firebaseAuth.currentUser?.reauthenticate(credential)?.addOnCompleteListener { reAuth ->
            if (!reAuth.isSuccessful) {
                showToast("Password change failed.")
                return@addOnCompleteListener
            }

            firebaseAuth.currentUser?.updatePassword(password)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast("Password changed successfully.")
                    finish()
                } else {
                    showToast("Password change failed.")
                }
            }

        }

    }


    /**
     * Text watcher for the password input.
     * and update the UI accordingly.
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

            // Check if the password is at least 8 characters long
            if (passwordString.length > 7) {
                updateTintAndColor(binding.error8Char, true)
                lengthRequirement = true
            } else {
                updateTintAndColor(binding.error8Char, false)
            }

            // Check if the password has both upper and lower case letters
            if (passwordString.matches(Regex(".*[a-z].*")) && passwordString.matches(Regex(".*[A-Z].*"))) {
                updateTintAndColor(binding.errorUpperLower, true)
                bothUpperLower = true;
            } else {
                updateTintAndColor(binding.errorUpperLower, false)
            }

            // Check if the password has at least one symbol or number
            if (passwordString.matches(Regex("(.*\\W.*)")) || passwordString.matches(Regex(".*[0-9].*"))) {
                updateTintAndColor(binding.errorNumSymbol, true)
                symbols = true
            } else {
                updateTintAndColor(binding.errorNumSymbol, false)
            }

            // Check if all the requirements are met
            if (lengthRequirement && bothUpperLower && symbols) {
                updateCreateButton(true)
            } else {
                updateCreateButton(false)
            }
        }
    }


    /**
     * Update the tint and color of the text view.
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
     * Update the create button.
     */
    fun updateCreateButton(onOff: Boolean) {
        binding.signUpButton.isEnabled = onOff
        binding.signUpButton.isClickable = onOff
    }
}