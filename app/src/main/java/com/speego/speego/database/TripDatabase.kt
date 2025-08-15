package com.speego.speego.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [TripEntry::class, TripCoordinate::class], version = 1)
abstract class TripDatabase : RoomDatabase() {
    abstract fun tripEntryDao(): TripEntryDao
    abstract fun tripCoordinateDao(): TripCoordinateDao
}