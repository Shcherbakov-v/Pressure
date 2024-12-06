package com.mydoctor.pressure.utilities

/**
 * Base-class of [Day], [Week], [Month]
 *
 * Indicates the selected time period
 */
sealed class PeriodOfTime

data object Day : PeriodOfTime()
data object Week : PeriodOfTime()
data object Month : PeriodOfTime()