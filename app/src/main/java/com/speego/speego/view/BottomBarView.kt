package com.speego.speego.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class BottomBarView {
    @Composable
    fun Build() {
        Box(Modifier
            .systemBarsPadding()
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center) {
            Text(text = "SpeeGo v1.0.0", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}