package com.speego.speego.view

import TrackMapView
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
import androidx.navigation.NavController

class SummaryView {
    private var mapView: TrackMapView = TrackMapView()

    @Composable
    fun Build(navController: NavController) {
        Column(Modifier
            .fillMaxSize()
            .background(Color(200, 54, 54, 255)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
            mapView.Build(Modifier.fillMaxHeight(0.5f))
            Button(onClick = {
                navController.navigate("selectview")
            }) {
                Text(text = "Finish")
            }
        }
    }
}