package edu.singaporetech.csc2007team06

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.singaporetech.csc2007team06.activities.LoginActivity
import edu.singaporetech.csc2007team06.activities.MainActivity
import edu.singaporetech.csc2007team06.utils.TestIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LoginActivityTest {
    @get:Rule
    var rule = ActivityScenarioRule(LoginActivity::class.java)
    lateinit var idlingResource: IdlingResource

    @Before
    fun setUp() {
        Firebase.auth.signOut()
        idlingResource = TestIdlingResource.countingIdlingResource
        IdlingRegistry.getInstance().register(idlingResource)
        Intents.init()
    }

    @Test
    fun loginSuccessful() {
        onView(
            withId(R.id.editTextEmail),
        ).perform(typeText("zhizhanlua@gmail.com"), closeSoftKeyboard())
        onView(
            withId(R.id.editTextPassword)
        ).perform(typeText("Asdqwe123"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
        Firebase.auth.signOut()
        Intents.release()
    }
}