package com.speego.speego.database

import android.content.Context
import android.location.Location
import androidx.room.Room
import kotlin.Long


object TripDatabaseInterface {
    private lateinit var db: TripDatabase
    private lateinit var tripEntryDao: TripEntryDao
    private lateinit var coordinateDao: TripCoordinateDao

    fun init(context: Context) {
        db = Room.databaseBuilder(
            context.applicationContext,
            TripDatabase::class.java, "trip-database"
        ).build()
        tripEntryDao = db.tripEntryDao()
        coordinateDao = db.tripCoordinateDao()
    }

    /*********************************/
    /******** Trip Operations ********/
    suspend fun createNewTrip(tripName: Long = 0): Long {
        val currTime: Long = if(tripName == 0L) System.currentTimeMillis() else tripName
        val newTrip: TripEntry = TripEntry(startTime = currTime, finished = false)
        tripEntryDao.createNew(newTrip)
        return currTime
    }

    suspend fun deleteTrip(tripStartTime: Long): Boolean {
        val deleteTrip: TripEntry? = tripEntryDao.getByStartTime(tripStartTime)
        if (deleteTrip == null)
        {
            return false
        }
        val deletedEntries: Int = tripEntryDao.delete(deleteTrip)
        return deletedEntries > 0
    }

    suspend fun clearAllTrips(): Int {
        return tripEntryDao.clear()
    }

    suspend fun getLastTrip(): TripEntry? {
        return tripEntryDao.getLast()
    }

    suspend fun getAllTrips(): List<TripEntry> {
        return tripEntryDao.getAll()
    }

    suspend fun setTripFinished(tripName: Long) {
        tripEntryDao.setFinished(tripName, true)
    }

    /***************************************/
    /******** Coordinate Operations ********/
    suspend fun createNewCoordinate(tripStartTime: Long,
                                    latitude: Double, longitude: Double,
                                    coordinateTime: Long = 0): TripCoordinate {
        val prevCoordinate: TripCoordinate? = coordinateDao.getLastOfTrip(tripStartTime)
        val currTime: Long = if(coordinateTime == 0L) System.currentTimeMillis() else coordinateTime
        val newDuration: Long = currTime - tripStartTime
        var newSpeed: Float = 0f
        var newDistance: Float = 0.0f
        var currSequenceNr: Int = 0
        var newAvgSpeed: Float = newSpeed

        // Continuous average formulas:
        //      https://stackoverflow.com/questions/22999487/update-the-average-of-a-continuous-sequence-of-numbers-in-constant-time
        if (prevCoordinate != null) {
            val timeDelta: Long = newDuration - prevCoordinate.duration
            currSequenceNr = prevCoordinate.sequenceNr + 1
            val distanceResults = FloatArray(3)
            Location.distanceBetween(
                prevCoordinate.latitude, prevCoordinate.longitude,  // lat1, lon1
                latitude, longitude,  // lat2, lon2
                distanceResults
            )
            val newDistanceKm = distanceResults[0] / 1000.0f
            newDistance = prevCoordinate.distance + newDistanceKm
            newSpeed = newDistanceKm / (timeDelta / 3600000.0f) // 3600000 = factor from ms to h
            //newAvgSpeed = prevCoordinate.avgspeed + ((newSpeed - prevCoordinate.avgspeed) / currSequenceNr)
            // Compute avgSpeed only based on total moved distance and duration of trip
            newAvgSpeed = newDistance / (newDuration / 1000.0f / 60.0f / 60.0f)
        }

        val newCoordinate: TripCoordinate = TripCoordinate(
            tripStartTime = tripStartTime,
            sequenceNr = currSequenceNr,
            latitude = latitude,
            longitude = longitude,
            speed = newSpeed,
            avgspeed = newAvgSpeed,
            duration = newDuration,
            distance = newDistance
        )
        coordinateDao.addNewCoordinate(newCoordinate)
        return newCoordinate
    }

    suspend fun getCoordinateList(tripStartTime: Long): List<TripCoordinate> {
        return coordinateDao.getAllOfTrip(tripStartTime)
    }

    suspend fun getLastCoordinateOfTrip(tripStartTime: Long): TripCoordinate? {
        return coordinateDao.getLastOfTrip(tripStartTime)
    }

    suspend fun getLastNCoordinatesOfTrip(tripStartTime: Long, n: Int): List<TripCoordinate> {
        return coordinateDao.getLastNOfTrip(tripStartTime, n)
    }
}