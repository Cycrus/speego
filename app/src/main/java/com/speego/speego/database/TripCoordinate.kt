package com.speego.speego.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Query


@Entity(
    primaryKeys = ["tripStartTime", "sequenceNr"],
    foreignKeys = [
        ForeignKey(
            entity = TripEntry::class,
            parentColumns = ["startTime"],
            childColumns = ["tripStartTime"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["tripStartTime", "sequenceNr"])]
)
data class TripCoordinate(
    val tripStartTime: Long,    // milliseconds
    val sequenceNr: Int,
    val latitude: Double,
    val longitude: Double,
    val speed: Float,           // km/h
    val duration: Long,         // milliseconds
    val distance: Float         // km
)


@Dao
interface TripCoordinateDao {
    @Query("SELECT * FROM tripcoordinate WHERE tripStartTime = :tripStartTime")
    fun getAllOfTrip(tripStartTime: Long): List<TripEntry>

    @Query("""
        SELECT * FROM tripcoordinate WHERE tripStartTime = :tripStartTime
            AND sequenceNr = (
              SELECT MAX(sequenceNr) 
              FROM TripCoordinate 
              WHERE tripStartTime = :tripStartTime
          )
          """)
    fun getLastOfTrip(tripStartTime: Long) : TripEntry

    @Query("SELECT * FROM tripentry WHERE startTime LIKE :startTime")
    fun findByStartTime(startTime: Long): TripEntry
}