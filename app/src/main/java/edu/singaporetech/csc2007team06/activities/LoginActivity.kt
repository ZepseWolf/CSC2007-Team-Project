package edu.singaporetech.csc2007team06.activities

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import edu.singaporetech.csc2007team06.R
import edu.singaporetech.csc2007team06.databinding.ActivityLoginBinding
import edu.singaporetech.csc2007team06.utils.Resource
import edu.singaporetech.csc2007team06.utils.TestIdlingResource
import edu.singaporetech.csc2007team06.viewmodels.AuthViewModel

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var cancellationSignal: CancellationSignal? = null
    private lateinit var authViewModel: AuthViewModel

    // Biometric prompt callback
    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() =
            @RequiresApi(Build.VERSION_CODES.P)
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errorCode, errString)
                    showToast("$errString")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }

    companion object {
        private const val TAG = "MainActivity"
        private const val USE_BIO = 100 // request code for fingerprint authentication
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@LoginActivity, R.layout.activity_login)

        // Initialize view model
        authViewModel = AuthViewModel()

        // Get current user
        val user = FirebaseAuth.getInstance().currentUser

        // Start forgot password activity if user clicks on forgot password
        binding.textForgot.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgottenPasswordActivity::class.java))
        }

        // Sign in user if user clicks on login button
        binding.loginButton.setOnClickListener {
            TestIdlingResource.increment() // Set app as busy. This is for testing purposes.
            signIn(
                binding.editTextEmail.text.toString(),
                binding.editTextPassword.text.toString()
            )
        }

        // Start create account activity if user clicks on sign up
        binding.signup.setOnClickListener {
            startActivity(Intent(this@LoginActivity, CreateAccountActivity::class.java))
        }

        // Check if the device has fingerprint sensor
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            checkBiometricSupport() // check if the device has fingerprint sensor

            if (user == null) {
                binding.fingerprintButton.visibility = View.GONE
            } else {
                showBiometricPrompt()
            }

            binding.fingerprintButton.setOnClickListener {
                showBiometricPrompt()
            }
        } else {
            // hide the fingerprint button
            binding.fingerprintButton.isEnabled = false
            binding.fingerprintButton.visibility = View.INVISIBLE
        }
    }

    /**
     * Check if the device has fingerprint sensor
     * show biometric prompt if the device has fingerprint sensor
     */
    @RequiresApi(Build.VERSION_CODES.P)
    private fun showBiometricPrompt() {
        val biometricPrompt: BiometricPrompt = BiometricPrompt.Builder(this)
            .setTitle("Login")
            .setSubtitle("Authenticaion is required")
            .setDescription("Fingerprint Authentication")
            .setNegativeButton("Cancel", this.mainExecutor) { dialog, which -> }.build()
        biometricPrompt.authenticate(
            getCancellationSignal(),
            mainExecutor,
            authenticationCallback
        )
    }

    /**
     * Sign in user
     */
    private fun signIn(email: String, password: String) {
        authViewModel.login(email, password)

        // Observe the user sign LiveData
        authViewModel.userSignInStatus.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    shouldEnableButton(false)
                }
                is Resource.Success -> {
                    Log.d(TAG, "signInWithEmail:success")
                    // get firebase cloud messaging token
                    // and update the user's token in the database
                    val token = authViewModel.fcmTokenStatus.value
                    if (token?.data != null) {
                        authViewModel.addToken(token.data)
                    }
                    showToast("Authenticated")
                    TestIdlingResource.decrement() // Set app as idle. This is for testing purposes.
                    shouldEnableButton(true)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
                is Resource.Error -> {
                    Log.e(TAG, it.message!!)
                    TestIdlingResource.decrement() // Set app as idle. This is for testing purposes.
                    shouldEnableButton(true)
                    showToast(it.message)
                }
            }
        }
    }

    /**
     * Check if we should enable the login button
     */
    private fun shouldEnableButton(enabled: Boolean) {
        if (enabled) {
            binding.loginButton.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_right,
                0
            )
            binding.loginButton.isEnabled = true
            binding.loginButton.text = getString(R.string.intro_login_button)
        } else {
            // hide drawable icon on the right of the button
            // and set the button to be disabled
            // and set the button text to signing up...
            binding.loginButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            binding.loginButton.isEnabled = false
            binding.loginButton.text = getString(R.string.intro_logging_in_button)
        }
    }

    /**
     * Get cancellation signal for biometric prompt
     */
    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            showToast("Cancelled.")
        }
        return cancellationSignal as CancellationSignal
    }

    /**
     * Check for bio metric support
     * @return true if the device has fingerprint sensor
     * @return false if the device does not have fingerprint sensor or fingerprint has not been enabled
     */
    @RequiresApi(Build.VERSION_CODES.P)
    private fun checkBiometricSupport(): Boolean {
        val keyguardManager: KeyguardManager =
            getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!keyguardManager.isKeyguardSecure) {
            showToast("Fingerprint has not been enabled in settings.")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CALL_LOG),
                USE_BIO
            )
            return false
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.USE_BIOMETRIC
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CALL_LOG),
                USE_BIO
            )
            showToast("Fingerprint has not been enabled in settings.")
            return false
        }
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            true
        } else true
    }
}