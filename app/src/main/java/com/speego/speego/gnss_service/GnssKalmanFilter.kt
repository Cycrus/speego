package com.speego.speego.gnss_service

import android.location.Location

class GnssKalmanFilter {
    private var lastFilteredLocation: Location? = null
    private val processNoise = 2.0 // The higher, the more new coordinates are trusted

    fun filterLocation(newLocation: Location): Location {
        val lastLoc = lastFilteredLocation

        if (lastLoc == null) {
            lastFilteredLocation = newLocation
            return newLocation
        }

        // Simple Kalman-like filtering
        val timeDelta = (newLocation.time - lastLoc.time) / 1000.0 // seconds
        val accuracy = newLocation.accuracy.toDouble()

        // Prediction (assume constant velocity)
        val predictedLat = lastLoc.latitude
        val predictedLon = lastLoc.longitude

        // Update
        val kalmanGain = (processNoise + timeDelta) / (processNoise + timeDelta + accuracy)

        val filteredLat = predictedLat + kalmanGain * (newLocation.latitude - predictedLat)
        val filteredLon = predictedLon + kalmanGain * (newLocation.longitude - predictedLon)

        val filteredLocation = Location(newLocation.provider).apply {
            latitude = filteredLat
            longitude = filteredLon
            time = newLocation.time
        }

        lastFilteredLocation = filteredLocation
        return filteredLocation
    }
}