package com.speego.speego.view

import TrackMapView
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.speego.speego.gnss_service.GnssService
import com.speego.speego.model.GlobalModel

class TripView {
    private var mapView: TrackMapView = TrackMapView()

    @Composable
    fun Build(navController: NavController) {
        val context = LocalContext.current
        GnssService.startForegroundService(context)
        Log.d("TripView", "Current trip name = " + GlobalModel.getCurrentTripName())
        Column(Modifier
            .fillMaxSize()
            .background(Color(54, 54, 54, 255)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
            mapView.Build(Modifier.fillMaxHeight(0.5f))
            Button(onClick = {
                GnssService.stopForegroundService(context)
                navController.navigate("summaryview")
            }) {
                Text(text = "Stop")
            }
        }
    }
}