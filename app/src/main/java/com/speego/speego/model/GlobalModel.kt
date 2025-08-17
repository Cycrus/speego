package com.speego.speego.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.speego.speego.database.TripCoordinate

object GlobalModel {
    private var currentTripName: Long = 0

    private val _currentCoordinate = MutableLiveData<TripCoordinate>()
    val currentCoordinate: LiveData<TripCoordinate> = _currentCoordinate

    fun setCurrentTripName(tripName: Long) {
        this.currentTripName = tripName
    }

    fun getCurrentTripName(): Long {
        return this.currentTripName
    }

    fun getCoordinateContainer(): LiveData<TripCoordinate> {
        return this.currentCoordinate
    }

    fun getMutableCoordinateContainer(): MutableLiveData<TripCoordinate> {
        return this._currentCoordinate
    }
}