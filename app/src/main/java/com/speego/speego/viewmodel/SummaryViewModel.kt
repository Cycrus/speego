package com.speego.speego.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speego.speego.database.TripCoordinate
import com.speego.speego.database.TripDatabaseInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SummaryViewModel : ViewModel() {
    private val _coordinateListLiveData: MutableLiveData<List<TripCoordinate>> = MutableLiveData<List<TripCoordinate>>()
    val coordinateListLiveData: LiveData<List<TripCoordinate>> = _coordinateListLiveData

    fun getCoordListContainer(): LiveData<List<TripCoordinate>> {
        return this.coordinateListLiveData
    }

    fun postCoordinateList(trackName: Long) {
        viewModelScope.launch {
            val coordinateList = withContext(Dispatchers.IO) {
                TripDatabaseInterface.getCoordinateList(trackName)
            }
            _coordinateListLiveData.postValue(coordinateList)
        }
    }

    fun clearCoordinateList() {
        _coordinateListLiveData.postValue(listOf())
    }
}