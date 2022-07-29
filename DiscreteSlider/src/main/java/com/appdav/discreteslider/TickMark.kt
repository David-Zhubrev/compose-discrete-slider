@file:Suppress("unused")

package com.appdav.discreteslider

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed class TickMark {

    protected abstract fun drawElement(
        drawScope: DrawScope,
        color: Color,
        xPosition: Float,
        indexInfo: IndexInfo
    )

    internal fun draw(
        drawScope: DrawScope,
        color: Color,
        xPosition: Float,
        indexInfo: IndexInfo
    ) =
        drawElement(drawScope, color, xPosition, indexInfo)

    protected abstract val canvasHeight: Dp
    internal fun getCanvasHeight(): Dp = canvasHeight

    object None : TickMark() {
        override fun drawElement(
            drawScope: DrawScope,
            color: Color,
            xPosition: Float,
            indexInfo: IndexInfo
        ) {
        }

        override val canvasHeight: Dp = 0.dp
    }

    class Line(
        private val height: Dp = 4.dp,
        private val strokeWidth: Dp = Dp.Hairline
    ) : TickMark() {
        override val canvasHeight: Dp = height

        override fun drawElement(
            drawScope: DrawScope,
            color: Color,
            xPosition: Float,
            indexInfo: IndexInfo
        ) {
            with(drawScope) {
                val center = size.height / 2
                val yStart = center - height.toPx()
                val yEnd = center + height.toPx()
                val current = indexInfo.currentIndex
                if (current != indexInfo.first &&
                    current != indexInfo.last &&
                    current != indexInfo.selectedIndex
                ) {
                    drawLine(
                        color = color,
                        start = Offset(xPosition, yStart),
                        end = Offset(xPosition, yEnd),
                        strokeWidth = strokeWidth.toPx()
                    )
                }
            }
        }
    }

    class Dot(private val radius: Dp = 4.dp) : TickMark() {
        override val canvasHeight: Dp = radius * 2
        override fun drawElement(
            drawScope: DrawScope,
            color: Color,
            xPosition: Float,
            indexInfo: IndexInfo
        ) {
            with(drawScope) {
                val center = Offset(xPosition, size.height / 2)
                drawCircle(color, radius = radius.toPx(), center = center)
            }
        }
    }

    abstract class CustomMark : TickMark()

    companion object {
        fun CustomMark(
            canvasHeight: Dp = 4.dp,
            draw: DrawScope.(
                color: Color,
                xPosition: Float,
                indexInfo: IndexInfo
            ) -> Unit
        ): CustomMark {
            return object : CustomMark() {
                override fun drawElement(
                    drawScope: DrawScope,
                    color: Color,
                    xPosition: Float,
                    indexInfo: IndexInfo
                ) {
                    with(drawScope) { draw(color, xPosition, indexInfo) }
                }

                override val canvasHeight: Dp = canvasHeight
            }
        }
    }


}