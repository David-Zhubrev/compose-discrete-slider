package com.appdav.discreteslider

data class IndexInfo(
    val indices: IntRange,
    val currentIndex: Int,
    val selectedIndex: Int
) {
    val first: Int
        get() = indices.first
    val last: Int
        get() = indices.last
}