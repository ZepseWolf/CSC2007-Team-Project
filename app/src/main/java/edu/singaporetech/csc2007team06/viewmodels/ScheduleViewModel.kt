package edu.singaporetech.csc2007team06.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.singaporetech.csc2007team06.models.Event
import edu.singaporetech.csc2007team06.repositories.ScheduleRepository
import edu.singaporetech.csc2007team06.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ScheduleViewModel : ViewModel() {
    private val TAG: String = "Schedule view model: "

    private val _eventsStatus = MutableLiveData<Resource<List<Event>>>()
    val eventsStatus: LiveData<Resource<List<Event>>> = _eventsStatus

    private val _eventsByDateStatus = MutableLiveData<Resource<List<Event>>>()
    val eventsByDateStatus: LiveData<Resource<List<Event>>> = _eventsByDateStatus

    private val _addEventStatus = MutableLiveData<Resource<Boolean>>()
    val addEventStatus: LiveData<Resource<Boolean>> = _addEventStatus

    private val scheduleRepository = ScheduleRepository()

    fun addEvent(event: Event) {
        _addEventStatus.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.Main) {
            try {
                scheduleRepository.addEvent(event)
                _addEventStatus.postValue(Resource.Success(true))
            } catch (e: Exception) {
                _addEventStatus.postValue(Resource.Error(e.toString()))
            }
        }
    }

    fun events() {
        _eventsStatus.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.Main) {
            val notificationsResult = scheduleRepository.events()
            val data = notificationsResult.data

            if (data != null && !data.isEmpty) {
                val list = data.toObjects(Event::class.java)
                _eventsStatus.postValue(Resource.Success(list))
            } else {
                _eventsStatus.postValue(Resource.Success(listOf()))
            }
        }
    }

    fun eventsByDate(timeStampOfDay: Calendar) {
        _eventsByDateStatus.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.Main) {
            // Get start and return date events
            val startDateEventsResult = scheduleRepository.eventsByStartDate(timeStampOfDay)
            val returnDateEventsResult = scheduleRepository.eventsByReturnDate(timeStampOfDay)

            val startDateEventsData = startDateEventsResult.data
            val returnDateEventsData = returnDateEventsResult.data

            // Add them to a list
            val list = mutableListOf<Event>()
            if (startDateEventsData != null) {
                list.addAll(startDateEventsData.toObjects(Event::class.java))
            }
            if (returnDateEventsData != null) {
                list.addAll(returnDateEventsData.toObjects(Event::class.java))
            }

            // Post the list
            _eventsByDateStatus.postValue(Resource.Success(list))
        }
    }

    fun deleteEventById(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            scheduleRepository.deleteEventById(id)
        }
    }
}