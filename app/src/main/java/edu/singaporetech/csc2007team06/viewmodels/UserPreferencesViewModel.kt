package edu.singaporetech.csc2007team06.viewmodels

import androidx.lifecycle.*
import edu.singaporetech.csc2007team06.repositories.UserPreferencesRepository
import kotlinx.coroutines.launch

class UserPreferencesViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    private val _showIntroActivity = MutableLiveData<Boolean>()
    val showIntroActivity: LiveData<Boolean> = _showIntroActivity

    init {
        viewModelScope.launch {
            userPreferencesRepository.showIntroActivityFlow.collect {
                _showIntroActivity.postValue(it.showIntroActivity)
            }
        }
    }

    fun updateShowIntroActivity(showIntroActivity: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateShowIntroActivity(showIntroActivity)
        }
    }
}

class UserPreferencesViewModelFactory(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserPreferencesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserPreferencesViewModel(userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}