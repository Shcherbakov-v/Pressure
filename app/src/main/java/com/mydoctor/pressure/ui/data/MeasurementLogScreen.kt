package com.mydoctor.pressure.ui.data

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.hilt.navigation.compose.hiltViewModel
import com.mydoctor.pressure.R
import com.mydoctor.pressure.data.Pressure
import com.mydoctor.pressure.ui.navigation.NavigationDestination
import com.mydoctor.pressure.ui.theme.PressureTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Destination for Measurement Log screen
 */
object MeasurementLogDestination : NavigationDestination {
    override val route = "measurement_log"
}

/**
 * Entry route for Measurement Log Screen
 *
 * @param navigateBack - the function that will be used to navigate to the previous screen
 * @param navigateToAddData - function that will be used to
 * navigate to the route to the [AddDataDestination]
 * @param navigateToPressureDetails - function that will be used to
 * navigate to the route to the [PressureDetailsDestination]
 * @param viewModel - ViewModel [MeasurementLogViewModel] for this screen
 */
@Composable
fun MeasurementLogScreen(
    navigateBack: () -> Unit,
    navigateToAddData: () -> Unit,
    navigateToPressureDetails: (Long) -> Unit,
    viewModel: MeasurementLogViewModel = hiltViewModel()
) {
    val measurementLogUiState by viewModel.measurementLogUiState.collectAsState()
    MeasurementLogScreenUI(
        navigateBack = navigateBack,
        navigateToAddPressure = navigateToAddData,
        onPressureBlockClick = viewModel::sendStateBlockChangedEvent,
        onPressureClick = { indexMonth, indexPressure ->
            navigateToPressureDetails(
                measurementLogUiState.pressureMeasurementLogBlockList[indexMonth].pressureList[indexPressure].id
            )
        },
        pressureLists = measurementLogUiState.pressureMeasurementLogBlockList
    )
}

@Composable
fun MeasurementLogScreenUI(
    navigateBack: () -> Unit,
    navigateToAddPressure: () -> Unit,
    onPressureBlockClick: (indexMonth: Int) -> Unit,
    onPressureClick: (indexMonth: Int, indexPressure: Int) -> Unit,
    pressureLists: List<PressureMeasurementLogBlock>,
) {
    LazyColumn {
        item {
            MeasurementLogHeader(
                navigateBack = navigateBack,
                navigateToAddPressure = navigateToAddPressure
            )
        }
        if (pressureLists.isEmpty()) {
            item {
                MeasurementEmptyList()
            }
        } else {
            itemsIndexed(
                items = pressureLists,
                key = { index, item -> item.pressureList.first().id }
            ) { indexMonth, pressureBlock ->
                PressureInMeasurementLog(
                    pressureBlock = pressureBlock,
                    onPressureBlockClick = { onPressureBlockClick(indexMonth) },
                    onPressureClick = { indexPressure ->
                        onPressureClick(indexMonth, indexPressure)
                    },
                    possibilityOfExpansion = indexMonth != 0
                )
            }
        }
    }
}

