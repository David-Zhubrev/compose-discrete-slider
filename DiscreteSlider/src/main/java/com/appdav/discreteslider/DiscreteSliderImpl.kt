package com.appdav.discreteslider

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.Slider
import androidx.compose.material.SliderColors
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

@Composable
internal fun DiscreteSliderImpl(
    elements: Collection<*>,
    currentIndex: Int,
    onCurrentIndexChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    tickMark: TickMark = TickMark.None,
    enabled: Boolean = true,
    colors: SliderColors = SliderDefaults.colors(),
) {
    assert(elements.isNotEmpty()) { "Provided collection is empty" }
    assert(currentIndex in elements.indices) { "Current index is out of elements range" }
    val step = remember(elements) { 1f / (elements.size - 1) }
    val positions = remember(elements) { elements.indices.map { it * step } }
    val scope = rememberCoroutineScope()
    var job: Job? by remember { mutableStateOf(null) }
    DisposableEffect(currentIndex) {
        onDispose {
            job?.cancel()
            job = null
        }
    }
    BoxWithConstraints(
        modifier = Modifier
            .requiredHeightIn(min = 16.dp)
            .then(modifier),
        contentAlignment = Alignment.Center,
        true
    ) {
        Slider(
            modifier = modifier,
            colors = colors,
            enabled = true,
            value = positions[currentIndex],
            onValueChange = { newValue ->
                job = scope.launch {
                    onCurrentIndexChanged(
                        positions.indexOf(
                            positions.findClosestValue(newValue)
                        )
                    )
                }
            }
        )
        if (tickMark != TickMark.None) {
            val activeColor by colors.tickColor(enabled = enabled, active = true)
            val inactiveColor by colors.tickColor(enabled = enabled, active = false)
            val height = tickMark.getCanvasHeight()
            val drawPadding = with(LocalDensity.current) { 10.dp.toPx() }
            Canvas(
                Modifier
                    .width(maxWidth)
                    .height(height)
            ) {
                val distance = (size.width - 2 * drawPadding) / (elements.size - 1)
                currentIndex.let {
                    positions.indices.forEach { index ->
                        val x = drawPadding + index * distance
                        val color = if (index <= currentIndex) activeColor else inactiveColor
                        tickMark.draw(
                            this,
                            color,
                            x,
                            IndexInfo(positions.indices, index, currentIndex)
                        )
                    }
                }
            }
        }
    }
}

private suspend fun List<Float>.findClosestValue(value: Float): Float {
    return withContext(Dispatchers.IO) {
        var result = get(0)
        forEach { current ->
            val diff = abs(current - value)
            val resultDiff = abs(result - value)
            if (diff < resultDiff) {
                result = current
            } else {
                return@forEach
            }
        }
        return@withContext result
    }
}

