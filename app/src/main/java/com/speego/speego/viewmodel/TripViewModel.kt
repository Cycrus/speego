package com.speego.speego.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speego.speego.database.TripCoordinate
import com.speego.speego.database.TripDatabaseInterface
import com.speego.speego.model.GlobalModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TripViewModel : ViewModel() {
    private val _coordinateListLiveData: MutableLiveData<List<TripCoordinate>> = MutableLiveData<List<TripCoordinate>>()
    val coordinateListLiveData: LiveData<List<TripCoordinate>> = _coordinateListLiveData

    fun getCoordinateUpdatedContainer(): LiveData<TripCoordinate> {
        return GlobalModel.getCoordinateContainer()
    }

    fun getCoordinateListContainer(): LiveData<List<TripCoordinate>> {
        return this.coordinateListLiveData
    }

    fun postCoordinateList(trackName: Long, n: Int) {
        viewModelScope.launch {
            val coordinateList = withContext(Dispatchers.IO) {
                TripDatabaseInterface.getLastNCoordinatesOfTrip(trackName, n)
            }
            _coordinateListLiveData.postValue(coordinateList)
        }
    }

    fun setTripFinished(trackName: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                TripDatabaseInterface.setTripFinished(trackName)
            }
            _coordinateListLiveData.postValue(listOf())
            GlobalModel.getMutableCoordinateContainer().postValue(null)
        }
    }
}