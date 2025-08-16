package com.speego.speego.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.speego.speego.viewmodel.TripButtonViewModel

class TripButtonView(val newTrip: Boolean = false, val startTime: Long = 0, val onClick: () -> Unit,
                     val removeCallback: (() -> Unit)? = null) {
    private var buttonViewModel: TripButtonViewModel = TripButtonViewModel(startTime)
    @Composable
    fun Build() {
        val tripStats by buttonViewModel.getTripStatsContainer().observeAsState()
        buttonViewModel.fetchTripStats()

        Box(Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(horizontal = 5.dp, vertical = 5.dp)
                .background(Color(148, 148, 148, 255))
        ) {
            Button(onClick = onClick,
                modifier = Modifier.align(Alignment.Center)) {
                if (newTrip) {
                    Text(text = "+", fontSize = 100.sp)
                } else {
                    Text(
                        text = tripStats?.startTime.toString() + "\n" +
                                tripStats?.duration.toString() + " ms\n" +
                                tripStats?.distance.toString() + " km\n" +
                                tripStats?.avgSpeed.toString() + " kmh\n", fontSize = 20.sp
                    )
                }
            }

            if (removeCallback != null) {
                Button(onClick = removeCallback,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)) {
                    Text("-", fontSize = 30.sp)
                }
            }
        }
    }
}