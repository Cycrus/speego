package com.speego.speego.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class TripButtonView(val newTrip: Boolean = false, val startTime: Long = 0) {
    @Composable
    fun Build() {
        Box(Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(horizontal = 5.dp, vertical = 5.dp)
                .background(Color(148, 148, 148, 255)),
            contentAlignment = Alignment.Center
        ) {
            if (newTrip) {
                Text(text = "+", fontSize = 100.sp)
            }
            else {
                Text(text = startTime.toString(), fontSize = 20.sp)
            }
        }
    }
}