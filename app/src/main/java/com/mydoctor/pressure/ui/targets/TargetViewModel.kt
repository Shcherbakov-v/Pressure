package com.mydoctor.pressure.ui.targets

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mydoctor.pressure.data.Target
import com.mydoctor.pressure.data.TargetsRepository
import com.mydoctor.pressure.ui.targets.TargetDetails.Companion.EMPTY_INDEX_TARGET_ID
import com.mydoctor.pressure.utilities.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

/**
 * ViewModel to retrieve, update and delete an target from the [TargetsRepository]'s data source.
 */
@HiltViewModel
class TargetViewModel @Inject constructor(
    private val targetsRepository: TargetsRepository,
    savedStateHandle: SavedStateHandle,
) :
    ViewModel() {

    /**
     * Holds current target ui state
     */
    var targetUiState by mutableStateOf(TargetUiState())
        private set

    /**
     * Stores the id received from the previous screen
     */
    private val targetId: Long =
        checkNotNull(savedStateHandle[TargetDestination.targetIdArg])

    init {
        if (targetId > EMPTY_INDEX_TARGET_ID) {
            viewModelScope.launch {
                val targetDetails = targetsRepository.getTargetStream(targetId)
                    .filterNotNull()
                    .first()
                    .toTargetDetails()
                targetUiState = TargetUiState(
                    allowEditing = false,
                    targetDetails = targetDetails,
                    isEntryValid = validateInput(targetDetails),
                )
            }
        }
    }

    /**
     * Updates the [targetUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(targetDetails: TargetDetails) {
        targetUiState =
            targetUiState.copy(
                targetDetails = targetDetails,
                isEntryValid = validateInput(targetDetails)
            )
    }

    /**
     * Allows editing
     */
    fun editTarget() {
        targetUiState = targetUiState.copy(allowEditing = true)
    }

    /**
     * Inserts an [Target] in the Room database. And assigns the received id
     */
    suspend fun saveTarget() {
        if (validateInput()) {
            targetUiState = targetUiState.copy(allowEditing = false)
            val id = viewModelScope.async {
                targetsRepository.insertTarget(targetUiState.targetDetails.toTarget())
            }.await()
            Log.d(TAG, "saveTarget: targetId: $id")
            updateUiState(
                targetDetails = targetUiState.targetDetails.copy(
                    id = id,
                    isCreated = true
                )
            )
        }
    }

    /**
     * Deletes the Target from the [TargetsRepository]'s data source.
     */
    suspend fun deleteTarget() {
        targetsRepository.deleteTarget(targetUiState.targetDetails.toTarget())
    }

    private fun validateInput(uiState: TargetDetails = targetUiState.targetDetails): Boolean {
        return with(uiState) {
            description.isNotBlank() && dateSelected //&& timeSelected
        }
    }
}

/**
 * Represents Ui State for an Target.
 */
data class TargetUiState(
    val currentTime: Long = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli(),
    val targetDetails: TargetDetails = TargetDetails(),
    val isEntryValid: Boolean = false,
    val allowEditing: Boolean = true,
)

data class TargetDetails(
    val id: Long = EMPTY_INDEX_TARGET_ID,
    val description: String = "",
    val date: Long = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli(),
    val accomplished: Boolean = false,
    val status: Status = run {
        val targetDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(date),
            ZoneId.systemDefault()
        )
        val currentTime = LocalDateTime.now()
        val previousDay = currentTime.dayOfYear == targetDateTime.minusDays(1L).dayOfYear
        val todayBeforeCurrentTime =
            targetDateTime.dayOfYear == currentTime.dayOfYear && currentTime < targetDateTime
        when {
            accomplished -> Status.ACCOMPLISHED
            previousDay || todayBeforeCurrentTime -> Status.ALMOST_OVERDUE
            targetDateTime < currentTime -> Status.OVERDUE
            else -> Status.SET
        }
    },
    val dateSelected: Boolean = false,
    val timeSelected: Boolean = false,
    val isCreated: Boolean = false
) {
    enum class Status { SET, ALMOST_OVERDUE, OVERDUE, ACCOMPLISHED }
    companion object {
      const val EMPTY_INDEX_TARGET_ID = -1L
    }
}

/**
 * Extension function to convert [TargetUiState] to [Target].
 * If the target has an empty index, it will be assigned the value 0.
 */
fun TargetDetails.toTarget(): Target = Target(
    id = if (id == EMPTY_INDEX_TARGET_ID) 0 else id,
    description = description,
    date = date,
    accomplished = accomplished,
)

/**
 * Extension function to convert [Target] to [TargetDetails]
 */
fun Target.toTargetDetails(): TargetDetails {
    val targetDateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(date),
        ZoneId.systemDefault()
    )
    val currentTime = LocalDateTime.now()
    val previousDay = currentTime.dayOfYear == targetDateTime.minusDays(1L).dayOfYear
    val todayBeforeCurrentTime =
        targetDateTime.dayOfYear == currentTime.dayOfYear && currentTime < targetDateTime
    return TargetDetails(
        id = id,
        description = description,
        date = date,
        accomplished = accomplished,
        status = when {
            accomplished -> TargetDetails.Status.ACCOMPLISHED
            previousDay || todayBeforeCurrentTime -> TargetDetails.Status.ALMOST_OVERDUE
            targetDateTime < currentTime -> TargetDetails.Status.OVERDUE
            else -> TargetDetails.Status.SET
        },
        isCreated = true,
        dateSelected = true,
        timeSelected = true
    )
}

/**
 * TargetDetails Status Update extension function
 */
fun TargetDetails.updateStatus(): TargetDetails {
    val targetDateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(date),
        ZoneId.systemDefault()
    )
    val currentTime = LocalDateTime.now()
    val previousDay = currentTime.dayOfYear == targetDateTime.minusDays(1L).dayOfYear
    val todayBeforeCurrentTime =
        targetDateTime.dayOfYear == currentTime.dayOfYear && currentTime < targetDateTime
    return copy(
        status = when {
            accomplished -> TargetDetails.Status.ACCOMPLISHED
            previousDay || todayBeforeCurrentTime -> TargetDetails.Status.ALMOST_OVERDUE
            targetDateTime < currentTime -> TargetDetails.Status.OVERDUE
            else -> TargetDetails.Status.SET
        }
    )
}
