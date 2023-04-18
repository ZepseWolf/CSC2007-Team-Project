package edu.singaporetech.csc2007team06

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.singaporetech.csc2007team06.activities.LoginActivity
import edu.singaporetech.csc2007team06.databinding.DialogConfirmLogoutBinding
import edu.singaporetech.csc2007team06.viewmodels.AuthViewModel

class ConfirmLogoutDialog : DialogFragment() {
    private var _binding: DialogConfirmLogoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogConfirmLogoutBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())

        // Initialize AuthViewModel
        val authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // underline confirm logout text
        binding.textViewConfirmLogout.paint.isUnderlineText = true

        // close dialog
        binding.imageViewClose.setOnClickListener {
            dismiss()
        }

        // cancel to close dialog
        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        // logout user
        binding.buttonYes.setOnClickListener {
            authViewModel.removeToken(
                Firebase.auth.uid!!,
                authViewModel.fcmTokenStatus.value?.data!!
            )
            Firebase.auth.signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }

        return builder
            .setView(binding.root)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}