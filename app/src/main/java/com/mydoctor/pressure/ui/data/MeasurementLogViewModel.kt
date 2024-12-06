package com.mydoctor.pressure.ui.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mydoctor.pressure.data.Pressure
import com.mydoctor.pressure.data.PressuresRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

/**
 *  ViewModel to retrieve all pressures in the Room database.
 */
@HiltViewModel
class MeasurementLogViewModel @Inject constructor(pressuresRepository: PressuresRepository) :
    ViewModel() {

    /**
     * Holds current state of collapsing and expanding the month list
     */
    private var expandedMouthsList: List<Int> by mutableStateOf(listOf())

    /**
     * Holds current state of the user interface of the measurement log, modified by the user
     */
    private val measurementLogFlow: MutableStateFlow<MeasurementLogUiState> =
        MutableStateFlow(MeasurementLogUiState())

    /**
     * Holds current measurement log ui state
     */
    val measurementLogUiState: StateFlow<MeasurementLogUiState> =
        merge(
            pressuresRepository.getAllPressuresStream()
                .map { pressureList ->
                    val pressureListByMonth = pressureList
                        .reversed()
                        .groupByMonth()
                        .mapIndexed { index, it ->
                            val monthWithYear = getMonthWithYear(it)
                            val expanded = index == 0 || expandedMouthsList.contains(monthWithYear)
                            PressureMeasurementLogBlock(
                                monthWithYear = monthWithYear,
                                expanded = expanded,
                                pressureList = it
                            )
                        }
                    MeasurementLogUiState(
                        pressureMeasurementLogBlockList = pressureListByMonth
                    )
                },
            measurementLogFlow
        )
            .filter { it.pressureMeasurementLogBlockList.isNotEmpty() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = MeasurementLogUiState()
            )

    /**
     * Changes the data about the collapse and expansion of the list of months
     */
    fun sendStateBlockChangedEvent(indexMonth: Int) {
        val pressureBlockList = measurementLogUiState.value.pressureMeasurementLogBlockList
        val mouthWithYear = pressureBlockList[indexMonth].monthWithYear
        val expandedMouthsMutableList = expandedMouthsList.toMutableList()
        if (expandedMouthsMutableList.contains(element = mouthWithYear)) {
            expandedMouthsMutableList.remove(mouthWithYear)
        } else {
            expandedMouthsMutableList.add(mouthWithYear)
        }
        expandedMouthsList = expandedMouthsMutableList
        val pressureBlockListMutable = pressureBlockList.toMutableList()
        pressureBlockListMutable[indexMonth] = pressureBlockListMutable[indexMonth].copy(
            expanded = !pressureBlockListMutable[indexMonth].expanded
        )
        measurementLogFlow.update {
            measurementLogUiState.value.copy(
                pressureMeasurementLogBlockList = pressureBlockListMutable
            )
        }
    }

    /**
     * Groups the list of pressures by month
     */
    private fun List<Pressure>.groupByMonth(): List<List<Pressure>> {
        return this
            .groupBy { pressure ->
                val date =
                    Instant.ofEpochMilli(pressure.date).atZone(ZoneId.systemDefault())
                val monthWithYear = date.month.value + date.year
                return@groupBy monthWithYear
            }
            .values
            .toList()
    }

    /**
     * Gets the month and year from the pressure list (All items in the list have the same year and month)
     */
    private fun getMonthWithYear(pressureList: List<Pressure>): Int {
        val date = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(pressureList.first().date),
            ZoneId.systemDefault()
        )
        val monthWithYear = date.month.value + date.year
        return monthWithYear
    }

    private companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * Ui State for MeasurementLogScreen
 */
data class MeasurementLogUiState(
    val pressureMeasurementLogBlockList: List<PressureMeasurementLogBlock> = listOf(),
)

/**
 * Data for displaying one month and all pressures in it.
 */
data class PressureMeasurementLogBlock(
    val monthWithYear: Int,
    val expanded: Boolean,
    val pressureList: List<Pressure>,
)