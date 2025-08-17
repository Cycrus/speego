package com.speego.speego.viewmodel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.compose.ui.platform.LocalContext
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
import java.util.Locale
import java.util.concurrent.Executors


data class TripStats(
    val startTime: Long,    // ms
    val avgSpeed: Float,    // km/h
    val duration: Long,     // ms
    val distance: Float,    // km
    val latitude: Double,
    val longitude: Double
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
                        distance = 0f,
                        latitude = 0.0,
                        longitude = 0.0
                    )
                }
                else {
                    TripStats(
                        startTime = tripName,
                        avgSpeed = lastCoord.avgspeed,
                        duration = lastCoord.duration,
                        distance = lastCoord.distance,
                        latitude = lastCoord.latitude,
                        longitude = lastCoord.longitude
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