package com.mydoctor.pressure.ui.utilities

import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.mydoctor.pressure.R
import com.mydoctor.pressure.ui.theme.PressureTheme
import com.mydoctor.pressure.utilities.PeriodOfTime

@Composable
fun LineChartCard(
    modifier: Modifier = Modifier,
    lineData: LineData,
    chartAxisValues: List<String>,
    maxRangeValue: Float,
) {
    /*    Card(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(2f) // (width:height) 2:1
                .padding(16.dp)
        ) {*/
    LineChartComponent(
        modifier = Modifier
            //.fillMaxSize()
            //.padding(16.dp)
            .padding(
                bottom = 16.dp
            )
            .aspectRatio(1.45f),
        //.width(400.dp)
        //.height(400.dp)
        lineData = lineData,
        chartAxisValues = chartAxisValues,
        maxRangeValue = maxRangeValue
    )
    //}
}

@Composable
fun LineChartComponent(
    modifier: Modifier = Modifier,
    lineData: LineData,
    chartAxisValues: List<String>,
    maxRangeValue: Float,
) {
    // set up data-> (x,y) -> Entry -> List<Entry> -> LineDataSet -> LineData -> LineChart(LineData)
    AndroidView(
        modifier = modifier,
        factory = { context ->
            LineChart(context).setupLineChart(
                chartAxisValues = chartAxisValues,
                maxRangeValue = maxRangeValue
            ).apply {
                setBackgroundColor(Color.WHITE)
                data = lineData
            }
        },
    ) {
        it.data = lineData
        it.setupLineChart(
            chartAxisValues = chartAxisValues,
            maxRangeValue = maxRangeValue
        )
        it.notifyDataSetChanged()
        it.invalidate()
        Log.d("PressureTag", "тут")
    }
}

// List<Float> -> List<Entry> -> LineDataSet
fun List<Float>.createDataSetWithColor(
    datasetColor: Int = Color.GREEN, label: String = "No Label"
): LineDataSet {
    // List<Float> -> List<Entry>
    val entries = this.mapIndexed { index, value ->
        Entry(index.toFloat(), value)
    }
    // List<Entry> -> LineDataSet
    return LineDataSet(entries, label).apply {
        color = datasetColor
        setDrawFilled(false)
        setDrawCircles(true)
        mode = LineDataSet.Mode.CUBIC_BEZIER
    }
}

