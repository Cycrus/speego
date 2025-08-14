package com.speego.speego


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TestViewModel : ViewModel() {
    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter

    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text

    fun setCounter(value: Int) {
        _counter.value = value
    }

    fun getCounter(): Int {
        return _counter.value;
    }

    fun setText(value: String) {
        _text.value = value
    }

    fun getText(): String {
        return _text.value
    }
}

class TestActivity : ComponentActivity() {
    private var viewmodel = TestViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val counter by viewmodel.counter.collectAsState()
            val text by viewmodel.text.collectAsState()
            var tmpText by remember { mutableStateOf("") }

            Box(modifier = Modifier.fillMaxSize()) {
                ShowValue(counter.toString(), Modifier.align(Alignment.TopCenter).padding(top = 16.dp))
                ShowValue(text, Modifier.align(Alignment.TopCenter).padding(top = 50.dp))
                ShowButton(Modifier.align(Alignment.Center) ) {
                    viewmodel.setCounter(viewmodel.getCounter() + 1)
                    viewmodel.setText(tmpText)
                }
                ShowTextBox(tmpText, { newText -> tmpText = newText },
                    Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp))
            }
        }
    }

    @Composable
    fun ShowValue(value: String, modifier: Modifier = Modifier) {
        MaterialTheme {
            Surface(modifier = modifier) {
                Text(text = "Value = $value")
            }
        }
    }

    @Composable
    fun ShowButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
        Button(onClick = onClick,
            modifier = modifier) {
            Text(text = "Increase Value")
        }
    }

    @Composable
    fun ShowTextBox(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Enter Text") },
            modifier = modifier
        )
    }
}

/* To install a package
    1. Write repositories into libs.version.toml under [libraries]
    2. Include libraries with the names defined in step 1 in build.gradle.kts
    3. Sync gradle
    4. Write a service in the AndroidManifest.xml (Only if necessary for background daemons)
*/