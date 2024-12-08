package com.mydoctor.pressure.ui.pressure

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.mydoctor.pressure.data.Pressure
import com.mydoctor.pressure.data.PressuresRepository
import com.mydoctor.pressure.data.TargetsRepository
import com.mydoctor.pressure.ui.pressure.MarkerDetails.Size.BIG
import com.mydoctor.pressure.ui.pressure.MarkerDetails.Size.SMALL
import com.mydoctor.pressure.ui.targets.TargetDetails
import com.mydoctor.pressure.ui.targets.TargetDetails.Companion.EMPTY_INDEX_TARGET_ID
import com.mydoctor.pressure.ui.targets.toTarget
import com.mydoctor.pressure.ui.targets.toTargetDetails
import com.mydoctor.pressure.ui.targets.updateStatus
import com.mydoctor.pressure.utilities.Day
import com.mydoctor.pressure.utilities.Month
import com.mydoctor.pressure.utilities.PeriodOfTime
import com.mydoctor.pressure.utilities.Week
import com.mydoctor.pressure.utilities.unzip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import kotlin.math.max

/**
 * ViewModel to retrieve all pressures from the [PressuresRepository]'s data source.
 * Also, to retrieve all the targets from the data source of the [TargetsRepository].
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PressureViewModel @Inject constructor(
    private val pressuresRepository: PressuresRepository,
    private val targetsRepository: TargetsRepository,
    //@IODispatcher private val coroutineDispatcher: CoroutineDispatcher,
) : ViewModel() {

    /**
     * Holds current pressure ui state
     */
    var pressureUiState by mutableStateOf(PressuresUiState())
        private set

    /**
     * Holds the flow of user changes [PeriodOfTime]
     */
    private val uiFlow: MutableStateFlow<PeriodOfTime> =
        MutableStateFlow(Day)


    init {
        viewModelScope.launch {
            uiFlow.flatMapLatest { periodOfTime ->
                val range = createRange(pressureUiState.pressuresDetails.currentTime, periodOfTime)
                pressuresRepository.getPressuresStream(range.first, range.second)
                    .map { Pair(it, periodOfTime) }
            }.collect { (pressureList, periodOfTime) ->
                val pressureChartPage0 = PressureChart.create(
                    currentTime = pressureUiState.pressuresDetails.currentTime,
                    pressureList = pressureList,
                    periodOfTime = periodOfTime
                )
                val time = pressureUiState.pressuresDetails.currentTime
                val pressureDeferred1 = viewModelScope.async { getPressures(1, time) }
                val pressureDeferred2 = viewModelScope.async { getPressures(2, time) }
                val pressureChartPage1 = pressureDeferred1.await()
                val pressureChartPage2 = pressureDeferred2.await()
                pressureUiState = pressureUiState.copy(
                    pressuresDetails = pressureUiState.pressuresDetails.copy(
                        pressureChartsList = listOf(
                            pressureChartPage0,
                            pressureChartPage1,
                            pressureChartPage2,
                        ),
                        listUpdated = true,
                        //settledPageOfChart = 0,
                        periodOfTime = periodOfTime
                    )
                )

                if (!pressureUiState.pressuresDetails.isShownAddDataDialog) {
                    showAddDataDialog()
                    hideAddDataDialog()
                }


            }
        }
        viewModelScope.launch {
            targetsRepository.getAllTargetsStream().collect {
                val targetDetails = it.firstOrNull()?.toTargetDetails()
                pressureUiState = pressureUiState.copy(
                    targetDetails = targetDetails
                )
            }
        }
    }

    /**
     * Hides the dialog prompting you to add data
     */
    private fun hideAddDataDialog() {
        viewModelScope.launch {
            delay(10_000L) // 10 second delay for AddDataDialog display
            updatePressuresDetails(
                pressureUiState.pressuresDetails.copy(
                    showAddDataDialog = false
                )
            )
        }
    }

    /**
     * Shows the dialog prompting you to add data
     */
    private fun showAddDataDialog() {
        //delay(1_000L)
        updatePressuresDetails(
            pressureUiState.pressuresDetails.copy(
                showAddDataDialog = true, isShownAddDataDialog = true
            )
        )
    }

    /**
     * Updates the [pressureUiState] with the value provided in the argument.
     */
    fun updatePressuresDetails(pressuresDetails: PressuresDetails) {
        pressureUiState = pressureUiState.copy(pressuresDetails = pressuresDetails)
    }

    /**
     * Update the target in the [TargetsRepository]'s data source.
     * Updates [pressureUiState], in which it changes the status of targetDetails
     */
    fun updateTargetDetails() {
        pressureUiState.targetDetails?.let {
            viewModelScope.launch {
                val targetDetails = it.copy(accomplished = !it.accomplished).updateStatus()
                val id = viewModelScope.async {
                    targetsRepository.insertTarget(targetDetails.toTarget())
                }.await()
                if (id > EMPTY_INDEX_TARGET_ID) {
                    pressureUiState = pressureUiState.copy(targetDetails = targetDetails)
                }
            }
        }
    }

    /**
     * Retrieves an pressures from the [PressuresRepository] data source by pageIndex. And updates [pressureUiState] by adding them.
     *
     * @param pageIndex - index of the page for which pressures are requested
     * @param time - current time
     */
    private suspend fun getPressures(pageIndex: Long, time: LocalDateTime): PressureChart {
        val timeForPage = getTimeForPage(pageIndex, time)
        val range = createRange(timeForPage, uiFlow.value)
        val pressuresList = getPressuresInRange(range.first, range.second)
        return PressureChart.create(
            currentTime = timeForPage,
            pressureList = pressuresList,
            periodOfTime = uiFlow.value
        )
    }

    /**
     * Retrieves pressures from the [PressuresRepository] that
     * correspond to the range from [startDate] to [endDate].
     */
    private suspend fun getPressuresInRange(startDate: Long, endDate: Long): List<Pressure> {
        return pressuresRepository.getPressuresStream(startDate, endDate).first()
    }

    /**
     * Gets the time in the form of [LocalDateTime] for the page by [pageIndex], taking into account the selected [PeriodOfTime]
     */
    private fun getTimeForPage(pageIndex: Long, time: LocalDateTime): LocalDateTime {
        val newTime = when (uiFlow.value) {
            Day -> time.minusDays(pageIndex)
            Week -> time.minusWeeks(pageIndex)
            Month -> time.minusMonths(pageIndex)
        }
        return newTime
    }


    /**
     * Notifies that the page has been turned
     */
    fun changeSelectedPage(pageIndex: Int) {
        relievePressuresToPreparePage(pageIndex)
    }

    /**
     * Retrieves data to prepare it in advance
     */
    private fun relievePressuresToPreparePage(pageIndex: Int) {
        if (pageIndex == pressureUiState.pressuresDetails.pressureChartsList.count() - 2) {
            viewModelScope.launch {
                val pressureChartsListLocal =
                    pressureUiState.pressuresDetails.pressureChartsList.toMutableList()
                val pressureChartPage = viewModelScope.async {
                    getPressures(
                        pageIndex = pageIndex.toLong() + 2,
                        time = pressureUiState.pressuresDetails.currentTime,
                    )
                }.await()
                pressureChartsListLocal.add(pressureChartPage)
                updatePressuresDetails(
                    pressureUiState.pressuresDetails.copy(
                        pressureChartsList = pressureChartsListLocal.toList(),
                    )
                )
            }
        }
    }

    /**
     * Changes the current periodOfTime
     *
     * Updates the [pressureUiState] with the value provided in the argument.
     * Updates the [uiFlow] with the value provided in the argument.
     */
    fun updatePeriodOfTime(periodOfTime: PeriodOfTime) {
        uiFlow.update { periodOfTime }
    }

    /**
     * Creates a range of values for the start and end times of the selected
     * period in milliseconds, starting from the beginning of the period.
     *
     * @param time - time from which get the current range
     * @param periodOfTime - period of time
     */
    private fun createRange(time: LocalDateTime, periodOfTime: PeriodOfTime): Pair<Long, Long> {
        val localDate = time.toLocalDate()
        val startDate = when (periodOfTime) {
            Day -> localDate.atStartOfDay()// (or) localDate.atTime(LocalTime.MIN)
            Week -> localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay()

            Month -> localDate.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay()
        }
        val endDate = when (periodOfTime) {
            Day -> localDate.atTime(LocalTime.MAX)
            Week -> localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))//.plusDays(1)
                .atTime(LocalTime.MAX)

            Month -> localDate.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX)
        }

        val millisStartDate =
            ZonedDateTime.of(startDate, ZoneId.systemDefault()).toInstant().toEpochMilli()
        val millisEndDate =
            ZonedDateTime.of(endDate, ZoneId.systemDefault()).toInstant().toEpochMilli()

        return Pair(millisStartDate, millisEndDate)
    }
}