@Composable
fun MeasurementLogHeader(
    navigateBack: () -> Unit,
    navigateToAddPressure: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(
                top = 22.dp,
            )
            .padding(
                top = 44.dp,
                bottom = 16.dp,
            ),
    ) {
        SmallFloatingActionButton(
            onClick = navigateBack,
            modifier = Modifier
                .align(alignment = CenterStart)
                .padding(start = 16.dp),
            shape = RoundedCornerShape(10.dp),
            containerColor = Color.White,
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.button_back),
                null,
            )
        }
        Text(
            text = stringResource(R.string.measurement_log),
            modifier = Modifier
                .align(alignment = Alignment.Center),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
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
fun MeasurementEmptyList() {
    Card(
        modifier = Modifier.padding(
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp,
        ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(R.string.measurement_item_default),
            color = Color(0x4D1C1C24),
        )
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PressureInMeasurementLog(
    pressureBlock: PressureMeasurementLogBlock,
    onPressureBlockClick: () -> Unit,
    onPressureClick: (indexPressure: Int) -> Unit,
    possibilityOfExpansion: Boolean,
) {
    Card(
        modifier = Modifier
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp,
            )
            //.animateContentSize()
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        onClick = { if (possibilityOfExpansion) { onPressureBlockClick() } }
    ) {
        Row(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = if (pressureBlock.expanded) 0.dp else 16.dp
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val monthNames = stringArrayResource(R.array.months)
            val date =
                Instant.ofEpochMilli(pressureBlock.pressureList.first().date).atZone(ZoneId.systemDefault())
            val month = monthNames[date.month.value - 1]
            val formatterYear = DateTimeFormatter.ofPattern("yyyy")
            val monthWithYear = "$month ${formatterYear.format(date)}"
            Text(
                //modifier = Modifier.padding(16.dp),
                text = monthWithYear,
                fontSize = 16.sp
            )
            if (possibilityOfExpansion) {
                Icon(
                    modifier = Modifier.rotate(if (pressureBlock.expanded) 180f else 0f),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_expanding_arrow),
                    contentDescription = null,
                )
            }
        }
        if (pressureBlock.expanded) {
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier
                    .padding(
                        top = 8.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
                color = Color(0x4D1C1C24)
            )
            FlowColumn(
                modifier = Modifier
                    .padding(bottom = 16.dp)
            ) {
                val formatterDate = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                val formatterTime = DateTimeFormatter.ofPattern("HH:mm")
                pressureBlock.pressureList.fastForEachIndexed { index, pressure ->
                    PressureForMonthInMeasurementLog(
                        modifier = Modifier
                            .clickable { onPressureClick(index) },
                        pressure = pressure,
                        formatterDate = formatterDate,
                        formatterTime = formatterTime,
                    )
                }
            }
        }
    }
}

@Composable
fun PressureForMonthInMeasurementLog(
    modifier: Modifier,
    pressure: Pressure,
    formatterDate: DateTimeFormatter,
    formatterTime: DateTimeFormatter,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = stringResource(R.string.pressure_format, pressure.systolic, pressure.diastolic),
                    fontSize = 18.sp,
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(R.string.unit_of_pressure_measurement),
                    fontSize = 12.sp,
                    //lineHeight = 12.sp,
                    color = Color(0x801C1C24),
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = pressure.pulse.toString(),
                    fontSize = 18.sp,
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(R.string.unit_of_measurement_of_pulse),
                    fontSize = 12.sp,
                    color = Color(0x801C1C24),
                )
            }
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min),
            ) {
                Text(
                    text = formatterDate.format(
                        Instant.ofEpochMilli(pressure.date).atZone(ZoneId.systemDefault())
                    ),
                    fontSize = 12.sp,
                    color = Color(0x4D1C1C24),
                )
                VerticalDivider(
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(
                            top = 2.dp,
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 2.dp,
                        ),
                    color = Color(0x4D1C1C24)
                )
                Text(
                    text = formatterTime.format(
                        Instant.ofEpochMilli(pressure.date).atZone(ZoneId.systemDefault())
                    ),
                    fontSize = 12.sp,
                    color = Color(0x4D1C1C24),
                )
                val isHighPressure = pressure.systolic > 150 || pressure.diastolic > 150
                if (isHighPressure) {
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(R.string.pressure_is_higher_than_normal),
                        fontSize = 12.sp,
                        color = Color(0x80FF66A6),
                    )
                }
            }
        }
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.icon_measurement_log),
            contentDescription = null,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MeasurementLogScreenUIPreview() {
    PressureTheme {
        MeasurementLogScreenUI(
            navigateBack = {},
            navigateToAddPressure = {},
            onPressureClick = { _, _ -> },
            onPressureBlockClick = {},
            pressureLists = listOf(
                PressureMeasurementLogBlock(
                    monthWithYear = 0,
                    expanded = true,
                    pressureList = listOf(
                        //[Pressure(id=15, systolic=117, diastolic=97, pulse=77, date=1731780165940, note=)])
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
                            pulse = 73,
                            date = 1729555200000,
                            note = "",
                        ),
                        Pressure(
                            id = 0,
                            systolic = 152,
                            diastolic = 92,
                            pulse = 70,
                            date = 1729728000000,
                            note = "",
                        ),
                        Pressure(
                            id = 0,
                            systolic = 123,
                            diastolic = 93,
                            pulse = 75,
                            date = 1729814400000,
                            note = "",
                        ),
                        Pressure(
                            id = 0,
                            systolic = 124,
                            diastolic = 94,
                            pulse = 89,
                            date = 1729900800000,
                            note = "",
                        ),
                    )
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MeasurementEmptyListPreview() {
    PressureTheme {
        MeasurementEmptyList()
    }
}

@Preview(showBackground = true)
@Composable
fun PressureForMonthInMeasurementLogPreview() {
    PressureTheme {
        val formatterDate = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val formatterTime = DateTimeFormatter.ofPattern("HH:mm")
        PressureForMonthInMeasurementLog(
            modifier = Modifier,
            pressure = Pressure(
                id = 0,
                systolic = 154,
                diastolic = 94,
                pulse = 71,
                date = 1729900800000,
                note = "",
            ),
            formatterDate = formatterDate,
            formatterTime = formatterTime,
        )
    }
}
