package com.mydoctor.pressure.ui.pressure

import android.content.res.Resources
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Popup
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.mydoctor.pressure.R
import com.mydoctor.pressure.data.Pressure
import com.mydoctor.pressure.ui.data.AddDataDestination
import com.mydoctor.pressure.ui.data.MeasurementLogDestination
import com.mydoctor.pressure.ui.navigation.NavigationDestination
import com.mydoctor.pressure.ui.targets.TargetDestination
import com.mydoctor.pressure.ui.targets.TargetDetails
import com.mydoctor.pressure.ui.targets.TargetDetails.Companion.EMPTY_INDEX_TARGET_ID
import com.mydoctor.pressure.ui.theme.PressureTheme
import com.mydoctor.pressure.ui.utilities.Chart
import com.mydoctor.pressure.ui.utilities.SegmentedControl
import com.mydoctor.pressure.utilities.Day
import com.mydoctor.pressure.utilities.Month
import com.mydoctor.pressure.utilities.TAG
import com.mydoctor.pressure.utilities.Week
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

/**
 * Destination for Pressure screen
 */
object PressureDestination : NavigationDestination {
    override val route = "home"
}

/**
 * Entry route for Pressure screen
 *
 * @param navigateToAddData - function that will be used to
 * navigate to the route to the [AddDataDestination]
 * @param navigateToMeasurementLog - function that will be used to
 * navigate to the route to the [MeasurementLogDestination]
 * @param navigateToTarget - function that will be used to
 * navigate to the route to the [TargetDestination]
 * @param pressureViewModel - ViewModel [PressureViewModel] for this screen
 *
 */
@Composable
fun PressureScreen(
    //modifier: Modifier = Modifier,
    navigateToAddData: () -> Unit,
    navigateToMeasurementLog: () -> Unit,
    navigateToTarget: (Long) -> Unit,
    pressureViewModel: PressureViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    val pressuresDetails = pressureViewModel.pressureUiState.pressuresDetails
    val targetDetails = pressureViewModel.pressureUiState.targetDetails

    PressureScreenUI(
        navigateToAddPressure = navigateToAddData,
        navigateToMeasurementLog = navigateToMeasurementLog,
        navigateToTarget = { navigateToTarget(targetDetails?.id ?: EMPTY_INDEX_TARGET_ID) },
        targetDetails = targetDetails,
        pressuresDetails = pressuresDetails,
        onItemSelection = {
            coroutineScope.launch {
                pressureViewModel.updatePeriodOfTime(
                    periodOfTime = when (it) {
                        0 -> Day
                        1 -> Week
                        2 -> Month
                        else -> Day
                    }
                )
            }
        },
        onPageSelected = pressureViewModel::changeSelectedPage,
        onPagerUpdated = {
            pressureViewModel.updatePressuresDetails(
                pressuresDetails = pressuresDetails.copy(
                    listUpdated = false
                )
            )
        },
        onPointClick = { markerDetails ->
            pressureViewModel.updatePressuresDetails(
                pressuresDetails = pressuresDetails.copy(
                    marker = markerDetails
                )
            )
        },
        onValuePressuresChange = pressureViewModel::updatePressuresDetails,
        onValueTargetChange = pressureViewModel::updateTargetDetails,
    )
}

