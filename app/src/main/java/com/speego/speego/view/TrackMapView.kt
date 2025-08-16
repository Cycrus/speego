import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.Marker
import androidx.core.graphics.drawable.toDrawable
import org.osmdroid.views.CustomZoomButtonsController


data class WaypointMarker(
    val position: GeoPoint,
    val title: String = "",
    val description: String = "",
    val icon: Int? = android.R.drawable.ic_menu_mylocation,
    val onClickCallback: (() -> Unit)? = null // Custom click callback
)


data class TrackSegment(
    val points: List<GeoPoint>,
    val color: Color = Color.Blue,
    val width: Float = 8f
)


class TrackMapView {
    private var mapView: MapView? = null

    @Composable
    fun Build(
        modifier: Modifier = Modifier,
        latitude: Double = 47.0667,
        longitude: Double = 15.45,
        zoom: Double = 12.0,
        trackSegments: List<TrackSegment> = emptyList(),
        waypoints: List<WaypointMarker> = emptyList()
    ) {
        Box(modifier = modifier) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(0.dp))
            ) {
                CreateMap(latitude, longitude, zoom, trackSegments, waypoints)
            }
        }
    }

    @Composable
    fun CreateMap(
        latitude: Double = 47.0667,
        longitude: Double = 15.45,
        zoom: Double = 12.0,
        trackSegments: List<TrackSegment> = emptyList(),
        waypoints: List<WaypointMarker> = emptyList()
    ) {
        val context = LocalContext.current

        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    // Store reference for later updates
                    mapView = this

                    // Satellite options:
                    //setTileSource(TileSourceFactory.USGS_SAT) // USGS Satellite imagery
                    setTileSource(TileSourceFactory.MAPNIK) // Standard OpenStreetMap

                    setMultiTouchControls(true) // Enable touch controls
                    zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                    controller.setZoom(zoom)
                    controller.setCenter(GeoPoint(latitude, longitude))

                    // Add all track segments
                    trackSegments.forEach { segment ->
                        if (segment.points.isNotEmpty()) {
                            val polyline = Polyline().apply {
                                setPoints(segment.points)
                                color = segment.color.toArgb()
                                width = segment.width
                            }
                            overlays.add(polyline)
                        }
                    }

                    // Add waypoint markers
                    waypoints.forEach { waypoint ->
                        val marker = Marker(this).apply {
                            position = waypoint.position
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            title = waypoint.title
                            snippet = waypoint.description

                            // Set custom click callback
                            waypoint.onClickCallback?.let { callback ->
                                setOnMarkerClickListener { _, _ ->
                                    callback()
                                    true // Return true to consume the event
                                }
                            }

                            if (waypoint.icon == null) {
                                // Invisible dummy icon shape.
                                val tinyCircle = GradientDrawable().apply {
                                    shape = GradientDrawable.OVAL
                                    setColor(android.graphics.Color.argb(0, 0, 0, 0))
                                    setSize(80, 80)
                                }
                                icon = tinyCircle
                            }
                            else {
                                icon = ctx.getDrawable(waypoint.icon)
                            }
                        }
                        overlays.add(marker)
                    }

                    // Force invalidate to refresh
                    invalidate()
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    // Method to update map center
    fun updateCenter(latitude: Double, longitude: Double, zoom: Double? = null, animate: Boolean = true) {
        mapView?.let { map ->
            val newCenter = GeoPoint(latitude, longitude)
            if (animate) {
                map.controller.animateTo(newCenter)
                zoom?.let { map.controller.setZoom(it) }
            } else {
                map.controller.setCenter(newCenter)
                zoom?.let { map.controller.setZoom(it) }
            }
        }
    }

    // Method to add a single waypoint marker
    fun addWaypoint(
        latitude: Double,
        longitude: Double,
        title: String = "",
        description: String = "",
        iconRes: Int? = null
    ) {
        mapView?.let { map ->
            val marker = Marker(map).apply {
                position = GeoPoint(latitude, longitude)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                this.title = title
                snippet = description

                if (iconRes == null) {
                    // Invisible dummy icon shape.
                    val tinyCircle = GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(android.graphics.Color.argb(0, 0, 0, 0))
                        setSize(80, 80)
                    }
                    icon = tinyCircle
                }
                else {
                    icon = map.context.getDrawable(iconRes)
                }
            }
            map.overlays.add(marker)
            map.invalidate()
        }
    }

    // Method to clear all waypoints
    fun clearWaypoints() {
        mapView?.let { map ->
            map.overlays.removeAll { it is Marker }
            map.invalidate()
        }
    }

    // Method to clear all overlays (tracks and waypoints)
    fun clearAllOverlays() {
        mapView?.let { map ->
            map.overlays.clear()
            map.invalidate()
        }
    }

    // Method to fit map to show all waypoints and tracks
    fun fitToContent(padding: Int = 50) {
        mapView?.let { map ->
            val boundingBox = map.overlayManager.overlaysReversed()
                .filterIsInstance<Marker>()
                .map { it.position }
                .takeIf { it.isNotEmpty() }
                ?.let { points ->
                    val minLat = points.minOf { it.latitude }
                    val maxLat = points.maxOf { it.latitude }
                    val minLon = points.minOf { it.longitude }
                    val maxLon = points.maxOf { it.longitude }

                    org.osmdroid.util.BoundingBox(maxLat, maxLon, minLat, minLon)
                }

            boundingBox?.let { bbox ->
                map.zoomToBoundingBox(bbox, false, padding)
            }
        }
    }
}