fun LineChart.setupLineChart(chartAxisValues: List<String>, maxRangeValue: Float): LineChart =
    this.apply {

        //if (!(axisRight != null && axisRight.axisMaximum > 0)) return@apply

        setTouchEnabled(false)
        isDragEnabled = false
        setScaleEnabled(false)

        //this.setDragEnabled(true)
        //setPinchZoom(true)
        description.isEnabled = false

        legend.apply {
            form = Legend.LegendForm.CIRCLE
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
        }

        val space = 0.5f

        val createLimitLine: (limit: Float, color: Int) -> LimitLine = { limit, color ->
            LimitLine(limit).apply {
                lineColor = color
                setLineWidth(0.2f)
                enableDashedLine(14f, 14f, 0f)
            }
        }

        // set up x-axis
        xAxis.apply {
            setDrawGridLines(false)
            //maxVisibleCount = maxRangeValue.toInt()
            //setMaxVisibleValueCount(maxRangeValue.toInt())
            //setVisibleXRange(0f, maxRangeValue)
            //setAxisMaximum(maxRangeValue)
            axisMinimum = 0f
            axisMaximum = maxRangeValue
            //setVisibleXRangeMinimum(0f)
            //setVisibleXRangeMaximum(maxRangeValue)
            //mAxisMaximum = 0f
            //mAxisMaximum = maxRangeValue
            textColor = Color.GRAY
            axisLineColor = Color.BLACK
            position = XAxis.XAxisPosition.BOTTOM
            spaceMax = space
            spaceMin = space
            labelCount = if (chartAxisValues.size < 7) chartAxisValues.size else 7

            val llStart = createLimitLine(-space, Color.GRAY)
            addLimitLine(llStart)

            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase): String {
                    val index = value.toInt()
                    return if (chartAxisValues.isNotEmpty() && index < chartAxisValues.size) {
                        chartAxisValues[index]
                    } else ""
                }
            }
        }

        //axisLeft.setAxisMaximum(maxRangeValue);
        //axisRight.setAxisMaximum(maxRangeValue);

        axisLeft.axisMinimum = 0f
        axisRight.axisMinimum = 0f

        if (axisRight.axisMaximum <= 200f) {
            axisRight.labelCount = 4
            axisLeft.axisMaximum = 200f
            axisRight.axisMaximum = 200f
        } else {
            axisRight.labelCount = 5
        }

        axisLeft.isEnabled = false

        // set up y-axis
        axisRight.apply {
            setDrawGridLines(false)
            enableGridDashedLine(14f, 14f, 0f)
            axisLineColor = Color.BLACK

            //val axisMaximumValue = axisRight.axisMaximum

            val llDangerouslyHigh = createLimitLine(150f, Color.RED)
            val llDangerouslyLow = createLimitLine(50f, Color.BLUE)
            val llMiddle = createLimitLine(100f, Color.GRAY)
            val llTop = createLimitLine(axisMaximum, Color.GRAY)

            addLimitLine(llDangerouslyLow)
            addLimitLine(llMiddle)
            addLimitLine(llDangerouslyHigh)
            addLimitLine(llTop)
        }

        rendererRightYAxis = ColoredLabelYAxisRenderer(
            viewPortHandler = viewPortHandler,
            yAxis = axisRight,
            getTransformer(YAxis.AxisDependency.RIGHT)
        )
    }

sealed class SwipeDirection

data object Back : SwipeDirection()
data object Forward : SwipeDirection()

@Composable
fun Chart(
    listEntry1: List<Entry>,
    listEntry2: List<Entry>,
    chartAxisValues: List<String>,
    maxRangeValue: Float,
    onSwipe: (SwipeDirection) -> Unit = {},
) {
    val lineDataSet = createLineDataSet(
        listEntry1,
        Color.parseColor("#FF725E"),
        Color.parseColor("#FF725E"),
        stringResource(R.string.systolic),
    )
    val lineDataSet2 = createLineDataSet(
        listEntry2,
        Color.parseColor("#66FFB342"),
        Color.parseColor("#FFB342"),
        stringResource(R.string.diastolic),
    )
    val lineData = LineData(lineDataSet, lineDataSet2)

    LineChartCard(
        modifier = Modifier
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val x = dragAmount.x
                    when {
                        x > 0 -> {
                            /* right */
                            onSwipe
                        }

                        x < 0 -> {
                            /* left */
                        }
                    }
                }
            },
        lineData = lineData,
        chartAxisValues = chartAxisValues,
        maxRangeValue = maxRangeValue,
    )
}

fun createLineDataSet(
    listEntry: List<Entry>,
    colorLine: Int,
    circleColor: Int,
    label: String,
): LineDataSet {
    return LineDataSet(
        listEntry,
        label
    ).apply {
        color = colorLine
        setCircleColor(circleColor)
        setDrawCircleHole(false)
        setDrawValues(false)
        lineWidth = 2f
        setDrawFilled(false)
        setDrawCircles(true)
        mode = LineDataSet.Mode.CUBIC_BEZIER
    }
}

@Preview(showBackground = true)
@Composable
fun PressureScreenPreview() {
    PressureTheme {
        Column {
            val countDots = 17
            val startX = 5
            Chart(
                listEntry1 = (startX..<countDots).map {
                    Entry(
                        it.toFloat(),
                        (100..180).random().toFloat(),
                    )
                },
                listEntry2 = (startX..<countDots).map {
                    Entry(
                        it.toFloat(),
                        (80..110).random().toFloat()
                    )
                },
                chartAxisValues = (0..<countDots).map { "${it + 1}.10" },
                maxRangeValue = 25f//countDots.toFloat()
            )
        }
    }
}