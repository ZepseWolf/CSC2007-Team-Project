package edu.singaporetech.csc2007team06

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import edu.singaporetech.csc2007team06.activities.IntroActivity
import edu.singaporetech.csc2007team06.activities.LoginActivity
import edu.singaporetech.csc2007team06.repositories.UserPreferencesRepository
import edu.singaporetech.csc2007team06.viewmodels.UserPreferencesViewModel
import edu.singaporetech.csc2007team06.viewmodels.UserPreferencesViewModelFactory
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class IntroActivityTest {
    @get:Rule
    var rule = ActivityScenarioRule(IntroActivity::class.java)
    private val Context.dataStore by preferencesDataStore(
        name = UserPreferencesRepository.USER_PREFERENCES_NAME,
    )
    private val context =
        InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
    private val userPreferencesViewModel =
        UserPreferencesViewModelFactory(UserPreferencesRepository.getInstance(context.dataStore)).create(
            UserPreferencesViewModel::class.java
        )

    @Before
    fun setUp() {
        runBlocking {
            userPreferencesViewModel.updateShowIntroActivity(true)
            Intents.init()
        }
    }

    @Test
    fun goToLoginActivity() {
        onView(withId(R.id.loginButton)).perform(click())
        intended(hasComponent(LoginActivity::class.java.name))
    }

    @After
    fun tearDown() {

        runBlocking {
            userPreferencesViewModel.updateShowIntroActivity(true)
            Intents.release()
        }
    }

}