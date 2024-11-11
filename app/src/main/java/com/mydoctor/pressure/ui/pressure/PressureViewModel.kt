package com.mydoctor.pressure.ui.pressure

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mydoctor.pressure.data.Pressure
import com.mydoctor.pressure.data.PressuresRepository
import com.mydoctor.pressure.utilities.Day
import com.mydoctor.pressure.utilities.Month
import com.mydoctor.pressure.utilities.PeriodOfTime
import com.mydoctor.pressure.utilities.Week
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class PressureViewModel @Inject constructor(private val pressuresRepository: PressuresRepository) :
    ViewModel() {

    private val uiFlow: MutableStateFlow<Pair<PeriodOfTime, Long>> =
        MutableStateFlow(
            Pair(
                Day,
                ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant()
                    .toEpochMilli()
            )
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val pressureListState: StateFlow<HomeUiState> = uiFlow.flatMapLatest {
        val range = getRange(it.second, it.first)
        pressuresRepository.getPressuresStream(range.first, range.second)
    }.map {
        HomeUiState(
            pressureList = it,
            periodOfTime = uiFlow.value.first,
            selectedTime = uiFlow.value.second
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = HomeUiState()
    )

    /**
     * Updates the [pressureUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(periodOfTime: PeriodOfTime) {
        uiFlow.update { Pair(periodOfTime, uiFlow.value.second) }
    }

    private fun getRange(time: Long, periodOfTime: PeriodOfTime): Pair<Long, Long> {
        val localDate = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDate()
        val startDate = when (periodOfTime) {
            Day -> localDate.atStartOfDay()// (or) localDate.atTime(LocalTime.MIN)
            Week -> localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay()

            Month -> localDate.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay()
        }
        val endDate = when (periodOfTime) {
            Day -> localDate.atTime(LocalTime.MAX)
            Week -> localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY)).plusDays(1)
                .atTime(LocalTime.MAX)

            Month -> localDate.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX)
        }

        val millisStartDate = ZonedDateTime.of(startDate, ZoneId.systemDefault()).toInstant()
            .toEpochMilli()
        val millisEndDate = ZonedDateTime.of(endDate, ZoneId.systemDefault()).toInstant()
            .toEpochMilli()

        return Pair(millisStartDate, millisEndDate)
    }

    private companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * Ui State for HomeScreen
 */
data class HomeUiState(
    val pressureList: List<Pressure> = listOf(),
    val periodOfTime: PeriodOfTime = Day,
    val selectedTime: Long =
        ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant()
            .toEpochMilli()
)