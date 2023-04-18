package edu.singaporetech.csc2007team06.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.singaporetech.csc2007team06.models.Washer
import edu.singaporetech.csc2007team06.repositories.WasherRepository
import edu.singaporetech.csc2007team06.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WasherViewModel : ViewModel() {
    private val _washersStatus = MutableLiveData<Resource<List<Washer>>>()
    val washersStatus: LiveData<Resource<List<Washer>>> = _washersStatus

    private val _washerStatus = MutableLiveData<Resource<Washer?>>()
    val washerStatus: LiveData<Resource<Washer?>> = _washerStatus

    private val washerRepository = WasherRepository()

    init {
        washers()
    }

    fun washers() {
        _washersStatus.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.Main) {
            val washersResult = washerRepository.washers()
            val data = washersResult.data
            if (data != null && !data.isEmpty) {
                val list = data.toObjects(Washer::class.java)
                _washersStatus.postValue(Resource.Success(list))
            } else {
                _washersStatus.postValue(Resource.Success(listOf()))
            }
        }
    }

    fun washer(id: String) {
        _washerStatus.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.Main) {
            val washerResult = washerRepository.washer(id)
            val data = washerResult.data
            if (data != null && data.exists()) {
                val washer = data.toObject(Washer::class.java)
                _washerStatus.postValue(Resource.Success(washer))
            } else {
                _washerStatus.postValue(Resource.Success(null))
            }
        }
    }

    fun addWasher(washer: Washer) {
        // Add washer
        washerRepository.addWasher(washer)

        // Update the washer list
        washers()
    }

    // EditEquipmentActivity will use this function to update the washer
    // upcomingWash, upcomingRepair, nurseInCharge, note
    fun updateWasher(washer: Washer) {
        viewModelScope.launch(Dispatchers.Main) {
            // Update the washer
            washerRepository.updateWasher(washer)

            // Update the washer list
            washers()
        }
    }

}