/**
 * Ui State for [PressureScreen]
 */
data class PressuresUiState(
    val pressuresDetails: PressuresDetails = PressuresDetails(),
    val targetDetails: TargetDetails? = null,
)

/**
 * Data class for displaying all pressures information
 */
data class PressuresDetails(
    //val pressuresList: List<Pair<LocalDateTime, List<Pressure>>> = listOf(),
    val pressureChartsList: List<PressureChart> = listOf(),
    //val settledPageOfChart: Int = 0,
    val listUpdated: Boolean = false,
    val periodOfTime: PeriodOfTime = Day,
    val currentTime: LocalDateTime = LocalDateTime.now(),
    val marker: MarkerDetails? = null,
    val showAddDataDialog: Boolean = false,
    val isShownAddDataDialog: Boolean = false,
    var sizeAddButton: IntSize = IntSize.Zero,
    var positionInRootAddButton: Offset = Offset.Zero,
)

/**
 * Data class for displaying a chart
 */
data class PressureChart(
    val listEntrySystolic: List<Entry>,
    val listEntryDiastolic: List<Entry>,
    val listEntryNote: List<Entry>,
    val notes: List<Pair<Long, String>>?,
    val dateOrTimeRange: List<String>,
    val maxRangeValue: Float,
    val chartInfo: ChartInfo?,
    val date: LocalDateTime,
) {
    companion object
}

