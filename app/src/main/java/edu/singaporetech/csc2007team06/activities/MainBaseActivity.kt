package edu.singaporetech.csc2007team06.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * This is the base activity for all authenticated activities in this app.
 */
open class MainBaseActivity : AppCompatActivity() {
    private lateinit var toastMessage: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toastMessage = Toast.makeText(this, "", Toast.LENGTH_SHORT)

        // Check if user is logged in
        val user = FirebaseAuth.getInstance().currentUser

        // If user is not logged in, start login activity
        if (user == null) {
            startActivity(Intent(this@MainBaseActivity, LoginActivity::class.java))
            finish()
        }
    }

    // This function is used to replace and show a toast message
    fun showToast(message: String) {
        toastMessage.setText(message)
        toastMessage.show()
    }
}