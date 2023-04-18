package edu.singaporetech.csc2007team06.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.singaporetech.csc2007team06.models.User
import edu.singaporetech.csc2007team06.repositories.UserRepository
import edu.singaporetech.csc2007team06.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val _usersStatus = MutableLiveData<Resource<List<User>>>()
    val usersStatus: LiveData<Resource<List<User>>> = _usersStatus
    private val userRepository = UserRepository()

    init {
        users()
    }

    private fun users() {
        _usersStatus.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.Main) {
            val usersResult = userRepository.users()
            val data = usersResult.data
            if (data != null && !data.isEmpty) {
                val list = data.toObjects(User::class.java)
                _usersStatus.postValue(Resource.Success(list))
            } else {
                _usersStatus.postValue(Resource.Success(listOf()))
            }
        }
    }
}