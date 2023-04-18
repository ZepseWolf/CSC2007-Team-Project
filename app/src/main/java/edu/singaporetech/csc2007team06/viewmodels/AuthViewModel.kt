package edu.singaporetech.csc2007team06.viewmodels

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import edu.singaporetech.csc2007team06.models.User
import edu.singaporetech.csc2007team06.repositories.AuthRepository
import edu.singaporetech.csc2007team06.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for user registration and sign in.
 */
class AuthViewModel : ViewModel() {

    // User registration status
    private val _userRegistrationStatus = MutableLiveData<Resource<AuthResult>>()
    val userRegistrationStatus: LiveData<Resource<AuthResult>> = _userRegistrationStatus

    // User sign in status
    private val _userSignInStatus = MutableLiveData<Resource<AuthResult>>()
    val userSignInStatus: LiveData<Resource<AuthResult>> = _userSignInStatus

    private val _userStatus = MutableLiveData<Resource<User?>>()
    val userStatus: LiveData<Resource<User?>> = _userStatus

    private val _fcmTokenStatus = MutableLiveData<Resource<String>>()
    val fcmTokenStatus: LiveData<Resource<String>> = _fcmTokenStatus

    private val authRepository = AuthRepository()

    init {
        getFcmToken()
    }

    fun registerUser(email: String, password: String, name: String) {
        // Validate input
        val error =
            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                "Please enter your email and password."
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                "Please enter a valid email."
            } else null

        if (error != null) {
            _userRegistrationStatus.postValue(Resource.Error(error))
            return
        }

        // Set loading status
        _userRegistrationStatus.postValue(Resource.Loading())

        // Launch coroutine to register user
        viewModelScope.launch(Dispatchers.Main) {
            val registerResult = authRepository.registerUser(email, password, name)
            _userRegistrationStatus.postValue(registerResult)
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _userSignInStatus.postValue(Resource.Error("Please enter your email and password."))
            return
        }

        // Set loading status
        _userSignInStatus.postValue(Resource.Loading())

        // Launch coroutine to sign in user
        viewModelScope.launch(Dispatchers.Main) {
            val loginResult = authRepository.login(email, password)
            _userSignInStatus.postValue(loginResult)
        }
    }

    fun user() {
        _userStatus.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.Main) {
            val userResult = authRepository.user()
            val data = userResult.data
            if (data != null) {
                val user = data.toObject(User::class.java)
                _userStatus.postValue(Resource.Success(user))
            } else {
                _userStatus.postValue(Resource.Success(null))
            }
        }
    }

    fun addToken(token: String) {
        viewModelScope.launch(Dispatchers.Main) {
            authRepository.addToken(token)
        }
    }

    fun removeToken(userId: String, token: String) {
        viewModelScope.launch(Dispatchers.Main) {
            authRepository.removeToken(userId, token)
        }
    }

    private fun getFcmToken() {
        viewModelScope.launch(Dispatchers.Main) {
            val result = authRepository.getFcmToken()
            val data = result.data
            if (data != null) {
                _fcmTokenStatus.postValue(Resource.Success(data))
            } else {
                _fcmTokenStatus.postValue(Resource.Error("No token found"))
            }
        }
    }

    fun updateEmail(email: String){
        viewModelScope.launch(Dispatchers.Main) {
            authRepository.updateEmail(email)
        }
    }

    fun updateName(name: String){
        viewModelScope.launch(Dispatchers.Main) {
            authRepository.updateName(name)
        }
    }
}