package edu.singaporetech.csc2007team06.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.singaporetech.csc2007team06.models.Endoscope
import edu.singaporetech.csc2007team06.repositories.EndoscopeRepository
import edu.singaporetech.csc2007team06.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EndoscopeViewModel : ViewModel() {
    private val _endoscopesStatus = MutableLiveData<Resource<List<Endoscope>>>()
    val endoscopesStatus: LiveData<Resource<List<Endoscope>>> = _endoscopesStatus

    private val _endoscopeStatus = MutableLiveData<Resource<Endoscope?>>()
    val endoscopeStatus: LiveData<Resource<Endoscope?>> = _endoscopeStatus

    private val endoscopeRepository = EndoscopeRepository()

    init {
        endoscopes()
    }

    fun endoscopes() {
        _endoscopesStatus.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.Main) {
            val endoscopesResult = endoscopeRepository.endoscopes()
            val data = endoscopesResult.data
            if (data != null && !data.isEmpty) {
                val list = data.toObjects(Endoscope::class.java)
                _endoscopesStatus.postValue(Resource.Success(list))
            } else {
                _endoscopesStatus.postValue(Resource.Success(listOf()))
            }
        }
    }

    fun endoscope(id: String) {
        _endoscopeStatus.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.Main) {
            val endoscopeResult = endoscopeRepository.endoscope(id)
            val data = endoscopeResult.data
            if (data != null && data.exists()) {
                val endoscope = data.toObject(Endoscope::class.java)
                _endoscopeStatus.postValue(Resource.Success(endoscope))
            } else {
                _endoscopeStatus.postValue(Resource.Success(null))
            }
        }
    }

    fun addEndoscope(endoscope: Endoscope) {
        viewModelScope.launch(Dispatchers.Main) {
            // Add endoscope
            endoscopeRepository.addEndoscope(endoscope)

            // Update the endoscope list
            endoscopes()
        }
    }

    // EditEquipmentActivity will use this function to update the endoscope
    // upcomingWash, upcomingRepair, nurseInCharge, note
    fun updateEndoscope(endoscope: Endoscope) {
        viewModelScope.launch(Dispatchers.Main) {
            // Update the endoscope
            endoscopeRepository.updateEndoscope(endoscope)

            // Update the endoscope list
            endoscopes()
        }
    }
}