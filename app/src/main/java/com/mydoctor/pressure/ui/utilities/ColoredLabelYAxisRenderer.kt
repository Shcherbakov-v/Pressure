package com.mydoctor.pressure.ui.utilities

import android.graphics.Canvas
import android.graphics.Color
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.renderer.YAxisRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Class of y axis renderers.
 */
class ColoredLabelYAxisRenderer(
    viewPortHandler: ViewPortHandler,
    yAxis: YAxis,
    trans: Transformer
) : YAxisRenderer(viewPortHandler, yAxis, trans) {
    /**
     * draws the y-labels on the specified x-position. And sets the color for the labels
     *
     * @param c - Canvas
     * @param fixedPosition
     * @param positions
     * @param offset
     */
    override fun drawYLabels(
        c: Canvas?,
        fixedPosition: Float,
        positions: FloatArray?,
        offset: Float
    ) {
        val from = if (mYAxis.isDrawBottomYLabelEntryEnabled) 0 else 1
        val to = if (mYAxis.isDrawTopYLabelEntryEnabled)
            mYAxis.mEntryCount
        else
            (mYAxis.mEntryCount - 1)

        // draw
        for (i in from until to) {
            val text = mYAxis.getFormattedLabel(i)

            mAxisLabelPaint.color = when (i) {
                1 -> Color.BLUE
                3 -> Color.RED
                else -> Color.GRAY
            }

            c?.drawText(text, fixedPosition, positions!![i * 2 + 1] + offset, mAxisLabelPaint)
        }
    }
}