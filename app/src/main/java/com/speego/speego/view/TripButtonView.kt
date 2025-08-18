package com.speego.speego.view

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.text.format.DateFormat
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.speego.speego.viewmodel.TripButtonViewModel
import com.speego.speego.viewmodel.TripStats
import java.util.Locale

class TripButtonView(val newTrip: Boolean = false, val startTime: Long = 0, val onClick: () -> Unit,
                     val removeCallback: (() -> Unit)? = null) {
    private var buttonViewModel: TripButtonViewModel = TripButtonViewModel(startTime)
    @Composable
    fun BuildComposable() {
        val tripStats by buttonViewModel.getTripStatsContainer().observeAsState()
        buttonViewModel.fetchTripStats()

        Box(Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(horizontal = 5.dp, vertical = 5.dp)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            TextButton(onClick = onClick,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()) {
                if (newTrip) {
                    Text(text = "START",
                        fontSize = 60.sp,
                        )
                } else {
                    if (tripStats != null) {
                        Description(tripStats!!)
                    }
                }
            }

            if (removeCallback != null) {
                Button(
                    onClick = removeCallback,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "×",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }

    @Composable
    fun Description(tripStats: TripStats) {
        val dateString: String = DateFormat.format("yyyy MMM dd\nHH:mm", tripStats.startTime).toString()
        val totalDurationMinutes = tripStats.duration / (1000 * 60)
        val durationHours = totalDurationMinutes / 60
        val durationMinutes = totalDurationMinutes % 60
        val totalDurationSeconds = tripStats.duration / 1000
        val durationSeconds = totalDurationSeconds - totalDurationMinutes * 60
        val locationString: String = getAddressFromLocation(
            LocalContext.current,
            tripStats.latitude,
            tripStats.longitude
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side - Date and location
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = locationString,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Right side - Main stats stacked vertically
            Column(
                modifier = Modifier.weight(1.2f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Duration
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = if (durationHours > 0) {
                            "%dh%02dm".format(durationHours, durationMinutes)
                        } else {
                            "%02d:%02d".format(durationMinutes, durationSeconds)
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Time",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Distance
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "%.1f".format(tripStats.distance),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "km",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Speed
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "%.1f".format(tripStats.avgSpeed),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "km/h",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }

    fun getAddressFromLocation(
        context: Context,
        latitude: Double,
        longitude: Double,
        maxResults: Int = 1
    ): String {
        val geocoder = Geocoder(context, Locale.getDefault())

        val addresses = geocoder.getFromLocation(latitude, longitude, maxResults)
        if (addresses.isNullOrEmpty()) {
            return "---"
        } else {
            return addresses[0].locality
        }
    }
}