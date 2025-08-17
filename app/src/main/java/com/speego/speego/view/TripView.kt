package com.speego.speego.view

import TrackMapView
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.speego.speego.database.TripCoordinate
import com.speego.speego.gnss_service.GnssService
import com.speego.speego.model.GlobalModel
import com.speego.speego.viewmodel.TripViewModel

class TripView {
    private var tripViewModel = TripViewModel()
    private var mapView: TrackMapView = TrackMapView()


    @Composable
    fun Build(navController: NavController) {
        val coordinateData by tripViewModel.getCoordinateContainer().observeAsState()
        val context = LocalContext.current

        GnssService.startForegroundService(context)
        Log.d("TripView", "Current trip name = " + GlobalModel.getCurrentTripName())
        Column(Modifier
            .fillMaxSize()
            .background(Color(54, 54, 54, 255)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {

            BuildMapView(navController, context, coordinateData)
            BuildDataView(coordinateData)

        }
    }

    @Composable
    fun BuildMapView(navController: NavController, context: Context, coordinateData: TripCoordinate?) {
        Row(Modifier.fillMaxHeight(0.6f)) {
            mapView.Build()
            Button(onClick = {
                GnssService.stopForegroundService(context)
                navController.navigate("summaryview")
            }) {
                Text(text = "Stop")
            }
        }
    }

    @Composable
    fun BuildDataView(coordinateData: TripCoordinate?) {
        if (coordinateData == null) {
            Text("Waiting for GNSS fix...")
            return
        }

        // Stretch over full available space
        Column(Modifier.fillMaxSize()) {
            // First row - takes up half the height
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Top-left cell
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(coordinateData.latitude.toString() + ":\n" + coordinateData.longitude.toString())
                }

                // Vertical separator line
                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp),
                    thickness = DividerDefaults.Thickness, color = Color.Gray
                )

                // Top-right cell
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(coordinateData.distance.toString() + " km")
                }
            }

            // Horizontal separator line
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                thickness = DividerDefaults.Thickness, color = Color.Gray
            )

            // Second row - takes up the other half of the height
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Bottom-left cell
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(coordinateData.duration.toString() + " ms")
                }

                // Vertical separator line
                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp),
                    thickness = DividerDefaults.Thickness, color = Color.Gray
                )

                // Bottom-right cell
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(coordinateData.avgspeed.toString() + " km/h")
                }
            }
        }
    }
}