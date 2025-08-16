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

    val _newTripName = MutableLiveData<Long>()
    val newTripName: LiveData<Long> = _newTripName

    fun getAllTrips() {
        viewModelScope.launch {
            // Explicitly run database operation on IO thread
            val trips = withContext(Dispatchers.IO) {
                TripDatabaseInterface.getAllTrips()
            }
            // postValue automatically switches to main thread
            _storedTrips.postValue(trips)
        }
    }

    fun createNewTrip() {
        viewModelScope.launch {
            val tripName = withContext(Dispatchers.IO) {
                TripDatabaseInterface.createNewTrip()
            }

            _newTripName.postValue(tripName)
        }
    }

    fun clearAllTrips() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                TripDatabaseInterface.clearAllTrips()
            }
        }
        _storedTrips.postValue(emptyList())
    }

    fun getTripStats() {

    }

    fun getTripsContainer(): LiveData<List<TripEntry>> {
        return storedTrips
    }

    fun getNewTripNameContainer(): LiveData<Long> {
        return newTripName
    }
}