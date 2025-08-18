package com.speego.speego.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
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
    val avgspeed: Float,        // km/h
    val duration: Long,         // milliseconds
    val distance: Float         // km
)


@Dao
interface TripCoordinateDao {
    @Query("""
        SELECT * FROM TripCoordinate
        WHERE tripStartTime = :tripStartTime
        ORDER BY sequenceNr ASC
        """)
    fun getAllOfTrip(tripStartTime: Long): List<TripCoordinate>

    @Query("""
        SELECT * FROM TripCoordinate WHERE tripStartTime = :tripStartTime
            AND sequenceNr = (
              SELECT MAX(sequenceNr) 
              FROM TripCoordinate 
              WHERE tripStartTime = :tripStartTime
          )
          ORDER BY sequenceNr DESC 
          """)
    fun getLastOfTrip(tripStartTime: Long) : TripCoordinate?

    @Query("""
    SELECT * FROM (
        SELECT * FROM TripCoordinate
        WHERE tripStartTime = :tripStartTime
        ORDER BY sequenceNr DESC
        LIMIT :n
    )
    ORDER BY sequenceNr ASC
""")
    fun getLastNOfTrip(tripStartTime: Long, n: Int) : List<TripCoordinate>

    @Insert
    fun addNewCoordinate(tripCoordinate: TripCoordinate)
}