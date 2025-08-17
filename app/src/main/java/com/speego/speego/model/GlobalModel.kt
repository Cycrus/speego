package com.speego.speego.model

object GlobalModel {
    private var currentTripName: Long = 0

    fun setCurrentTripName(tripName: Long) {
        this.currentTripName = tripName
    }

    fun getCurrentTripName(): Long {
        return this.currentTripName
    }
}