package com.appdav.discreteslider

import androidx.compose.material.SliderColors
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun IntProgressionSlider(
    progression: IntProgression,
    selected: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tickMark: TickMark = TickMark.None,
    colors: SliderColors = SliderDefaults.colors()
) {
    CollectionSlider(
        collection = progression.toList(),
        selected = selected,
        onSelected = onSelected,
        modifier = modifier,
        colors = colors,
        enabled = enabled,
        tickMark = tickMark
    )
}


@Composable
fun <T : Any> CollectionSlider(
    collection: Collection<T>,
    selected: T,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tickMark: TickMark = TickMark.None,
    colors: SliderColors = SliderDefaults.colors()
) {
    assert(collection.size > 1) { "Collection must contain at least 2 elements" }
    assert(selected in collection) { "Selected element is not found inside collection" }
    val currentIndex by remember(selected) {
        mutableStateOf(collection.indexOf(selected)).takeUnless { it.value == -1 }
            ?: throw IllegalStateException("Selected element is not found inside collection")
    }
    DiscreteSliderImpl(
        elements = collection,
        currentIndex = currentIndex,
        onCurrentIndexChanged = { index ->
            onSelected(collection.elementAt(index))
        },
        modifier = modifier,
        colors = colors,
        tickMark = tickMark,
        enabled = enabled
    )
}