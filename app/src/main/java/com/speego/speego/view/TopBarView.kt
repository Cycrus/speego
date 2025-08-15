package com.speego.speego.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class TopBarView {
    @Composable
    fun Build() {
        Box(Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .height(80.dp)
                .background(Color(0, 0, 60)),
            contentAlignment = Alignment.Center) {
            Text(text = "SpeeGo Running Tracker", fontSize = 30.sp, color = Color(200, 200, 200))
        }
    }
}