@Composable
fun PressureScreenUI(
    pressuresDetails: PressuresDetails,
    targetDetails: TargetDetails?,
    navigateToAddPressure: () -> Unit,
    navigateToMeasurementLog: () -> Unit,
    navigateToTarget: () -> Unit,
    onItemSelection: (selectedItemIndex: Int) -> Unit,
    onPageSelected: (Int) -> Unit,
    onPagerUpdated: () -> Unit,
    onPointClick: (markerDetails: MarkerDetails) -> Unit,
    onValuePressuresChange: (PressuresDetails) -> Unit,
    onValueTargetChange: () -> Unit,
) {
    var showMarker by remember { mutableStateOf(false) }
    val x by remember { mutableIntStateOf(0) }
    val y by remember { mutableIntStateOf(0) }
    LazyColumn(
        modifier = Modifier.padding(
            top = 44.dp,
        )
    ) {
        item {
            Header()
        }
        item {
            val formatterDate = DateTimeFormatter.ofPattern("MMMM yyyy")
            PressureBlock(
                date = "${camelCase(formatterDate.format(pressuresDetails.currentTime))} г.",
                navigateToAddPressure = navigateToAddPressure
            )
        }
        item {
            SegmentedControl(
                selectedIndex = when (pressuresDetails.periodOfTime) {
                    Day -> 0
                    Week -> 1
                    Month -> 2
                },
                items = listOf(
                    stringResource(R.string.day),
                    stringResource(R.string.week),
                    stringResource(R.string.month)
                ),
                onItemSelection = onItemSelection
            )
        }
        item {
            if (pressuresDetails.pressureChartsList.isNotEmpty()) {
                ChartBlock(
                    pressuresDetails = pressuresDetails,
                    onPageSelected = onPageSelected,
                    onPagerUpdated = onPagerUpdated,
                    onPointClick = onPointClick,
                    onValuePressuresChange = onValuePressuresChange,
                    navigateToAddPressure = navigateToAddPressure,
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = (128.dp + 16.dp),
                            bottom = 128.dp
                        ),
                    contentAlignment = Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        }
        item {
            Notes(
                onClick = {},
                note = pressuresDetails.notes
            )
        }
        item {
            Targets(
                targetDetails = targetDetails,
                onClick = navigateToTarget,
                onValueTargetChange = onValueTargetChange
            )
        }
        item {
            MeasurementLog(
                onClick = navigateToMeasurementLog,
            )
        }
    }
    if (showMarker) {
        Popup(
            offset = IntOffset(x, y),
            onDismissRequest = { showMarker = false },
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                ),
                border = BorderStroke(1.dp, Color(0xFFFF9F76)),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                ),
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Popup",
                )
            }
        }
    }
    if (pressuresDetails.showAddDataDialog) {
        AddDataDialog(
            onDismissRequest = {
                onValuePressuresChange(
                    pressuresDetails.copy(
                        showAddDataDialog = false
                    )
                )
            },
            sizeAddButton = pressuresDetails.sizeAddButton,
            positionInRootAddButton = pressuresDetails.positionInRootAddButton,
        )
    }
}

@Composable
fun Header() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(
                top = 6.dp,
                bottom = 16.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.icon_logo),
            null
        )
        Text(
            text = stringResource(R.string.my_doctor),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(
                    start = 13.dp,
                )
        )
    }
}

