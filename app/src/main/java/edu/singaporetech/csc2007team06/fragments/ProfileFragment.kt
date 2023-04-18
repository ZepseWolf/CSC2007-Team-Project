package edu.singaporetech.csc2007team06.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.singaporetech.csc2007team06.ConfirmLogoutDialog
import edu.singaporetech.csc2007team06.activities.EditEmailActivity
import edu.singaporetech.csc2007team06.activities.EditNameActivity
import edu.singaporetech.csc2007team06.activities.EditPasswordActivity
import edu.singaporetech.csc2007team06.databinding.FragmentProfileBinding
import edu.singaporetech.csc2007team06.utils.Resource
import edu.singaporetech.csc2007team06.viewmodels.AuthViewModel

class ProfileFragment : BaseFragment() {
    private lateinit var profileBinding: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authViewModel: AuthViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        profileBinding = FragmentProfileBinding.inflate(inflater, container, false)

        // initialize firebase auth
        firebaseAuth = Firebase.auth

        // initialize auth view model
        authViewModel = AuthViewModel()

        // get user data
        authViewModel.user()
        // show user name
        authViewModel.userStatus.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    profileBinding.staffIdText.setText("")
                    profileBinding.nameText.setText("")
                    profileBinding.emailText.setText("")
                }
                is Resource.Success -> {
                    profileBinding.staffIdText.setText("${it.data?.id}")
                    profileBinding.nameText.setText("${it.data?.name}")
                    profileBinding.emailText.setText("${it.data?.email}")
                }
                is Resource.Error -> {}
            }
        }

        // set up edit profile buttons click listeners
        profileBinding.nameTextInput.setEndIconOnClickListener {
            startActivity(Intent(activity, EditNameActivity::class.java))
        }
        profileBinding.emailTextInput.setEndIconOnClickListener {
            startActivity(Intent(activity, EditEmailActivity::class.java))
        }
        profileBinding.passwordTextInput.setEndIconOnClickListener {
            startActivity(Intent(activity, EditPasswordActivity::class.java))
        }

        // Show confirm logout dialog
        profileBinding.logoutButton.setOnClickListener {
            val confirmLogoutDialog = ConfirmLogoutDialog()
            confirmLogoutDialog.show(parentFragmentManager, "ConfirmLogoutDialog")
        }

        return profileBinding.root
    }

    override fun onResume() {
        super.onResume()
        // Get user data
        authViewModel.user()

        // Observe user data
        authViewModel.userStatus.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    profileBinding.staffIdText.setText("")
                    profileBinding.nameText.setText("")
                    profileBinding.emailText.setText("")
                }
                is Resource.Success -> {
                    profileBinding.staffIdText.setText("${it.data?.id}")
                    profileBinding.nameText.setText("${it.data?.name}")
                    profileBinding.emailText.setText("${it.data?.email}")
                }
                is Resource.Error -> {}
            }
        }
    }
}