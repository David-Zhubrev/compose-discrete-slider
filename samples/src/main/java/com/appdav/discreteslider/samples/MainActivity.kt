package com.appdav.discreteslider.samples

import android.graphics.Rect
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appdav.discreteslider.CollectionSlider
import com.appdav.discreteslider.IntProgressionSlider
import com.appdav.discreteslider.TickMark
import com.appdav.discreteslider.samples.ui.theme.SampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Content()
                }
            }
        }
    }
}


@Composable
private fun Content() {
    SampleTheme {
        Surface(Modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                //Integer progression example
                Column(Modifier.padding(32.dp)) {
                    val intProgression = remember { 0..1000 step 100 }
                    val (current, setCurrent) = remember { mutableStateOf(intProgression.first) }
                    IntProgressionSlider(
                        progression = intProgression,
                        selected = current,
                        onSelected = setCurrent,
                        tickMark = TickMark.Line(strokeWidth = 2.dp, height = 6.dp)
                    )
                    Text("Selected: $current")
                }
                //Collection slider with enumeration example
                Column(Modifier.padding(32.dp)) {
                    val collection = remember { TestEnum.values().toList() }
                    val (current, setCurrent) = remember { mutableStateOf(collection.first()) }
                    CollectionSlider(
                        collection = collection,
                        selected = current,
                        onSelected = setCurrent,
                        tickMark = TickMark.Dot(2.dp),
                        colors = SliderDefaults.colors(
                            activeTickColor = Color.Transparent,
                            inactiveTickColor = Color.Black
                        )
                    )
                    Text("Selected: ${current.name}")
                }
                //Custom TickMarker example
                Column(Modifier.padding(32.dp)) {
                    val progression = remember { 0..1000 step 250 }
                    var current by remember { mutableStateOf(progression.first()) }
                    val paintTextSize = with(LocalDensity.current) { 16.sp.toPx() }
                    val paint = remember {
                        android.graphics.Paint().apply {
                            textSize = paintTextSize
                            color = 0x000000
                        }
                    }
                    IntProgressionSlider(
                        progression = progression,
                        selected = current,
                        onSelected = { current = it },
                        tickMark = TickMark.CustomMark(100.dp) { color, xPosition, indexInfo ->
                            val verticalPadding = 32.dp.toPx()
                            val yPosition = size.height / 2 + verticalPadding
                            drawContext.canvas.nativeCanvas.apply {
                                val text = progression.elementAt(indexInfo.currentIndex).toString()
                                paint.color = color.toArgb()

                                val bounds = Rect()
                                paint.getTextBounds(text, 0, text.length, bounds)
                                drawText(
                                    text,
                                    xPosition - bounds.width() / 2,
                                    yPosition,
                                    paint.also { it.color = color.toArgb() })
                            }
                            drawCircle(color, 8.dp.toPx(), Offset(xPosition, size.height / 2))
                        },
                        colors = SliderDefaults.colors(
                            activeTickColor = MaterialTheme.colors.primary.copy(alpha = 0.5f),
                            inactiveTickColor = MaterialTheme.colors.primary.copy(alpha = 0.5f)
                        )
                    )
                    Text("Current: $current")

                }
            }
        }
    }
}

private enum class TestEnum {
    FIRST,
    SECOND,
    THIRD,
    FOURTH,
    FIFTH,
    SIXTH,
    SEVENTH,
    EIGHTS,
    NINTH
}

