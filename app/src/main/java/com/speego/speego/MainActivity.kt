package com.speego.speego

// TODO: Implement Selection View
// TODO: Implement Trip View
// TODO: Implement Models
// TODO: Implement GNSS Service
// TODO: Support Maps and Satellite Images
// TODO: Final Goal --> Show Speed Heatmap

import TrackMapView
import TrackSegment
import WaypointMarker
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.speego.speego.ui.theme.SpeeGoTheme
import com.speego.speego.view.SelectView
import com.speego.speego.view.TopBarView
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint

class MainActivity : ComponentActivity() {
    private val selectView = TrackMapView()
    private val topBar = TopBarView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", 0))

        setContent {
            SpeeGoTheme {
                Scaffold(
                    topBar = { topBar.Build() },
                ) { innerPadding ->  // PaddingValues passed by Scaffold
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(10.dp) // optional extra padding
                    ) {
                        RouteWithWaypoints()
                    }
                }
            }
        }
    }

    @Composable
    fun SpeedBasedRoute() {
        val segments = listOf(
            // Slow speed - Red
            TrackSegment(
                points = listOf(
                    GeoPoint(47.0667, 15.45),
                    GeoPoint(47.07, 15.46)
                ),
                color = Color.Red,
                width = 14f
            ),
            // Medium speed - Yellow
            TrackSegment(
                points = listOf(
                    GeoPoint(47.07, 15.46),
                    GeoPoint(47.08, 15.48)
                ),
                color = Color.Yellow,
                width = 14f
            ),
            // Fast speed - Green
            TrackSegment(
                points = listOf(
                    GeoPoint(47.08, 15.48),
                    GeoPoint(47.10, 15.52)
                ),
                color = Color.Green,
                width = 14f
            )
        )

        selectView.Build(
            latitude = 47.08,
            longitude = 15.48,
            zoom = 14.0,
            trackSegments = segments
        )
    }

    @Composable
    fun RouteWithWaypoints() {
        val trackMap = TrackMapView()

        val segments = listOf(
            TrackSegment(
                points = listOf(
                    GeoPoint(47.0667, 15.45),   // Graz
                    GeoPoint(47.4, 16.0),       // Bruck
                    GeoPoint(48.2082, 16.3738)  // Vienna
                ),
                color = Color.Blue,
                width = 8f
            )
        )

        val waypoints = listOf(
            WaypointMarker(
                position = GeoPoint(47.0667, 15.45),
                title = "Start",
                description = "Graz - Starting point"
            ),
            WaypointMarker(
                position = GeoPoint(47.4, 16.0),
                title = "Waypoint",
                description = "Bruck an der Mur"
            ),
            WaypointMarker(
                position = GeoPoint(48.2082, 16.3738),
                title = "Finish",
                description = "Vienna - End point"
            )
        )

        trackMap.Build(
            latitude = 47.6,
            longitude = 16.0,
            zoom = 8.0,
            trackSegments = segments,
            waypoints = waypoints
        )
    }
}