@Composable
fun AddDataDialog(
    onDismissRequest: () -> Unit,
    sizeAddButton: IntSize,
    positionInRootAddButton: Offset,
) {
    if (sizeAddButton == IntSize.Zero || positionInRootAddButton == Offset.Zero) return
    Box {
        val isVisible by remember { derivedStateOf { mutableStateOf(false) } }

        if (isVisible.value) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val clippedPath = Path().apply {
                    val innerCornerRadius = 16.dp.toPx()

                    val rect = Rect(
                        offset = positionInRootAddButton/*.minus(Offset(16.dp.toPx(), 8.dp.toPx()))*/,
                        size = sizeAddButton.toSize()
                    )
                    addRoundRect(
                        RoundRect(
                            rect,
                            cornerRadius = CornerRadius(
                                x = innerCornerRadius,
                                y = innerCornerRadius
                            ),
                        )
                    )
                }
                clipPath(clippedPath, clipOp = ClipOp.Difference) {
                    drawRoundRect(
                        color = Color(0x4D1C1C24),
                    )
                }
            }
        }
        var popupHeightPx by remember { mutableIntStateOf(0) }
        val paddingY = with(LocalDensity.current) { 16.dp.toPx().toInt() }
        val y = positionInRootAddButton.y.toInt() - popupHeightPx - paddingY

        Popup(
            offset = IntOffset(0, y),
            onDismissRequest = onDismissRequest,
        ) {
            Box(
                modifier = Modifier
                    //.fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        // Set column height using the LayoutCoordinates
                        popupHeightPx = coordinates.size.height

                        isVisible.value = coordinates.parentLayoutCoordinates?.let {
                            val parentBounds = it.boundsInWindow()
                            val childBounds = coordinates.boundsInWindow()
                            parentBounds.overlaps(childBounds)
                        } ?: false
                    }
                    .padding(
                        start = 32.dp,
                        end = 32.dp,
                    )
            ) {
                Box(
                    modifier = Modifier
                        .padding(end = 38.dp)
                        .align(alignment = BottomEnd),
                ) {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.icon_polygon),
                        null,
                        //colorFilter = ColorFilter.tint(Color.Yellow),
                    )
                }
                Card(
                    modifier = Modifier.padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                    ),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Box {
                        IconButton(
                            modifier = Modifier.align(alignment = TopEnd),
                            //.padding(end = 16.dp),
                            onClick = onDismissRequest,
                        ) {
                            Image(
                                imageVector = ImageVector.vectorResource(R.drawable.icon_delete),
                                contentDescription = null,
                            )
                        }
                        Column(
                            modifier = Modifier.padding(
                                top = 16.dp,
                                start = 25.dp,
                                end = 25.dp,
                                bottom = 16.dp,
                            ),
                            horizontalAlignment = CenterHorizontally,
                        ) {
                            Image(
                                imageVector = ImageVector.vectorResource(R.drawable.icon_camera),
                                null
                            )
                            Text(
                                text = stringResource(R.string.add_data_appeal),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                //modifier = Modifier
                                //.fillMaxSize()
                                //.wrapContentSize(Alignment.Center),
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = stringResource(R.string.add_data_appeal_description),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
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

fun camelCase(string: String, delimiter: String = " ", separator: String = " "): String {
    return string.split(delimiter).joinToString(separator = separator) {
        it.lowercase().replaceFirstChar { char -> char.titlecase() }
    }
}

@Composable
fun ChartBlock(
    pressuresDetails: PressuresDetails,
    onPageSelected: (Int) -> Unit,
    onPagerUpdated: () -> Unit,
    onPointClick: (markerDetails: MarkerDetails) -> Unit,
    onValuePressuresChange: (PressuresDetails) -> Unit,
    navigateToAddPressure: () -> Unit,
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            //.animateContentSize()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
    ) {
        val pagerState = rememberPagerState(
            pageCount = { pressuresDetails.pressureChartsList.size }
        )
        if (pressuresDetails.listUpdated) {
            onPagerUpdated()
            pagerState.interactionSource
            pagerState.requestScrollToPage(0)
        }

        val dateS = if (pressuresDetails.pressureChartsList.isNotEmpty()) {
            val displayedDate =
                pressuresDetails.pressureChartsList[pagerState.settledPage].date
            when (pressuresDetails.periodOfTime) {
                Day -> {
                    val formatterDay = DateTimeFormatter.ofPattern("dd MMMM yyyy")
                    if (pressuresDetails.periodOfTime == Day && pressuresDetails.currentTime == displayedDate) {
                        stringResource(R.string.today)
                    } else {
                        "${camelCase(formatterDay.format(displayedDate))} г."
                    }
                }

                Week -> {
                    val startDate = displayedDate.toLocalDate()
                        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    val endDate = displayedDate.toLocalDate()
                        .with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)).plusDays(1)
                    val formatterWeek = DateTimeFormatter.ofPattern("dd MMMM")
                    val startWeek = formatterWeek.format(startDate)
                    val endWeek = formatterWeek.format(endDate)

                    camelCase("$startWeek - $endWeek")
                }

                Month -> {
                    val monthNames = stringArrayResource(R.array.months)
                    val month = monthNames[displayedDate.month.value - 1]
                    val formatterYear = DateTimeFormatter.ofPattern("yyyy")
                    "$month ${formatterYear.format(displayedDate)}"
                }
            }
        } else ""

        val chartInfo = if (pressuresDetails.pressureChartsList.isEmpty()) {
            null
        } else {
            pressuresDetails.pressureChartsList[pagerState.settledPage].chartInfo
        }
        if (chartInfo == null) {
            Text(
                text = stringResource(R.string.no_data),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(
                        top = 24.dp,
                        start = 16.dp,
                        bottom = 19.dp,//TODO the size is the same, but can the layout be assembled differently?
                    )
                    .animateContentSize(),
            )
        } else {
            PressureInfo(
                systolicValue = chartInfo.systolicValue,
                diastolicValue = chartInfo.diastolicValue,
                pulseValue = chartInfo.pulseValue,
            )
        }
        Text(
            text = dateS,
            fontSize = 10.sp,
            modifier = Modifier.padding(
                top = 16.dp,
                start = 16.dp,
            ),
        )
        HorizontalDivider(
            thickness = 1.dp,
            modifier = Modifier.padding(16.dp),
        )
        Row(
            modifier = Modifier.padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.icon_legend_circle_red),
                null,
            )
            Text(
                text = stringResource(R.string.systolic),
                fontSize = 12.sp,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp
                ),
            )
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.icon_legend_circle_yellow),
                null,
            )
            Text(
                text = stringResource(R.string.diastolic),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp),
            )
        }

        LaunchedEffect(pagerState) {
            // Collect from the a snapshotFlow reading the currentPage
            snapshotFlow { pagerState.currentPage }.collect { page ->
                Log.d(TAG, "Page changed to $page")
                onPageSelected(page)
            }
        }
        HorizontalPager(
            state = pagerState,
            reverseLayout = true,
            //beyondViewportPageCount = 1
        ) { page ->
            val pressureChart = pressuresDetails.pressureChartsList[page]
            Chart(
                listEntrySystolic = pressureChart.listEntrySystolic,
                listEntryDiastolic = pressureChart.listEntryDiastolic,
                listEntryNote = pressureChart.listEntryNote,
                chartAxisValues = pressureChart.dateOrTimeRange,
                maxRangeValue = pressureChart.maxRangeValue,
                onPointClick = onPointClick,
            )
        }
        Box(
            Modifier.fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .padding(
                        end = 16.dp, bottom = 16.dp
                    )
                    .align(alignment = TopEnd)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { navigateToAddPressure() }
                    .let {
                        if (pressuresDetails.showAddDataDialog) {
                            it.onGloballyPositioned { coordinates ->
                                if (coordinates.size != IntSize.Zero &&
                                    coordinates.size != pressuresDetails.sizeAddButton
                                ) {
                                    onValuePressuresChange(
                                        pressuresDetails.copy(
                                            // size
                                            sizeAddButton = coordinates.size,
                                        )
                                    )
                                }
                                val positionOnScreen = coordinates.positionOnScreen()
                                if (positionOnScreen != Offset.Zero &&
                                    positionOnScreen != pressuresDetails.positionInRootAddButton
                                ) {
                                    onValuePressuresChange(
                                        pressuresDetails.copy(
                                            // global position (local also available)
                                            positionInRootAddButton = coordinates.positionOnScreen(),
                                        )
                                    )
                                }
                                Log.d(
                                    TAG,
                                    "ChartBlock: " +
                                            "sizeAddButton: ${pressuresDetails.sizeAddButton}, " +
                                            "positionInRootAddButton: ${pressuresDetails.positionInRootAddButton}"
                                )
                            }
                        } else it
                    },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(
                    width = 1.dp, color = Color(0xFFFF9F76)
                ),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    modifier = Modifier.padding(
                        horizontal = 16.dp, vertical = 8.dp
                    ),
                    text = stringResource(R.string.add_data),
                    //fontFamily = FontFamily(Font(R.font.inter)),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = Color(0xFFFF9F76),
                )
            }
        }
    }
    if (pressuresDetails.marker != null) {
        // Create element height in pixel state
        var popupHeightPx by remember { mutableIntStateOf(0) }
        var popupWidthPx by remember { mutableIntStateOf(0) }
        val paddingX = with(LocalDensity.current) { 32.dp.toPx().toInt() }//32.dp.toPx().toInt()
        val paddingY = with(LocalDensity.current) { (16.dp + 44.dp).toPx().toInt() }
        val widthDisplay = Resources.getSystem().displayMetrics.widthPixels
        val xOffset = (pressuresDetails.marker.x - (popupWidthPx / 2))
        val x = if (xOffset <= paddingX) {
            paddingX
        } else if (xOffset + popupWidthPx >= widthDisplay - paddingX) {
            widthDisplay - paddingX - popupWidthPx
        } else {
            xOffset
        }/* - 24.dp.toPx().toInt()*/
        val y = pressuresDetails.marker.y - popupHeightPx - paddingY
        val onDismiss = {
            onValuePressuresChange(
                pressuresDetails.copy(
                    marker = null
                )
            ) //showMarker = false
        }
        val modifier = Modifier.onGloballyPositioned { coordinates ->
            // Set column height using the LayoutCoordinates
            popupHeightPx = coordinates.size.height
            popupWidthPx = coordinates.size.width
        }
        when (pressuresDetails.marker.size) {
            MarkerDetails.Size.BIG -> MarkerBig(
                x = x,
                y = y,
                onDismiss = onDismiss,
                showNoteLabel = pressuresDetails.marker.showNoteLabel,
                systolicValue = pressuresDetails.marker.systolicValue,
                diastolicValue = pressuresDetails.marker.diastolicValue,
                pulseValue = pressuresDetails.marker.pulseValue,
                dateValue = pressuresDetails.marker.dateValue,
                modifier = modifier,
            )

            MarkerDetails.Size.SMALL -> MarkerSmall(
                x = x,
                y = y,
                onDismiss = onDismiss,
                showNoteLabel = pressuresDetails.marker.showNoteLabel,
                systolicValue = pressuresDetails.marker.systolicValue,
                diastolicValue = pressuresDetails.marker.diastolicValue,
                pulseValue = pressuresDetails.marker.pulseValue,
                dateValue = pressuresDetails.marker.dateValue,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun PressureInfo(
    systolicValue: String,
    diastolicValue: String,
    pulseValue: String,
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(
                end = 16.dp,
            )
        //.animateContentSize()
    ) {
        val (
            pressureName,
            systolic,
            separator,
            diastolic,
            pressureEnd,
            pulseStart,
            pulse,
            pulseEnd,
        ) = createRefs()
        Text(
            modifier = Modifier.constrainAs(pressureName) {
                top.linkTo(parent.top, margin = 24.dp)
                start.linkTo(parent.start, margin = 16.dp)
            },
            text = stringResource(R.string.name_pressure),
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = Color(0x801C1C24),
        )
        Text(
            modifier = Modifier.constrainAs(systolic) {
                top.linkTo(pressureName.top)
                start.linkTo(pressureName.end, margin = 8.dp)
            },
            text = systolicValue,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 18.sp,
        )
        Text(
            modifier = Modifier.constrainAs(separator) {
                top.linkTo(systolic.top)
                start.linkTo(systolic.end)
            },
            text = stringResource(R.string.separator),
            fontSize = 18.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.constrainAs(diastolic) {
                top.linkTo(separator.top)
                start.linkTo(separator.end)
            },
            text = diastolicValue,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 18.sp,
        )
        Text(
            modifier = Modifier.constrainAs(pressureEnd) {
                bottom.linkTo(diastolic.bottom)
                start.linkTo(diastolic.end, margin = 4.dp)
            },
            text = stringResource(R.string.unit_of_pressure_measurement),
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = Color(0x801C1C24),
        )
        Text(
            modifier = Modifier.constrainAs(pulseStart) {
                bottom.linkTo(pulse.bottom)
                start.linkTo(pressureName.start)
            },
            text = stringResource(R.string.pulse),
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = Color(0x801C1C24),
        )
        Text(
            modifier = Modifier.constrainAs(pulse) {
                top.linkTo(systolic.bottom)
                start.linkTo(systolic.start)
            },
            text = pulseValue,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 18.sp,
        )
        Text(
            modifier = Modifier.constrainAs(pulseEnd) {
                bottom.linkTo(pulse.bottom)
                start.linkTo(pulse.end, margin = 4.dp)
            },
            text = stringResource(R.string.unit_of_measurement_of_pulse),
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = Color(0x801C1C24),
        )
    }
}

@Composable
fun MarkerSmall(
    modifier: Modifier,
    x: Int,
    y: Int,
    systolicValue: String,
    diastolicValue: String,
    pulseValue: String,
    dateValue: String,
    showNoteLabel: Boolean,
    onDismiss: () -> Unit,
) {
    Popup(
        offset = IntOffset(
            x = x,
            y = y,
        ),
        onDismissRequest = onDismiss,
    ) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            border = BorderStroke(1.dp, Color(0xFFFF9F76)),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = CenterHorizontally
            ) {
                Row {
                    Column {
                        Text(
                            text = stringResource(R.string.name_pressure),
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            color = Color(0x801C1C24),
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = systolicValue,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = stringResource(R.string.separator),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = diastolicValue,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Text(
                            text = stringResource(R.string.unit_of_pressure_measurement),
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            color = Color(0x801C1C24),
                        )
                    }
                    Column(
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.pulse),
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            color = Color(0x801C1C24),
                        )
                        Text(
                            text = pulseValue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            lineHeight = 18.sp,
                        )
                        Text(
                            text = stringResource(R.string.unit_of_measurement_of_pulse),
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            color = Color(0x801C1C24),
                        )
                    }
                }
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = dateValue,
                    fontSize = 10.sp,
                    lineHeight = 10.sp,
                )
                if (showNoteLabel) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            imageVector = ImageVector.vectorResource(R.drawable.icon_note),
                            contentDescription = null,
                        )
                        Text(
                            modifier = Modifier.padding(start = 4.dp),
                            text = stringResource(R.string.there_are_notes),
                            fontSize = 10.sp,
                            lineHeight = 10.sp,
                            color = Color(0x801C1C24),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MarkerBig(
    modifier: Modifier,
    x: Int,
    y: Int,
    systolicValue: String,
    diastolicValue: String,
    pulseValue: String,
    dateValue: String,
    showNoteLabel: Boolean,
    onDismiss: () -> Unit,
) {
    Popup(
        offset = IntOffset(
            x = x,
            y = y,
        ),
        onDismissRequest = onDismiss,
    ) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            border = BorderStroke(1.dp, Color(0xFFFF9F76)),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),
        ) {
            ConstraintLayout(
                modifier = Modifier.padding(
                    end = 16.dp, bottom = 16.dp
                )
            ) {
                val (
                    systolicTop,
                    systolic,
                    systolicBottom,
                    diastolicTop,
                    diastolic,
                    diastolicBottom,
                    pulseTop,
                    pulse,
                    pulseBottom,
                    date,
                    noteLabel,
                    noteIcon
                ) = createRefs()
                Text(
                    modifier = Modifier.constrainAs(systolicTop) {
                        top.linkTo(parent.top, margin = 16.dp)
                        start.linkTo(parent.start, margin = 16.dp)
                    },
                    text = stringResource(R.string.systolic),
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    color = Color(0x801C1C24),
                )
                Text(
                    modifier = Modifier.constrainAs(systolic) {
                        top.linkTo(systolicTop.bottom)
                        start.linkTo(systolicTop.start)
                    },
                    text = systolicValue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 12.sp,
                )
                Text(
                    modifier = Modifier.constrainAs(systolicBottom) {
                        top.linkTo(systolic.bottom)
                        start.linkTo(systolic.start)
                    },
                    text = stringResource(R.string.unit_of_pressure_measurement),
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    color = Color(0x801C1C24),
                )
                Text(
                    modifier = Modifier.constrainAs(pulseTop) {
                        top.linkTo(systolicBottom.bottom, margin = 16.dp)
                        start.linkTo(systolicBottom.start)
                    },
                    text = stringResource(R.string.pulse),
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    color = Color(0x801C1C24),
                )
                Text(
                    modifier = Modifier.constrainAs(pulse) {
                        top.linkTo(pulseTop.bottom)
                        start.linkTo(pulseTop.start)
                    },
                    text = pulseValue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 18.sp,
                )
                Text(
                    modifier = Modifier.constrainAs(pulseBottom) {
                        top.linkTo(pulse.bottom)
                        start.linkTo(pulse.start)
                    },
                    text = stringResource(R.string.unit_of_measurement_of_pulse),
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    color = Color(0x801C1C24),
                )
                Text(
                    modifier = Modifier.constrainAs(diastolicTop) {
                        top.linkTo(systolicTop.top)
                        start.linkTo(systolicTop.end, margin = 16.dp)
                    },
                    text = stringResource(R.string.diastolic),
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    color = Color(0x801C1C24),
                )
                Text(
                    modifier = Modifier.constrainAs(diastolic) {
                        top.linkTo(diastolicTop.bottom)
                        start.linkTo(diastolicTop.start)
                    },
                    text = diastolicValue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 18.sp,
                )
                Text(
                    modifier = Modifier.constrainAs(diastolicBottom) {
                        top.linkTo(diastolic.bottom)
                        start.linkTo(diastolic.start)
                    },
                    text = stringResource(R.string.unit_of_pressure_measurement),
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    color = Color(0x801C1C24),
                )
                if (showNoteLabel) {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.icon_note),
                        contentDescription = null,
                        modifier = Modifier.constrainAs(noteIcon) {
                            top.linkTo(pulseBottom.top)
                            bottom.linkTo(pulseBottom.bottom)
                            start.linkTo(diastolicTop.start)
                        }
                    )
                    Text(
                        modifier = Modifier.constrainAs(noteLabel) {
                            bottom.linkTo(noteIcon.bottom)
                            top.linkTo(noteIcon.top)
                            start.linkTo(noteIcon.end)
                        },
                        text = stringResource(R.string.there_are_notes),
                        fontSize = 10.sp,
                        lineHeight = 10.sp,
                        color = Color(0x801C1C24),
                    )
                }
                Text(
                    modifier = Modifier.constrainAs(date) {
                        if (showNoteLabel) {
                            bottom.linkTo(noteLabel.top)
                            start.linkTo(noteIcon.start)
                        } else {
                            top.linkTo(pulseBottom.top)
                            bottom.linkTo(pulseBottom.bottom)
                            start.linkTo(diastolicTop.start)
                        }
                    },
                    text = dateValue,
                    fontSize = 10.sp,
                    lineHeight = 10.sp,
                )
            }
        }
    }
}

