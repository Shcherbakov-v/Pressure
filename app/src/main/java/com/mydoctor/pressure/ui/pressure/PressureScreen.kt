package com.mydoctor.pressure.ui.pressure

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mikephil.charting.data.Entry
import com.mydoctor.pressure.R
import com.mydoctor.pressure.data.Pressure
import com.mydoctor.pressure.ui.Header
import com.mydoctor.pressure.ui.navigation.NavigationDestination
import com.mydoctor.pressure.ui.theme.PressureTheme
import com.mydoctor.pressure.ui.utilities.Chart
import com.mydoctor.pressure.ui.utilities.SegmentedControl
import com.mydoctor.pressure.utilities.Day
import com.mydoctor.pressure.utilities.Month
import com.mydoctor.pressure.utilities.PeriodOfTime
import com.mydoctor.pressure.utilities.Week
import com.mydoctor.pressure.utilities.unzip
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import kotlin.math.roundToInt

object PressureDestination : NavigationDestination {
    override val route = "home"
}

/**
 * Entry route for Pressure screen
 */
@Composable
fun PressureScreen(
    //modifier: Modifier = Modifier,
    navigateToAddPressure: () -> Unit,
    pressureViewModel: PressureViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    val pressureUiState by pressureViewModel.pressureListState.collectAsState()
    Log.d(stringResource(R.string.pressure_tag), pressureUiState.pressureList.toString())
    PressureScreenUI(
        navigateToAddPressure = navigateToAddPressure,
        pressureList = pressureUiState.pressureList,
        periodOfTime = pressureUiState.periodOfTime,
        selectedTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(pressureUiState.selectedTime),
            ZoneId.systemDefault()
        ),
        onItemSelection = {
            coroutineScope.launch {
                pressureViewModel.updateUiState(
                    //TODO Обработать вариант когда график пустой
                    periodOfTime = when (it) {
                        0 -> Day
                        1 -> Week
                        2 -> Month
                        else -> Day
                    }
                )
            }
        }
    )
}

@Composable
fun PressureScreenUI(
    navigateToAddPressure: () -> Unit,
    pressureList: List<Pressure>,
    periodOfTime: PeriodOfTime,
    selectedTime: LocalDateTime,
    onItemSelection: (selectedItemIndex: Int) -> Unit,
) {
    LazyColumn {
        item {
            Header()
        }
        item {
            val formatterDate = DateTimeFormatter.ofPattern("dd MMMM yyyy")
            PressureBlock(
                //TODO Change date along with adding schedule
                date = "${formatterDate.format(selectedTime)} г.",//"Июня 2024 г.",
                navigateToAddPressure = navigateToAddPressure
            )
        }
        item {
            data class Post(val title: String)

            val LocalPost = compositionLocalOf { Post(title = "Title") }
            val post = Post(title = "Mail")
            CompositionLocalProvider(LocalPost provides post) {
                SegmentedControl(

                    /* TODO How do indents affect?
            modifier = Modifier
                            .padding(
                                top = 16.dp,
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp,
                            ),*/
                    items = listOf(
                        stringResource(R.string.day),
                        stringResource(R.string.week),
                        stringResource(R.string.month)
                    ),
                    onItemSelection = onItemSelection
                )
            }
        }
        item {
            ChartBlock(
                pressureList = pressureList,
                periodOfTime = periodOfTime,
                selectedTime = selectedTime,
            )
        }
/*        item {
            val countDots = 7
            val startX = 2
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
                maxRangeValue = countDots.toFloat()
            )
        }*/
        item {
            Notes(
                {},
                stringResource(R.string.description_note_text)
            )
        }
        item {
            Targets(
                {},
                stringResource(R.string.description_target_text)
            )
        }
        item {
            MeasurementLog {}
        }
    }
}

@Composable
fun PressureBlock(
    date: String,
    navigateToAddPressure: () -> Unit,
) {
    Box(
        Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.align(alignment = Center)
        ) {
            Text(
                text = stringResource(R.string.name_pressure),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )
            Text(
                text = date,
                fontSize = 14.sp,
            )
        }
        SmallFloatingActionButton(
            onClick = navigateToAddPressure,
            modifier = Modifier
                .align(alignment = CenterEnd)
                .padding(end = 16.dp),
            shape = RoundedCornerShape(10.dp),
            containerColor = Color.White,
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.button_add_black),
                null,
            )
        }
    }
}

@Composable
fun ff() {
    Box(modifier = Modifier.fillMaxSize()) {
        var offsetX by remember { mutableStateOf(0f) }

        Box(
            Modifier
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()

                        val x = dragAmount.x
                        when {
                            x > 0 ->{ /* right */ }
                            x < 0 ->{ /* left */ }
                        }
                        offsetX += dragAmount.x
                    }
                }
        )
    }
}

