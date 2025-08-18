package com.speego.speego.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speego.speego.database.TripDatabaseInterface
import com.speego.speego.database.TripEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StartViewModel : ViewModel() {
    private val _lastTripLiveData: MutableLiveData<TripEntry?> = MutableLiveData<TripEntry?>()
    val lastTripLiveData: LiveData<TripEntry?> = _lastTripLiveData

    fun getLastTripContainer(): LiveData<TripEntry?> {
        return lastTripLiveData
    }

    fun postLastTrip() {
        viewModelScope.launch {
            val lastTrip = withContext(Dispatchers.IO) {
                TripDatabaseInterface.getLastTrip()
            }
            _lastTripLiveData.postValue(lastTrip)
        }
    }
}