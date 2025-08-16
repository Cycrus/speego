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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.speego.speego.ui.theme.SpeeGoTheme
import com.speego.speego.view.SelectView
import com.speego.speego.view.TopBarView
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint

class MainActivity : ComponentActivity() {
    private val selectView = SelectView()
    private val topBar = TopBarView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", 0))

        setContent {
            SpeeGoTheme {
                Scaffold(
                    topBar = { topBar.Build() },
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        InteractiveWaypoints()
                    }
                }
            }
        }
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

    @Composable
    fun InteractiveWaypoints() {
        val trackMap = TrackMapView()

        val waypoints = listOf(
            WaypointMarker(
                position = GeoPoint(47.0667, 15.45),
                title = "Graz",
                description = "Starting point",
                onClickCallback = {
                    // Custom action when marker is clicked
                    println("Clicked on Graz!")
                    // You can update UI state, show dialogs, navigate, etc.
                }
            ),
            WaypointMarker(
                position = GeoPoint(48.2082, 16.3738),
                title = "Vienna",
                description = "Capital city",
                icon = null,
                onClickCallback = {
                    println("Clicked on Vienna!")
                    // Another custom action
                }
            )
        )

        trackMap.Build(
            modifier = Modifier
                .height(LocalConfiguration.current.screenHeightDp.dp / 2),
            latitude = 47.6,
            longitude = 16.0,
            zoom = 8.0,
            waypoints = waypoints
        )
    }
}