@Composable
fun ChartBlock(
    pressureList: List<Pressure>,
    selectedTime: LocalDateTime,
    periodOfTime: PeriodOfTime,
) {
    val formatterDateTime = DateTimeFormatter.ofPattern(
        when (periodOfTime) {
            Day -> "HH:mm"
            Week, Month -> "dd.MM"
        }
    )

    val pressureListTriple: List<Triple<Float, Float, Pair<String, Float>>> =
        pressureList.mapIndexed { index, pressure ->
            val date = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(pressure.date),
                ZoneId.systemDefault()
            )
            Triple(
                //Entry(index.toFloat(), pressure.systolic.toFloat(), pressure),
                //Entry(index.toFloat(), pressure.diastolic.toFloat(), pressure),
                pressure.systolic.toFloat(),
                pressure.diastolic.toFloat(),
                Pair(formatterDateTime.format(date), date.dayOfMonth.toFloat())
            )
        }

    val (
        listEntrySystolic,
        listEntryDiastolic,
        dates
    ) = pressureListTriple.groupBy { it.third }
        .mapValues { it.value.average() }
        .values.map {
            Triple(
                Entry(it.third.second, it.first),
                Entry(it.third.second, it.second),
                it.third.first
            )
        }.unzip()

    val formatterTime = DateTimeFormatter.ofPattern("H")
    val hoursOfDay = 24
    val dateOfWeek = 7
    var maxRangeValue = 0f
    val dateOrTimeRange = when(periodOfTime) {
        Day -> {
            maxRangeValue = hoursOfDay.toFloat()
            List(hoursOfDay) {
                formatterTime.format(selectedTime.withHour(it))
            }
        }
        Week -> {
            maxRangeValue = dateOfWeek.toFloat()
            List(maxRangeValue.toInt()) {
                //localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                formatterDateTime.format(selectedTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusDays(it.toLong()))
            }
        }
        Month -> {
            maxRangeValue = selectedTime.toLocalDate().lengthOfMonth().toFloat()//dayOfMonth.toFloat()
            List(maxRangeValue.toInt()) {
                formatterDateTime.format(selectedTime.withDayOfMonth(it + 1))
            }
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
    ) {
        Chart(
            listEntry1 = listEntrySystolic,
            listEntry2 = listEntryDiastolic,
            chartAxisValues = dateOrTimeRange,
            maxRangeValue = maxRangeValue
        )
    }
}

fun List<Triple<Float, Float, Pair<String, Float>>>.average():
        Triple<Float, Float, Pair<String, Float>> {
    var averageSystolicList: MutableList<Float> = mutableListOf()
    var averageDiastolic: MutableList<Float> = mutableListOf()
    var minSystolic: Float
    var minDiastolic: Float
    var maxSystolic: Float
    var maxDiastolic: Float
    var minPulse: Float
    var maxPulse: Float
    forEachIndexed { index, triple ->
        averageSystolicList.add(triple.first)
        averageDiastolic.add(triple.second)
    }
    return Triple(
        averageSystolicList.average().toFloat(),
        averageDiastolic.average().toFloat(),
        first().third
    )
}

@Composable
fun Notes(
    onClick: () -> Unit,
    descriptionNote: String,
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier
            .padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Column(
            modifier = Modifier.padding(
                top = 8.dp,
                bottom = 16.dp,
                start = 16.dp,
                end = 6.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.icon_notes),
                        null
                    )
                    Text(
                        text = stringResource(R.string.notes),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                IconButton(onClick) {
                    Icon(
                        painterResource(R.drawable.button_add), null,
                    )
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier
                    .padding(end = 10.dp),
            )
            Text(
                text = descriptionNote,
                fontSize = 14.sp,
                color = Color(0xFF83A0B9),
                modifier = Modifier
                    .padding(end = 10.dp),
            )
        }
    }
}

@Composable
fun Targets(
    onClick: () -> Unit,
    descriptionTarget: String,
) {
    Card(
        Modifier
            .padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
    ) {
        Column(
            Modifier.padding(
                top = 8.dp,
                bottom = 16.dp,
                start = 16.dp,
                end = 6.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.icon_targets),
                        null
                    )
                    Text(
                        text = stringResource(R.string.targets),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                IconButton(onClick) {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.button_add),
                        null
                    )
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier
                    .padding(end = 10.dp),
            )
            Text(
                text = descriptionTarget,
                fontSize = 14.sp,
                color = Color(0xFF83A0B9),
                modifier = Modifier
                    .padding(end = 10.dp),
            )
        }
    }
}

@Composable
fun MeasurementLog(onClick: () -> Unit) {
    Card(
        modifier = Modifier.padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 6.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.icon_measurement_log),
                    null
                )
                Text(
                    text = stringResource(R.string.measurement_log),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            IconButton(onClick) {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.icon_next),
                    null
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PressureScreenPreview() {
    PressureTheme {
        PressureScreenUI(
            navigateToAddPressure = {},
            selectedTime = LocalDateTime.now(),
            periodOfTime = Month,
            pressureList = listOf(
                Pressure(
                    id = 0,
                    systolic = 125,
                    diastolic = 15,
                    pulse = 71,
                    date = 1731100000000,
                    note = "",
                ),
                Pressure(
                    id = 0,
                    systolic = 121,
                    diastolic = 91,
                    pulse = 71,
                    date = 1729555200000,
                    note = "",
                ),
                Pressure(
                    id = 0,
                    systolic = 122,
                    diastolic = 92,
                    pulse = 71,
                    date = 1729728000000,
                    note = "",
                ),
                Pressure(
                    id = 0,
                    systolic = 123,
                    diastolic = 93,
                    pulse = 71,
                    date = 1729814400000,
                    note = "",
                ),
                Pressure(
                    id = 0,
                    systolic = 124,
                    diastolic = 94,
                    pulse = 71,
                    date = 1729900800000,
                    note = "",
                ),
            ),
            onItemSelection = {},
        )
    }
}