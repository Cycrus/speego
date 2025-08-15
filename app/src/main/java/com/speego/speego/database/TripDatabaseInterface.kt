package com.speego.speego.database

import android.content.Context
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
    fun createNewTrip(): Long {
        val currTime: Long = System.currentTimeMillis()
        val newTrip: TripEntry = TripEntry(startTime = currTime, finished = false)
        tripEntryDao.createNew(newTrip)
        return currTime
    }

    fun deleteTrip(tripStartTime: Long): Boolean {
        val deleteTrip: TripEntry? = tripEntryDao.getByStartTime(tripStartTime)
        if (deleteTrip == null)
        {
            return false
        }
        val deletedEntries: Int = tripEntryDao.delete(deleteTrip)
        return deletedEntries > 0
    }

    fun getLastTrip(): TripEntry? {
        return tripEntryDao.getLast()
    }

    fun getAllTrips(): List<TripEntry> {
        return tripEntryDao.getAll()
    }

    /***************************************/
    /******** Coordinate Operations ********/
    fun createNewCoordinate(tripStartTime: Long,
                            latitude: Double, longitude: Double) {
        val prevCoordinate: TripCoordinate? = coordinateDao.getLastOfTrip(tripStartTime)
        var newSpeed: Float = 0f // TODO: Compute real speed
        var newDistance: Float = 0.1f // TODO: Compute real distance
        var newDuration: Long = System.currentTimeMillis() - tripStartTime
        var currSequenceNr: Int = 0
        var newAvgSpeed: Float = newSpeed

        if (prevCoordinate != null) {
            currSequenceNr = prevCoordinate.sequenceNr + 1
            newAvgSpeed = prevCoordinate.avgspeed + ((newSpeed - prevCoordinate.avgspeed) / currSequenceNr)
            newDistance = prevCoordinate.distance + newDistance
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
    }

    fun getCoordinateList(tripStartTime: Long): List<TripCoordinate> {
        return coordinateDao.getAllOfTrip(tripStartTime)
    }

    fun getLastCoordinateOfTrip(tripStartTime: Long): TripCoordinate? {
        return coordinateDao.getLastOfTrip(tripStartTime)
    }
}