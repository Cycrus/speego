package com.speego.speego.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.speego.speego.database.TripEntry
import com.speego.speego.model.GlobalModel
import com.speego.speego.viewmodel.SelectionViewModel

class SelectView {
    private var selectionViewModel: SelectionViewModel = SelectionViewModel()
    private val tripButtons: MutableList<TripButtonView> = mutableListOf()

    @Composable
    fun Build(navController: NavController) {
        val tripsData by selectionViewModel.getTripsContainer().observeAsState()
        val newTripName by selectionViewModel.getNewTripNameContainer().observeAsState()

        // TODO: JUST FOR DEBUGGING
        /*LaunchedEffect(Unit) {
            selectionViewModel.createNewTrip()
        }*/
        selectionViewModel.getAllTrips()

        Column(Modifier
                .fillMaxSize()
                .background(Color(54, 54, 54, 255))
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
            CreateTripButtons(tripsData, navController)
        }
    }

    @Composable
    fun CreateTripButtons(trips: List<TripEntry>?, navController: NavController) {
        if (trips == null)
            return
        val numButtons = trips.size

        tripButtons.clear()
        for (i in 0..numButtons) {
            if (i == 0)
                tripButtons.add(TripButtonView(newTrip = true,
                    onClick = {
                        val newTripName: Long = System.currentTimeMillis()
                        GlobalModel.setCurrentTripName(newTripName)
                        selectionViewModel.createNewTrip(newTripName)
                        navController.navigate("tripview")
                    }))
            else
                tripButtons.add(TripButtonView(newTrip = false, startTime = trips[i - 1].startTime,
                    onClick = {
                        navController.navigate("summaryview")
                    },
                    removeCallback = {
                        removeTrack(trips[i - 1].startTime)
                    }))
        }

        for(button in tripButtons) {
            button.Build()
        }
    }

    fun removeTrack(trackName: Long) {
        selectionViewModel.removeTrack(trackName)
    }
}
