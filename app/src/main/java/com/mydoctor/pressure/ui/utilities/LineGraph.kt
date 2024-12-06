package com.mydoctor.pressure.ui.utilities

import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.EntryXComparator
import com.github.mikephil.charting.utils.MPPointD
import com.mydoctor.pressure.R
import com.mydoctor.pressure.ui.pressure.MarkerDetails
import com.mydoctor.pressure.ui.theme.PressureTheme
import com.mydoctor.pressure.utilities.TAG
import java.util.Collections
import kotlin.random.Random

/**
 * Chart that draws lines, surfaces, circles
 *
 * @param listEntrySystolic - list of entries for displaying the systolic pressure value line
 * @param listEntryDiastolic - list of entries for displaying the diastolic pressure value line
 * @param listEntryNote - list of entries to display the points of note values
 * @param chartAxisValues - list of entries to display labels at the bottom of the chart
 * @param maxRangeValue - maximum value of list of entries to display labels at the bottom of the chart
 * @param onPointClick - function that is called when click on a point on the chart. And transmits MarkerDetails
 */
@Composable
fun Chart(
    listEntrySystolic: List<Entry>,
    listEntryDiastolic: List<Entry>,
    listEntryNote: List<Entry>,
    chartAxisValues: List<String>,
    maxRangeValue: Float,
    onPointClick: (markerDetails: MarkerDetails) -> Unit,
) {
    val lineDataSetSystolic = createLineDataSet(
        listEntry = listEntrySystolic,
        colorLine = Color.parseColor("#FF725E"),
        circleColor = Color.parseColor("#FF725E"),
        label = stringResource(R.string.systolic),
    )
    val lineDataSetDiastolic = createLineDataSet(
        listEntry = listEntryDiastolic,
        colorLine = Color.parseColor("#66FFB342"),
        circleColor = Color.parseColor("#FFB342"),
        label = stringResource(R.string.diastolic),
    )
    val lineDataSetNote = createNotesDataSet(
        listEntry = listEntryNote,
        circleColor = Color.parseColor("#0088FF"),
        label = stringResource(R.string.diastolic),
    )
    val lineData = LineData(lineDataSetSystolic, lineDataSetDiastolic, lineDataSetNote)
    val yMaxRangeValue =
        listEntrySystolic.plus(listEntryDiastolic).ifEmpty { null }?.maxOf { it.y } ?: 200f
    LineChartCard(
        lineData = lineData,
        chartAxisValues = chartAxisValues,
        xMaxRangeValue = maxRangeValue,
        yMaxRangeValue = yMaxRangeValue,
        onPointClick = onPointClick,
    )
}

@Composable
fun LineChartCard(
    //modifier: Modifier = Modifier,
    lineData: LineData,
    chartAxisValues: List<String>,
    xMaxRangeValue: Float,
    yMaxRangeValue: Float,
    onPointClick: (markerDetails: MarkerDetails) -> Unit,
) {
    LineChartComponent(
        modifier = Modifier
            .padding(
                bottom = 16.dp
            )
            .aspectRatio(1.45f),
        lineData = lineData,
        chartAxisValues = chartAxisValues,
        xMaxRangeValue = xMaxRangeValue,
        yMaxRangeValue = yMaxRangeValue,
        onPointClick = onPointClick,
    )
}

/**
 * Creates an AndroidView to display the graph and sets the required properties
 */
@Composable
fun LineChartComponent(
    modifier: Modifier = Modifier,
    lineData: LineData,
    chartAxisValues: List<String>,
    xMaxRangeValue: Float,
    yMaxRangeValue: Float,
    onPointClick: (markerDetails: MarkerDetails) -> Unit,
) {
    // set up data-> (x,y) -> Entry -> List<Entry> -> LineDataSet -> LineData -> LineChart(LineData)
    AndroidView(
        modifier = modifier,
        factory = { context ->
            LineChart(context).setupLineChart(
                chartAxisValues = chartAxisValues,
                xMaxRangeValue = xMaxRangeValue,
                yMaxRangeValue = yMaxRangeValue,
            ).apply {
                setBackgroundColor(Color.WHITE)
                data = lineData
            }
        },
    ) { chart ->
        chart.setupLineChart(
            chartAxisValues = chartAxisValues,
            xMaxRangeValue = xMaxRangeValue,
            yMaxRangeValue = yMaxRangeValue,
        ).apply {
            setBackgroundColor(Color.WHITE)
            data = lineData
        }
        //chart.clear()
        chart.data = lineData
        chart.notifyDataSetChanged()
        chart.invalidate()
        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            /**
             * Called when a value has been selected inside the chart.
             *
             * Sends an event with a [MarkerDetails] for the top value. Click on the note is disabled
             *
             * @param e The selected Entry
             * @param h The corresponding highlight object that contains information
             *          about the highlighted position such as dataSetIndex, ...
             */
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (h?.dataSetIndex == 2) return

                val topDataSetIndex = 0
                h?.let {
                    val index =
                        (chart.data.dataSets[h.dataSetIndex] as LineDataSet).values.indexOf(e)
                    val topEntry =
                        (chart.data.dataSets[topDataSetIndex] as LineDataSet).values[index]

                    val point: MPPointD = chart.getTransformer(YAxis.AxisDependency.RIGHT)
                        .getPixelForValues(topEntry.x, topEntry.y)
                    val xValue = point.x
                    val yValue = point.y

                    val loc = IntArray(2)
                    chart.getLocationInWindow(loc)
                    val screenX = (xValue + loc[0]).toInt()
                    val screenY = (yValue + loc[1]).toInt()
                    Log.d(
                        TAG, "onValueSelected:\n" +
                                "screenX:$screenX\n" +
                                "screenY: $screenY"
                    )

                    val markerDetails = (e?.data as MarkerDetails)
                    onPointClick(
                        markerDetails.copy(
                            x = screenX,
                            y = screenY,
                        )
                    )
                }
            }

            override fun onNothingSelected() {}
        })
    }
}

