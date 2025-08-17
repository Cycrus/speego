package com.speego.speego.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.speego.speego.database.TripCoordinate
import com.speego.speego.database.TripEntry
import com.speego.speego.model.GlobalModel

class TripViewModel : ViewModel() {

    fun getCoordinateContainer(): LiveData<TripCoordinate> {
        return GlobalModel.getCoordinateContainer()
    }
}