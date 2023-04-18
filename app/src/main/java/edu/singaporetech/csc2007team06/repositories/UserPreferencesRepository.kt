package edu.singaporetech.csc2007team06.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import edu.singaporetech.csc2007team06.models.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    private object PreferencesKeys {
        val showIntroActivity = booleanPreferencesKey("showIntroActivity")
    }

    val showIntroActivityFlow: Flow<UserPreferences> = dataStore.data.map { preferences ->
        mapUserPreferences(preferences)
    }

    suspend fun updateShowIntroActivity(showIntroActivity: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.showIntroActivity] = showIntroActivity
        }
    }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        return UserPreferences(preferences[PreferencesKeys.showIntroActivity] ?: true)
    }

    companion object {
        // Constant for naming our DataStore - you can change this if you want
        const val USER_PREFERENCES_NAME = "user_preferences"

        // Boilerplate-y code for singleton: the private reference to this self
        @Volatile
        private var INSTANCE: UserPreferencesRepository? = null

        /**
         * Boilerplate-y code for singleton: to ensure only a single copy is ever present
         * @param dataStore The DataStore to use
         */
        fun getInstance(dataStore: DataStore<Preferences>): UserPreferencesRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE?.let {
                    return it
                }
                val instance = UserPreferencesRepository(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}