@Composable
fun Notes(
    onClick: () -> Unit,
    note: List<Pair<Long, String>>?,
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier.padding(
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
                        imageVector = ImageVector.vectorResource(R.drawable.icon_notes), null
                    )
                    Text(
                        text = stringResource(R.string.notes),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                IconButton(onClick) {
                    Icon(
                        painterResource(R.drawable.icon_next), null,
                    )
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(end = 10.dp),
            )
            if (note.isNullOrEmpty()) {
                Text(
                    text = stringResource(R.string.description_note_text),
                    fontSize = 14.sp,
                    color = Color(0xFF83A0B9),
                )
            } else {
                val formatterDateTime = DateTimeFormatter.ofPattern("d.MM HH:mm")
                note.forEach {
                    val (dateMillis, descriptionNote) = it
                    val date = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(dateMillis), ZoneId.systemDefault()
                    )
                    val textDate = formatterDateTime.format(date)
                    Text(
                        text = textDate,
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        color = Color(0xFF83A0B9),
                    )
                    Text(
                        text = descriptionNote,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun Targets(
    targetDetails: TargetDetails?,
    onClick: () -> Unit,
    onValueTargetChange: () -> Unit,
) {
    val textColor = Color(
        if (targetDetails != null) {
            when (targetDetails.status) {
                TargetDetails.Status.SET, TargetDetails.Status.ALMOST_OVERDUE -> 0xFF1C1C24
                TargetDetails.Status.OVERDUE -> 0xFFB22222
                TargetDetails.Status.ACCOMPLISHED -> 0x4D1C1C24
            }
        } else 0xFF83A0B9
    )
    val textDateTimeColor = Color(
        if (targetDetails != null) {
            when (targetDetails.status) {
                TargetDetails.Status.SET -> 0xFF6BD8AB
                TargetDetails.Status.ALMOST_OVERDUE -> 0xFFFF8F42
                TargetDetails.Status.OVERDUE -> 0xFFB22222
                TargetDetails.Status.ACCOMPLISHED -> 0x4D1C1C24
            }
        } else 0xFF83A0B9
    )
    Card(
        Modifier.padding(
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
                        imageVector = ImageVector.vectorResource(R.drawable.icon_targets), null
                    )
                    Text(
                        text = stringResource(R.string.targets),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                IconButton(onClick) {
                    Image(
                        imageVector = ImageVector.vectorResource(
                            if (targetDetails == null) R.drawable.button_add else R.drawable.icon_next
                        ), null
                    )
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(end = 10.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (targetDetails != null) {
                        val formatterDate = DateTimeFormatter.ofPattern("dd.MM")
                        val formatterTime = DateTimeFormatter.ofPattern("HH:mm")

                        val date = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(targetDetails.date), ZoneId.systemDefault()
                        )

                        val textDate = formatterDate.format(date)
                        val textTime = formatterTime.format(date)
                        Column {
                            Text(
                                text = textDate,
                                fontSize = 12.sp,
                                lineHeight = 12.sp,
                                color = textDateTimeColor,
                            )
                            Text(
                                modifier = Modifier.padding(top = 3.dp),
                                text = textTime,
                                fontSize = 12.sp,
                                lineHeight = 12.sp,
                                color = textDateTimeColor,
                            )
                        }
                    }
                    Text(
                        modifier = Modifier.padding(
                            start = if (targetDetails == null) 0.dp else 16.dp
                        ),
                        text = targetDetails?.description
                            ?: stringResource(R.string.description_target_text),
                        fontSize = 14.sp,
                        color = textColor,
                    )
                }
                targetDetails?.let {
                    Box(
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = false),
                                onClick = onValueTargetChange
                            )
                            .clip(RoundedCornerShape(30.dp)),
                    ) {
                        Image(
                            imageVector = ImageVector.vectorResource(
                                if (targetDetails.status == TargetDetails.Status.ACCOMPLISHED) R.drawable.checkbox_selected
                                else R.drawable.checkbox_unselected
                            ),
                            null,
                        )
                    }
                }
            }
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
        onClick = onClick,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp, start = 16.dp, end = 22.dp, bottom = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.icon_measurement_log), null
                )
                Text(
                    text = stringResource(R.string.measurement_log),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.icon_next), null
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 1100)
@Composable
fun PressureScreenPreview() {
    PressureTheme {
        PressureScreenUI(
            navigateToAddPressure = {},
            navigateToMeasurementLog = {},
            navigateToTarget = {},
            targetDetails = TargetDetails(
                id = EMPTY_INDEX_TARGET_ID,
                description = "Lose 2 kg",
                date = 1733227200000,
                status = TargetDetails.Status.SET,
                dateSelected = true,
                timeSelected = true
            ),
            pressuresDetails = PressuresDetails(
                listUpdated = true,
                showAddDataDialog = false,
                //settledPageOfChart = 0,
                currentTime = LocalDateTime.now(),
                periodOfTime = Day,
                pressureChartsList = listOf(
                    PressureChart.create(
                        currentTime = LocalDateTime.now(),
                        periodOfTime = Day,
                        pressureList = listOf(
                            //[Pressure(id=15, systolic=117, diastolic=97, pulse=77, date=1731780165940, note=)])
                            Pressure(
                                id = 0,
                                systolic = 125,
                                diastolic = 115,
                                pulse = 71,
                                date = 1731100000000,
                                note = "",
                            ),
                            Pressure(
                                id = 0,
                                systolic = 121,
                                diastolic = 91,
                                pulse = 72,
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
                                note = "Lose 2 kg",
                            ),
                            Pressure(
                                id = 0,
                                systolic = 124,
                                diastolic = 94,
                                pulse = 71,
                                date = 1729900800000,
                                note = "",
                            ),
                        )
                    )
                )
            ),
            onItemSelection = {},
            onPageSelected = {},
            onPagerUpdated = {},
            onPointClick = {},
            onValuePressuresChange = {},
            onValueTargetChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddDataDialogPreview() {
    PressureTheme {
        AddDataDialog(
            onDismissRequest = {},
            sizeAddButton = IntSize(432, 96),
            positionInRootAddButton = Offset(552f, 1654f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MarkerBigPreview() {
    PressureTheme {
        MarkerBig(
            modifier = Modifier,
            x = 0,
            y = 0,
            systolicValue = "110 - 150",
            diastolicValue = "80 - 90",
            pulseValue = "65 - 98",
            dateValue = "31 Мая 2024 г.",
            showNoteLabel = true,
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MarkerSmallPreview() {
    PressureTheme {
        MarkerSmall(modifier = Modifier,
            x = 0,
            y = 0,
            systolicValue = "150",
            diastolicValue = "90",
            pulseValue = "65",
            dateValue = "31 Мая 2024 г.",
            showNoteLabel = true,
            onDismiss = {})
    }
}