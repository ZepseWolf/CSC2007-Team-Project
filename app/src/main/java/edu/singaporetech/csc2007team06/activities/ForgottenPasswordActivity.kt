package edu.singaporetech.csc2007team06.activities

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import edu.singaporetech.csc2007team06.R
import edu.singaporetech.csc2007team06.databinding.ActivityForgottenPasswordBinding


class ForgottenPasswordActivity : BaseActivity() {
    private lateinit var forgottenPasswordBinding: ActivityForgottenPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgottenPasswordBinding = DataBindingUtil.setContentView(
            this@ForgottenPasswordActivity,
            R.layout.activity_forgotten_password
        )

        // Set up the forgotten password button click listener
        forgottenPasswordBinding.buttonSubmit.setOnClickListener {
            val email = forgottenPasswordBinding.emailInput.text.toString()
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Password reset email sent successfully
                        showToast("Password reset email sent")
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        // Password reset email failed to send
                        val exception = task.exception
                        showToast("Password reset email failed: $exception")
                    }
                }
        }
    }
}