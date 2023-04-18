package edu.singaporetech.csc2007team06.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import edu.singaporetech.csc2007team06.databinding.ActivityIntroBinding
import edu.singaporetech.csc2007team06.repositories.UserPreferencesRepository
import edu.singaporetech.csc2007team06.viewmodels.UserPreferencesViewModel
import edu.singaporetech.csc2007team06.viewmodels.UserPreferencesViewModelFactory


class IntroActivity : BaseActivity() {
    private lateinit var binding: ActivityIntroBinding

    // Use data store to store user preferences
    private val Context.dataStore by preferencesDataStore(
        name = UserPreferencesRepository.USER_PREFERENCES_NAME,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)

        // Use view model to retrieve user preferences
        val userPreferencesViewModel = ViewModelProvider(
            this,
            UserPreferencesViewModelFactory(UserPreferencesRepository.getInstance(dataStore))
        )[UserPreferencesViewModel::class.java]

        // Check if user has already seen intro activity
        // If yes, start login activity
        // If no, show intro activity
        userPreferencesViewModel.showIntroActivity.observe(this) {
            if (it == false && binding.introLayout.visibility == View.GONE) {
                // Only start login activity if intro activity is not visible
                startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
                overridePendingTransition(0, 0)
                finish()
            } else {
                // Show intro activity
                setContentView(binding.root)
                binding.introLayout.visibility = View.VISIBLE

                binding.loginButton.setOnClickListener {
                    // update user preferences to not show intro activity again
                    userPreferencesViewModel.updateShowIntroActivity(false)
                    // start login activity without animation
                    startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }
}