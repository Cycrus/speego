import android.graphics.drawable.GradientDrawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.speego.speego.database.TripCoordinate
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline


data class TrackSegment(
    val points: List<GeoPoint>,
    val color: Color = Color.Blue,
    val width: Float = 10f
)


class TrackMapView {
    private var mapView: MapView? = null
    private var positionMarker: Marker? = null
    private var prevCoordinate: TripCoordinate? = null
    private var currCoordinate: TripCoordinate? = null

    @Composable
    fun Build(
        modifier: Modifier = Modifier,
        latitude: Double = 47.0667,
        longitude: Double = 15.45,
        zoom: Double = 18.0
    ) {
        Box(modifier = modifier) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(0.dp))
            ) {
                CreateMap(latitude, longitude, zoom)
            }
        }
    }

    @Composable
    fun CreateMap(
        latitude: Double = 47.0667,
        longitude: Double = 15.45,
        zoom: Double = 18.0
    ) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    // Store reference for later updates
                    mapView = this

                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                    controller.setZoom(zoom)
                    controller.setCenter(GeoPoint(latitude, longitude))

                    // Force invalidate to refresh
                    renderMap()
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    fun drawFullTrack(coordinates: List<TripCoordinate>, rescaleToTrack: Boolean = false) {
        for (coordinate in coordinates) {
            setNewCoordinate(coordinate)
            updateTrack()
        }

        if (rescaleToTrack)
            fitToContent()
    }

    fun setNewCoordinate(coordinate: TripCoordinate) {
        this.prevCoordinate = this.currCoordinate
        this.currCoordinate = coordinate
    }

    fun interpolateColor(
        minValue: Float,
        maxValue: Float,
        currentValue: Float,
        startColor: Color,
        endColor: Color
    ): Color {
        val clampedValue = currentValue.coerceIn(minValue, maxValue)

        val fraction = if (maxValue == minValue) {
            0f
        } else {
            (clampedValue - minValue) / (maxValue - minValue)
        }

        return lerp(startColor, endColor, fraction)
    }

    fun updateTrack(trackColor: Color? = null) {
        if (this.prevCoordinate == null)
            return
        val prevPoint = GeoPoint(this.prevCoordinate!!.latitude, this.prevCoordinate!!.longitude)
        val currPoint = GeoPoint(this.currCoordinate!!.latitude, this.currCoordinate!!.longitude)
        val color: Color = trackColor
            ?: interpolateColor(
                minValue = 0.0f,
                maxValue = 20.0f,
                currentValue = this.currCoordinate!!.speed,
                startColor = Color.Blue,
                endColor = Color.Red)
        val newTrackSegment = TrackSegment(
            points = listOf(prevPoint, currPoint),
            color = color
        )
        addTrackSegment(newTrackSegment)
    }

    fun addTrackSegment(segment: TrackSegment) {
        if (this.mapView == null)
            return

        if (segment.points.isNotEmpty()) {
            val polyline = Polyline().apply {
                setPoints(segment.points)
                color = segment.color.toArgb()
                width = segment.width
            }
            this.mapView!!.overlays.add(polyline)
        }
    }

    fun renderMap() {
        this.mapView!!.invalidate()
    }

    fun updatePositionMarker() {
        if (this.currCoordinate == null)
            return
        updatePositionWaypoint(this.currCoordinate!!)
        updateCenter(latitude = this.currCoordinate!!.latitude, longitude = this.currCoordinate!!.longitude)
    }

    fun updatePositionWaypoint(coordinate: TripCoordinate) {
        if (this.positionMarker == null) {
            this.positionMarker = addWaypoint(latitude = coordinate.latitude, longitude = coordinate.longitude,
                iconRes = android.R.drawable.ic_menu_mylocation)
        }
        else {
            val newPosition = GeoPoint(coordinate.latitude, coordinate.longitude)
            this.positionMarker!!.setPosition(newPosition)
        }
    }

    // Method to update map center
    fun updateCenter(latitude: Double, longitude: Double, zoom: Double? = null, animate: Boolean = true) {
        mapView?.let { map ->
            val newCenter = GeoPoint(latitude, longitude)
            if (animate) {
                map.controller.animateTo(newCenter, map.zoomLevelDouble, 200L)
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
        iconRes: Int? = null
    ): Marker? {
        var marker: Marker? = null
        mapView?.let { map ->
            marker = Marker(map).apply {
                position = GeoPoint(latitude, longitude)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

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
        }
        return marker
    }

    // Method to clear all waypoints
    fun clearWaypoints() {
        mapView?.let { map ->
            map.overlays.removeAll { it is Marker }
        }
    }

    // Method to clear all overlays (tracks and waypoints)
    fun clearAllOverlays() {
        mapView?.let { map ->
            map.overlays.clear()
        }

        this.currCoordinate = null
        this.prevCoordinate = null
        this.positionMarker = null
    }

    // Method to fit map to show all waypoints and tracks
    fun fitToContent(padding: Int = 50) {
        mapView?.let { map ->
            val allPoints = mutableListOf<GeoPoint>()

            map.overlayManager.overlaysReversed().forEach { overlay ->
                when (overlay) {
                    is Marker -> allPoints.add(overlay.position)
                    is Polyline -> allPoints.addAll(overlay.actualPoints)
                    is Polygon -> allPoints.addAll(overlay.actualPoints)
                }
            }

            if (allPoints.isNotEmpty()) {
                val boundingBox = BoundingBox.fromGeoPoints(allPoints)
                map.zoomToBoundingBox(boundingBox, false, padding)
            }
        }
    }
}
