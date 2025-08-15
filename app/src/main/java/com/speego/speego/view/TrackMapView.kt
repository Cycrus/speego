import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.Marker

// Data class for waypoint markers
data class WaypointMarker(
    val position: GeoPoint,
    val title: String = "",
    val description: String = "",
    val icon: Int? = null // Resource ID for custom icon
)
// Data class for track segments with individual colors
data class TrackSegment(
    val points: List<GeoPoint>,
    val color: Color = Color.Blue,
    val width: Float = 8f
)

class TrackMapView {
    // Store map reference for updates
    private var mapView: MapView? = null

    @Composable
    fun Build(
        latitude: Double = 47.0667,
        longitude: Double = 15.45,
        zoom: Double = 12.0,
        trackSegments: List<TrackSegment> = emptyList(),
        waypoints: List<WaypointMarker> = emptyList()
    ) {
        val context = LocalContext.current

        AndroidView(
            factory = { ctx ->
                // Ensure configuration is loaded
                Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", 0))

                MapView(ctx).apply {
                    // Store reference for later updates
                    mapView = this

                    // Satellite options:
                    //setTileSource(TileSourceFactory.USGS_SAT) // USGS Satellite imagery
                    //setTileSource(TileSourceFactory.USGS_TOPO) // USGS Topographic
                    setTileSource(TileSourceFactory.MAPNIK) // Standard OpenStreetMap
                    //setTileSource(TileSourceFactory.WIKIMEDIA) // Wikimedia maps

                    setMultiTouchControls(true) // Enable touch controls
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
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = waypoint.title
                            snippet = waypoint.description

                            // Set custom icon if provided
                            waypoint.icon?.let { iconRes ->
                                try {
                                    icon = ctx.getDrawable(iconRes)
                                } catch (e: Exception) {
                                    // Use default icon if custom icon fails to load
                                }
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

                // Set custom icon if provided
                iconRes?.let { icon ->
                    try {
                        this.icon = map.context.getDrawable(icon)
                    } catch (e: Exception) {
                        // Use default icon if custom icon fails to load
                    }
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

    // Convenience method for single-color tracks
    @Composable
    fun BuildWithSingleTrack(
        latitude: Double = 47.0667,
        longitude: Double = 15.45,
        zoom: Double = 12.0,
        trackPoints: List<GeoPoint> = emptyList(),
        trackColor: Color = Color.Blue,
        trackWidth: Float = 8f,
        waypoints: List<WaypointMarker> = emptyList()
    ) {
        val segment = if (trackPoints.isNotEmpty()) {
            listOf(TrackSegment(trackPoints, trackColor, trackWidth))
        } else {
            emptyList()
        }

        Build(
            latitude = latitude,
            longitude = longitude,
            zoom = zoom,
            trackSegments = segment,
            waypoints = waypoints
        )
    }
}
