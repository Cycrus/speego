package com.speego.speego.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Query


@Entity(
    primaryKeys = ["startTime"]
)
data class TripEntry(
    val startTime: Long,    // milliseconds
    val finished: Boolean
)


@Dao
interface TripEntryDao {
    @Query("SELECT * FROM tripentry")
    fun getAll(): List<TripEntry>

    @Query("SELECT * FROM tripentry WHERE startTime = :startTime")
    fun findByStartTime(startTime: Long): TripEntry

    @Delete
    fun delete(tripEntry: TripEntry)
}