/**
 * Data class for displaying the main chart indicators for the entire selected period
 */
data class ChartInfo(
    val systolicValue: String,
    val diastolicValue: String,
    val pulseValue: String,
)

/**
 * Data class for displaying a [MarkerBig] or [MarkerSmall] on a [ChartBlock]
 */
data class MarkerDetails(
    val x: Int,
    val y: Int,
    val systolicValue: String,
    val diastolicValue: String,
    val pulseValue: String,
    val dateValue: LocalDateTime,
    val showNoteLabel: Boolean,
    val size: Size,
) {
    /**
     * Class that contains the size of the marker. Can be [BIG] or [SMALL]. The size corresponds to the name
     */
    enum class Size { BIG, SMALL }
}

/**
 * Creates [PressureChart] based on [currentTime], [pressureList] and [periodOfTime]
 *
 * Creates an [Entry] and a [MarkerDetails] for each point on the graph.
 *
 * @param currentTime
 * @param pressureList
 * @param periodOfTime
 */
fun PressureChart.Companion.create(
    currentTime: LocalDateTime,
    pressureList: List<Pressure>,
    periodOfTime: PeriodOfTime
): PressureChart {
    val listEntrySystolic: List<Entry>
    val listEntryDiastolic: List<Entry>
    val listEntryNote: List<Entry>
    val chartInfo: ChartInfo?
    if (pressureList.isNotEmpty()) {
        val (
            listEntrySystolicValues: List<Entry>,
            listEntryDiastolicValues: List<Entry>,
            listEntryNoteWithNulls: List<Entry?>,
        ) = pressureList.groupBy {
            val date = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(it.date), ZoneId.systemDefault()
            )
            when (periodOfTime) {
                Day -> date.hour.toFloat()
                Week -> date.dayOfWeek.value.toFloat() - 1f
                Month -> date.dayOfMonth.toFloat() -1f
            }
        }.entries.map { it.mapToEntries() }.unzip()

        val offsetX: Float = when (periodOfTime) {
            Day -> 0.65f
            Week -> 0.19f
            Month -> 0.85f
        }
        val offsetY = 7.8f

        val listEntryNoteValues = listEntryNoteWithNulls.filterNotNull().map {
            it.apply {
                x += offsetX
                y += offsetY
            }
        }

        listEntrySystolic = listEntrySystolicValues
        listEntryDiastolic = listEntryDiastolicValues
        listEntryNote = listEntryNoteValues
        chartInfo = createChartInfo(pressureList)
    } else {
        listEntrySystolic = listOf()
        listEntryDiastolic = listOf()
        listEntryNote = listOf()
        chartInfo = null
    }

    var maxRangeValue = 0f
    val formatterDateTime = DateTimeFormatter.ofPattern(
        when (periodOfTime) {
            Day -> "HH:mm"
            Week, Month -> "dd.MM"
        }
    )
    val notes = createNotes(pressureList)

    val formatterTime = DateTimeFormatter.ofPattern("H")
    val hoursOfDay = 24 + 1
    val dateOfWeek = 7
    val dateOrTimeRange = when (periodOfTime) {
        Day -> {
            maxRangeValue = hoursOfDay.toFloat()
            List(hoursOfDay) {
                if (it == 24) {
                    "0"
                } else {
                    formatterTime.format(currentTime.withHour(it))
                }
            }
        }

        Week -> {
            maxRangeValue = dateOfWeek.toFloat()
            List(maxRangeValue.toInt()) {
                formatterDateTime.format(
                    currentTime.with(
                        TemporalAdjusters.previousOrSame(
                            DayOfWeek.MONDAY
                        )
                    ).plusDays(it.toLong())
                )
            }
        }

        Month -> {
            maxRangeValue =
                currentTime.toLocalDate().lengthOfMonth().toFloat()//dayOfMonth.toFloat()
            List(maxRangeValue.toInt()) {
                formatterDateTime.format(currentTime.withDayOfMonth(it + 1))
            }
        }
    }
    val maxRangeValueChart =
        listEntrySystolic.plus(listEntryNote).takeIf { it.isNotEmpty() }?.maxOf { it.x } ?: 0f
    val maxRange = max(maxRangeValue - 1, maxRangeValueChart)
    return PressureChart(
        listEntrySystolic = listEntrySystolic,
        listEntryDiastolic = listEntryDiastolic,
        listEntryNote = listEntryNote,
        notes = notes,
        dateOrTimeRange = dateOrTimeRange,
        maxRangeValue = maxRange,
        chartInfo = chartInfo,
        date = currentTime,
    )
}

