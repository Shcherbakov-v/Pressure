package com.mydoctor.pressure.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineTargetsRepository @Inject constructor(private val targetDao: TargetDao) :
    TargetsRepository {
    override fun getAllTargetsStream(): Flow<List<Target>> = targetDao.getAllTargets()

    override fun getTargetStream(id: Long): Flow<Target?> = targetDao.getTarget(id)

    override suspend fun insertTarget(target: Target): Long = targetDao.insert(target)

    override suspend fun deleteTarget(target: Target) = targetDao.delete(target)
}
