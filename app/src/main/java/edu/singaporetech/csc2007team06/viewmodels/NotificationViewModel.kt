package edu.singaporetech.csc2007team06.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.singaporetech.csc2007team06.models.Notification
import edu.singaporetech.csc2007team06.repositories.NotificationRepository
import edu.singaporetech.csc2007team06.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {
    private val _notificationsStatus = MutableLiveData<Resource<List<Notification>>>()
    val notificationsStatus: LiveData<Resource<List<Notification>>> = _notificationsStatus
    private val notificationRepository = NotificationRepository()

    fun notifications(userId: String) {
        _notificationsStatus.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.Main) {
            val notificationsResult = notificationRepository.notifications(userId)
            val data = notificationsResult.data
            if (data != null && !data.isEmpty) {
                val list = data.toObjects(Notification::class.java)
                _notificationsStatus.postValue(Resource.Success(list))
            } else {
                _notificationsStatus.postValue(Resource.Success(listOf()))
            }
        }
    }

    fun updateNotification(id: String, acknowledge: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            // Update the notification
            notificationRepository.updateNotification(id, acknowledge)
        }
    }
}