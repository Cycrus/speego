package com.speego.speego.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.speego.speego.database.TripDatabaseInterface
import com.speego.speego.database.TripEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectionViewModel : ViewModel() {
    private val _storedTrips = MutableLiveData<List<TripEntry>>()
    val storedTrips: LiveData<List<TripEntry>> = _storedTrips

    fun getAllTrips() {
        viewModelScope.launch {
            val trips = withContext(Dispatchers.IO) {
                TripDatabaseInterface.getAllTrips()
            }
            _storedTrips.postValue(trips)
        }
    }

    fun createNewTrip(tripName: Long = 0) {
        viewModelScope.launch {
            val tripName = withContext(Dispatchers.IO) {
                TripDatabaseInterface.createNewTrip(tripName)
            }
        }
    }

    fun clearAllTrips() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                TripDatabaseInterface.clearAllTrips()
            }
            _storedTrips.postValue(emptyList())
        }
    }

    fun removeTrack(trackName: Long) {
        viewModelScope.launch {
            val trips = withContext(Dispatchers.IO) {
                val success: Boolean = TripDatabaseInterface.deleteTrip(trackName)
                TripDatabaseInterface.getAllTrips()
            }
            _storedTrips.postValue(trips)
        }
    }

    fun getTripsContainer(): LiveData<List<TripEntry>> {
        return storedTrips
    }
}