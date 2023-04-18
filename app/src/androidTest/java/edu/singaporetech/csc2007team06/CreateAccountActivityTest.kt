package edu.singaporetech.csc2007team06

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.singaporetech.csc2007team06.activities.CreateAccountActivity
import edu.singaporetech.csc2007team06.activities.LoginActivity
import edu.singaporetech.csc2007team06.repositories.UserPreferencesRepository
import edu.singaporetech.csc2007team06.utils.TestIdlingResource
import edu.singaporetech.csc2007team06.viewmodels.UserPreferencesViewModel
import edu.singaporetech.csc2007team06.viewmodels.UserPreferencesViewModelFactory
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateAccountActivityTest {
    @get:Rule
    var rule = ActivityScenarioRule(CreateAccountActivity::class.java)
    lateinit var idlingResource: IdlingResource

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
        Firebase.auth.signOut()
        idlingResource = TestIdlingResource.countingIdlingResource
        IdlingRegistry.getInstance().register(idlingResource)
        Intents.init()
    }

    @Test
    fun createAccountSuccessful() {
        onView(
            ViewMatchers.withId(R.id.name_input),
        ).perform(ViewActions.typeText("testUser"), ViewActions.closeSoftKeyboard())
        onView(
            ViewMatchers.withId(R.id.email_input),
        ).perform(ViewActions.typeText("testuser@test.com"), ViewActions.closeSoftKeyboard())
        onView(
            ViewMatchers.withId(R.id.password_input),
        ).perform(ViewActions.typeText("Asdqwe123"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.sign_up_button)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(LoginActivity::class.java.name))
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)

        runBlocking {
            userPreferencesViewModel.updateShowIntroActivity(true)
            // firebase login with email and password
            val result =
                Firebase.auth.signInWithEmailAndPassword("testuser@test.com", "Asdqwe123").await()
            val user = result.user
            if (user != null) {
                Firebase.firestore.collection("users").document(user.uid).delete().await()
                user.delete()
                Firebase.auth.signOut()
            }
            Intents.release()
        }
    }
}