/**
 * Creates a [ChartInfo] from a [pressureList]
 *
 * @param pressureList
 */
private fun createChartInfo(pressureList: List<Pressure>): ChartInfo = ChartInfo(
    systolicValue = pressureList.map { it.systolic }.getMinMaxStringValue(),
    diastolicValue = pressureList.map { it.diastolic }.getMinMaxStringValue(),
    pulseValue = pressureList.map { it.pulse }.getMinMaxStringValue()
)

/**
 * Creates a list of notes based on the pressure list
 */
private fun createNotes(pressureList: List<Pressure>): List<Pair<Long, String>>? {
    val notes = pressureList.mapNotNull {
        if (it.note.isNotEmpty()) {
            Pair(it.date, it.note)
        } else null
    }.ifEmpty { null }
    return notes
}

/**
 * Creates a string from the minimum and maximum values
 */
fun List<Int>.getMinMaxStringValue(): String {
    val minValue: Int = this.min()
    val maxValue: Int = this.max()

    fun getValue(minValue: Int, maxValue: Int): String {
        return if (minValue != maxValue) "$minValue-$maxValue" else minValue.toString()
    }

    return getValue(minValue, maxValue)
}

/**
 * Creates one entry for the graph. In the form of coordinates for
 * systolic pressure, diastolic pressure and notes. Also creates [MarkerDetails] for this entry.
 */
private fun Map.Entry<Float, List<Pressure>>.mapToEntries(): Triple<Entry, Entry, Entry?> {
    val systolicList: MutableList<Int> = mutableListOf()
    val diastolicList: MutableList<Int> = mutableListOf()
    val pulseList: MutableList<Int> = mutableListOf()
    var isNotes = false

    this.value.forEach { pressure ->
        systolicList.add(pressure.systolic)
        diastolicList.add(pressure.diastolic)
        pulseList.add(pressure.pulse)
        pressure.note.isNotEmpty().let { isNotes = it }
    }

    val minSystolic: Int = systolicList.min()
    val maxSystolic: Int = systolicList.max()
    val minDiastolic: Int = diastolicList.min()
    val maxDiastolic: Int = diastolicList.max()
    val minPulse: Int = pulseList.min()
    val maxPulse: Int = pulseList.max()

    val date = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(this.value.first().date), ZoneId.systemDefault()
    )
    val markerSize =
        if (minSystolic != maxSystolic || minDiastolic != maxDiastolic || minPulse != maxPulse) MarkerDetails.Size.BIG else MarkerDetails.Size.SMALL

    fun getValue(minValue: Int, maxValue: Int): String {
        return if (minValue != maxValue) "$minValue - $maxValue" else minValue.toString()
    }

    val emptyCoordinate = -1
    val markerDetails = MarkerDetails(
        x = emptyCoordinate,
        y = emptyCoordinate,
        systolicValue = getValue(minSystolic, maxSystolic),
        diastolicValue = getValue(minDiastolic, maxDiastolic),
        pulseValue = getValue(minPulse, maxPulse),
        dateValue = date,
        showNoteLabel = isNotes,
        size = markerSize
    )
    val systolic = systolicList.average().toFloat()
    val diastolic = diastolicList.average().toFloat()
    val maxValue = if (systolic > diastolic) systolic else diastolic
    val noteEntry = if (isNotes) Entry(this.key, maxValue, markerDetails) else null
    return Triple(
        Entry(this.key, systolic, markerDetails),
        Entry(this.key, diastolic, markerDetails),
        noteEntry
    )
}