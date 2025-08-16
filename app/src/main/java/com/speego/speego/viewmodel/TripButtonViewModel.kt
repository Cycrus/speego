package com.speego.speego.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speego.speego.database.TripCoordinate
import com.speego.speego.database.TripDatabaseInterface
import com.speego.speego.database.TripEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


data class TripStats(
    val startTime: Long,    // ms
    val avgSpeed: Float,    // km/h
    val duration: Long,     // ms
    val distance: Float     // km
)


class TripButtonViewModel(val tripName: Long) : ViewModel() {
    private val _tripStats = MutableLiveData<TripStats>()
    val tripStats: LiveData<TripStats> = _tripStats

    fun fetchTripStats() {
        viewModelScope.launch {
            val tripStats = withContext(Dispatchers.IO) {
                val lastCoord: TripCoordinate? = TripDatabaseInterface.getLastCoordinateOfTrip(tripName)
                if (lastCoord == null) {
                    TripStats(
                        startTime = tripName,
                        avgSpeed = 0f,
                        duration = 0,
                        distance = 0f
                    )
                }
                else {
                    TripStats(
                        startTime = tripName,
                        avgSpeed = lastCoord.avgspeed,
                        duration = lastCoord.duration,
                        distance = lastCoord.distance
                    )
                }
            }
            _tripStats.postValue(tripStats)
        }
    }

    fun getTripStatsContainer(): LiveData<TripStats> {
        return tripStats
    }
}