package com.mydoctor.pressure.ui.utilities

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
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.mydoctor.pressure.R
import com.mydoctor.pressure.ui.theme.PressureTheme


@Composable
fun LineChartCard(
    modifier: Modifier = Modifier,
    lineData: LineData,
    chartAxisValues: List<String>,
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
            .padding(16.dp)
            .aspectRatio(2f),
        lineData = lineData,
        chartAxisValues = chartAxisValues,
    )
    //}
}

@Composable
fun LineChartComponent(
    modifier: Modifier = Modifier,
    lineData: LineData,
    chartAxisValues: List<String>,
) {
    // set up data-> (x,y) -> Entry -> List<Entry> -> LineDataSet -> LineData -> LineChart(LineData)
    AndroidView(
        modifier = modifier,
        factory = { context ->
            LineChart(context).setupLineChart(chartAxisValues).apply {
                setBackgroundColor(android.graphics.Color.WHITE)
                data = lineData
            }
        },
    ) {
        it.data = lineData
        it.setupLineChart(chartAxisValues)
        it.notifyDataSetChanged()
        it.invalidate()
        Log.d("PressureTag", "тут")
    }
}

// List<Float> -> List<Entry> -> LineDataSet
fun List<Float>.createDataSetWithColor(
    datasetColor: Int = android.graphics.Color.GREEN, label: String = "No Label"
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

fun LineChart.setupLineChart(chartAxisValues: List<String>): LineChart = this.apply {
    setTouchEnabled(true)
    isDragEnabled = true

    //this.setDragEnabled(true)
    setScaleEnabled(true)
    setPinchZoom(true)
    description.isEnabled = false

    legend.form = Legend.LegendForm.CIRCLE
    legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
    //legend.setDrawInside(false)
    //legend.


    //xAxis.setAxisMinimum(0f);
    //xAxis.spaceMax = 0.4f
    //xAxis.spaceMin = 0.4f;
    // set up x-axis
    xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
        // axisMinimum = -10f
        // axisMaximum = 10f
    }


    val chartAxisValuesM = chartAxisValues.toMutableList().apply {
        if (chartAxisValues.isNotEmpty()) {
        add("")
        add(chartAxisValues.size - 1, "")
            }
    }
    xAxis.setLabelCount(chartAxisValues.size, true)
    //xAxis.valueFormatter = LineChartXAxisValueFormatter(chartAxisValues.toTypedArray())

    xAxis.valueFormatter = object : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase): String {
            return if (chartAxisValues.isNotEmpty()) chartAxisValues[value.toInt()] else ""
            }
        }


/*    xAxis.valueFormatter = object : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase): String {
            return when (value.toInt()) {
                0 -> ""
                chartAxisValuesM.size - 1 -> ""
                else -> chartAxisValuesM[value.toInt()]
            }
        }
    }*/

    //xAxis.setXOffset(155f)
    //xAxis.setAxisLineWidth(1f);

    //setVisibleXRangeMaximum(chartAxisValues.size.toFloat());
    //setVisibleXRangeMinimum(chartAxisValues.size.toFloat());

    //setViewPortOffsets(0f, 0f, 0f, 0f);

    //xAxis.setCenterAxisLabels(true)

    // set up y-axis
    axisRight.apply {
        // axisMinimum = -5f
        // axisMaximum = 5f
        // setDrawGridLines(false)
        xAxis.setDrawGridLines(false);
        enableGridDashedLine(14f, 14f, 0f)
        //gridColor = android.graphics.Color.GREEN

    }

    axisLeft.isEnabled = false
}

class LineChartXAxisValueFormatter(chartAxisValues: Array<String>) :
    IndexAxisValueFormatter(chartAxisValues) {
    override fun getFormattedValue(value: Float): String {

        //Timber.i("index = %s", value);
        return super.getFormattedValue(value);
    }
}

//class LineChartXAxisValueFormatter : IndexAxisValueFormatter() {
//    override fun getFormattedValue(value: Float): String {
//        // Convert float value to date string
//        // Convert from seconds back to milliseconds to format time  to show to the user
//
//        val emissionsMilliSince1970Time = (value.toLong()) //* 1000
//
//        // Show time in local version
//
//
//                val timeMilliseconds: Date = Date(emissionsMilliSince1970Time)
//                val dateTimeFormat: DateFormat =
//                    DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
//
//        val formatterDate = DateTimeFormatter.ofPattern("dd.MM")
//        val date = LocalDateTime.ofInstant(
//            Instant.ofEpochMilli(emissionsMilliSince1970Time),
//            ZoneId.systemDefault()
//        )
//
//        return formatterDate.format(date)
//    }
//}

@Composable
fun Chart(
    listEntry1: List<Entry>,
    listEntry2: List<Entry>,
    chartAxisValues: List<String>,
) {
    val lineDataSet = createLineDataSet(
        listEntry1,
        android.graphics.Color.parseColor("#66FF725E"),
        android.graphics.Color.parseColor("#FF725E"),
        stringResource(R.string.systolic),
    )
    val lineDataSet2 = createLineDataSet(
        listEntry2,
        android.graphics.Color.parseColor("#66FFB342"),
        android.graphics.Color.parseColor("#FFB342"),
        stringResource(R.string.diastolic),
    )
    val lineData = LineData(lineDataSet, lineDataSet2)

    LineChartCard(
        lineData = lineData,
        chartAxisValues = chartAxisValues,
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
            val countDots = 4
            Chart(
                listEntry1 = (0..<countDots).map {
                    Entry(
                        it.toFloat(),
                        (10..20).random().toFloat(),
                    )
                },
                listEntry2 = (0..<countDots).map {
                    Entry(
                        it.toFloat(),
                        (0..10).random().toFloat()
                    )
                },
                chartAxisValues = (0..<countDots).map { "1$it.10" }
            )
        }
    }
}