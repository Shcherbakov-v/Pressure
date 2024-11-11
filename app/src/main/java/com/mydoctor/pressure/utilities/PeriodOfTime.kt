package com.mydoctor.pressure.utilities

sealed class PeriodOfTime

data object Day : PeriodOfTime()
data object Week : PeriodOfTime()
data object Month : PeriodOfTime()