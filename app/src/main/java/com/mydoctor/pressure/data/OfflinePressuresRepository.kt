package com.mydoctor.pressure.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflinePressuresRepository @Inject constructor(private val pressureDao: PressureDao) :
    PressuresRepository {
    override fun getPressuresStream(startDate: Long, endDate: Long): Flow<List<Pressure>> =
        pressureDao.getPressures(startDate ,endDate)

    override fun getPressureStream(id: Int): Flow<Pressure?> = pressureDao.getPressure(id)

    override suspend fun insertPressure(pressure: Pressure) = pressureDao.insert(pressure)

    override suspend fun deletePressure(pressure: Pressure) = pressureDao.delete(pressure)
}