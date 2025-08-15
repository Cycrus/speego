package com.speego.speego

// TODO: Implement Selection View
// TODO: Implement Trip View
// TODO: Implement Models
// TODO: Implement GNSS Service
// TODO: Support Maps and Satellite Images
// TODO: Final Goal --> Show Speed Heatmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.speego.speego.ui.theme.SpeeGoTheme
import com.speego.speego.view.SelectView
import com.speego.speego.view.TopBarView

class MainActivity : ComponentActivity() {
    private val selectView = SelectView()
    private val topBar = TopBarView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SpeeGoTheme {
                Scaffold(
                    topBar = { topBar.Build() },
                ) { innerPadding ->  // PaddingValues passed by Scaffold
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(10.dp) // optional extra padding
                    ) {
                        selectView.Build()
                    }
                }
            }
        }
    }
}