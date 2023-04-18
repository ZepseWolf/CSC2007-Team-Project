package edu.singaporetech.csc2007team06.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {
    private lateinit var toastMessage: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toastMessage = Toast.makeText(activity, "", Toast.LENGTH_SHORT)
    }

    // This function is used to replace and show a toast message
    fun showToast(message: String) {
        toastMessage.setText(message)
        toastMessage.show()
    }
}