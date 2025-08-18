package com.speego.speego.view

import TrackMapView
import android.content.Context
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    private var currSequenceNr: Int = 0


    @Composable
    fun Build(navController: NavController) {
        val coordinateUpdatedData by tripViewModel.getCoordinateUpdatedContainer().observeAsState()
        val coordinateListData by tripViewModel.getCoordinateListContainer().observeAsState()
        val context = LocalContext.current

        BackHandler { }

        // This only checks if new data exists and instructs to fetch the required last n data points
        // Required for the app to be able to close down in between and still correctly work
        LaunchedEffect(coordinateUpdatedData) {
            coordinateUpdatedData?.let { coordinate ->
                val newSequenceNr: Int = coordinate.sequenceNr
                val sequenceNrDelta: Int = newSequenceNr - currSequenceNr
                currSequenceNr = newSequenceNr
                if (sequenceNrDelta > 0)
                    tripViewModel.postCoordinateList(GlobalModel.getCurrentTripName(), sequenceNrDelta)
            }
        }

        // This gets the last n fetched datapoints and inserts them.
        LaunchedEffect(coordinateListData) {
            coordinateListData?.let { coordinateList ->
                mapView.drawFullTrack(coordinateList)
                mapView.updatePositionMarker()
                mapView.renderMap()
            }
        }

        GnssService.startForegroundService(context)
        Log.d("TripView", "Current trip name = " + GlobalModel.getCurrentTripName())
        Column(Modifier
            .fillMaxSize()
            .background(Color(54, 54, 54, 255)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {

            BuildMapView(navController, context, coordinateUpdatedData)
            BuildDataView(coordinateUpdatedData)
        }
    }

    @Composable
    fun BuildMapView(navController: NavController, context: Context, coordinateData: TripCoordinate?) {
        Column(Modifier.fillMaxHeight(0.6f)) {
            Box(Modifier.fillMaxSize()) {
                mapView.Build(Modifier.fillMaxHeight(0.95f)) // Map takes most space
                Button(
                    onClick = {
                        GnssService.stopForegroundService(context)
                        tripViewModel.setTripFinished(GlobalModel.getCurrentTripName())
                        mapView.clearAllOverlays()
                        navController.navigate("summaryview")
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                ) {
                    Text(text = "Stop")
                }
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
                    Text("Run distance:")
                    Text("%.2f".format(coordinateData.distance) + " km")
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
                    val totalDurationMinutes = coordinateData.duration / (1000 * 60)
                    val durationHours = totalDurationMinutes / 60
                    val durationMinutes = totalDurationMinutes % 60
                    val totalDurationSeconds = coordinateData.duration / 1000
                    val durationSeconds = totalDurationSeconds - totalDurationMinutes * 60
                    Text("Run duration:")
                    Text("%02d:%02d.%02d h\n".format(durationHours, durationMinutes, durationSeconds))
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
                    Text("Current speed:")
                    Text("%.2f km/h".format(coordinateData.speed))
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
                    Text("Average speed:")
                    Text("%.2f km/h".format(coordinateData.avgspeed))
                }
            }
        }
    }
}