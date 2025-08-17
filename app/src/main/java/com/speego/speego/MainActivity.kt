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
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.speego.speego.database.TripDatabaseInterface
import com.speego.speego.gnss_service.GnssService
import com.speego.speego.ui.theme.SpeeGoTheme
import com.speego.speego.view.SelectView
import com.speego.speego.view.SummaryView
import com.speego.speego.view.TopBarView
import com.speego.speego.view.TripView
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint

class MainActivity : ComponentActivity() {
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            // Location permission granted, now check notification permission
            checkNotificationPermission()
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // All permissions granted, can start service
            Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    private val selectView = SelectView()
    private val tripView = TripView()
    private val summaryView = SummaryView()
    private val topBar = TopBarView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", 0))
        TripDatabaseInterface.init(this)

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
                        checkAndRequestPermissions()
                        SetupNavigation()
                    }
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // Check location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            locationPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            checkNotificationPermission()
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    @Composable
    fun SetupNavigation() {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = "selectview"
        ) {
            composable("selectview") {
                selectView.Build(navController)
            }

            composable("tripview") {
                tripView.Build(navController)
            }

            composable("summaryview") {
                summaryView.Build(navController)
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
            zoom = 8.0
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
            zoom = 8.0
        )
    }
}