/**
 * Sets properties for the graph.
 */
fun LineChart.setupLineChart(
    chartAxisValues: List<String>,
    xMaxRangeValue: Float,
    yMaxRangeValue: Float
): LineChart =
    this.apply {
        //if (!(axisRight != null && axisRight.axisMaximum > 0)) return@apply

        isDragEnabled = false
        setScaleEnabled(false)
        description.isEnabled = false
        legend.isEnabled = false

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
            axisMinimum = 0f
            axisMaximum = xMaxRangeValue
            textColor = Color.GRAY
            axisLineColor = Color.BLACK
            position = XAxis.XAxisPosition.BOTTOM
            spaceMax = space
            spaceMin = space
            labelCount = if (chartAxisValues.size < 7) chartAxisValues.size else 7

            val llStart = createLimitLine(0f/*-space*/, Color.GRAY)
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
        axisLeft.setAxisMaximum(yMaxRangeValue)
        axisRight.setAxisMaximum(yMaxRangeValue)
        axisLeft.axisMinimum = 0f
        axisRight.axisMinimum = 0f

        if (axisRight.axisMaximum <= 200f) {
            axisRight.labelCount = 4
            axisLeft.axisMaximum = 200f
            axisRight.axisMaximum = 200f
        } else {
            axisRight.labelCount = 6
        }

        // set up y-axis
        axisLeft.isEnabled = false
        axisRight.apply {
            setDrawGridLines(false)
            enableGridDashedLine(14f, 14f, 0f)
            axisLineColor = Color.BLACK

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

/**
 * Sets properties for a line
 */
fun createLineDataSet(
    listEntry: List<Entry>,
    colorLine: Int,
    circleColor: Int,
    label: String,
): LineDataSet {
    //we do not allow java.lang.NegativeArraySizeException to occur
    Collections.sort(listEntry, EntryXComparator())
    // List<Float> -> List<Entry> -> LineDataSet
    return LineDataSet(
        listEntry,
        label
    ).apply {
        setDrawVerticalHighlightIndicator(false)
        setDrawHorizontalHighlightIndicator(false)
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

/**
 * Sets properties for note points
 */
fun createNotesDataSet(
    listEntry: List<Entry>,
    circleColor: Int,
    label: String,
): LineDataSet {
    Collections.sort(listEntry, EntryXComparator())
    return LineDataSet(
        listEntry,
        label
    ).apply {
        enableDashedLine(0f, 1f, 0f)
        setDrawVerticalHighlightIndicator(false)
        setDrawHorizontalHighlightIndicator(false)
        setCircleColor(circleColor)
        //setDrawCircleHole(false)
        setDrawValues(false)
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
            val listEntrySystolic = (startX..<countDots).map {
                Entry(
                    it.toFloat(),
                    (100..180).random().toFloat(),
                )
            }
            val listEntryDiastolic = (startX..<countDots).map {
                Entry(
                    it.toFloat(),
                    (80..110).random().toFloat()
                )
            }
            val offsetX = 0.3f
            val offsetY = 6f
            val listEntryNote = listEntrySystolic.toMutableList().map {
                if (Random.nextBoolean()) {
                    Entry(it.x + offsetX, it.y + offsetY)
                } else null
            }
            Chart(
                listEntrySystolic = listEntrySystolic,
                listEntryDiastolic = listEntryDiastolic,
                listEntryNote = listEntryNote.filterNotNull(),
                chartAxisValues = (0..<countDots).map { "${it + 1}.10" },
                maxRangeValue = 25f,//countDots.toFloat()
                onPointClick = {}
            )
        }
    }
}