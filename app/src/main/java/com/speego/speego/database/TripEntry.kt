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

    // Fetching
    @Query("SELECT * FROM TripEntry ORDER BY startTime DESC ")
    fun getAll(): List<TripEntry>

    @Query("SELECT * FROM TripEntry WHERE startTime = :startTime")
    fun getByStartTime(startTime: Long): TripEntry?

    @Query("""
        SELECT * FROM TripEntry WHERE
            startTime = (
              SELECT MAX(startTime) 
              FROM TripEntry
          )
          """)
    fun getLast() : TripEntry?

    // Inserting
    @Insert
    fun createNew(tripEntry: TripEntry)

    // Deleting
    @Delete
    fun delete(tripEntry: TripEntry): Int

    @Query("DELETE FROM TripEntry")
    fun clear(): Int

    // Updating
    @Query("UPDATE TripEntry SET finished = :finished WHERE startTime = :startTime")
    fun setFinished(startTime: Long, finished: Boolean)
}