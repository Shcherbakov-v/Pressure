package com.mydoctor.pressure.ui.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mydoctor.pressure.data.Pressure
import com.mydoctor.pressure.data.PressuresRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel to validate and insert pressures in the Room database.
 */
@HiltViewModel
class AddDataViewModel @Inject constructor(private val pressuresRepository: PressuresRepository) :
    ViewModel() {

    /**
     * Holds current pressure ui state
     */
    var pressureUiState by mutableStateOf(PressureUiState())
        private set

    /**
     * Updates the [pressureUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(pressureDetails: PressureDetails) {
        pressureUiState =
            PressureUiState(
                pressureDetails = pressureDetails,
                isEntryValid = validateInput(pressureDetails)
            )
    }

    /**
     * Inserts an [Pressure] in the Room database
     */
    suspend fun savePressure() {
        if (validateInput()) {
            pressuresRepository.insertPressure(pressureUiState.pressureDetails.toPressure())
        }
    }

    private fun validateInput(uiState: PressureDetails = pressureUiState.pressureDetails): Boolean {
        return with(uiState) {
            systolic.isNotBlank() && diastolic.isNotBlank()
        }
    }
}

/**
 * Represents Ui State for an Pressure.
 */
data class PressureUiState(
    val pressureDetails: PressureDetails = PressureDetails(),
    val isEntryValid: Boolean = false
)

data class PressureDetails(
    val id: Long = 0,
    val systolic: String = "",
    val diastolic: String = "",
    val pulse: String = "",
    val date: Long = 0,
    val dateCreated: Boolean = false,
    val dateSelected: Boolean = false,
    val timeSelected: Boolean = false,
    val note: String = "",
)

/**
 * Extension function to convert [PressureUiState] to [Pressure]. If the value of [PressureDetails.systolic] is
 * not a valid [Int], then the price will be set to 0. Similarly if the value of
 * [PressureUiState] is not a valid [Int], then the quantity will be set to 0
 */
fun PressureDetails.toPressure(): Pressure = Pressure(
    id = id,
    systolic = systolic.toIntOrNull() ?: 0,
    diastolic = diastolic.toIntOrNull() ?: 0,
    pulse = pulse.toIntOrNull() ?: 0,
    date = date,
    note = note,
)

/**
 * Extension function to convert [Pressure] to [PressureUiState]
 */
fun Pressure.toPressureUiState(isEntryValid: Boolean = false): PressureUiState = PressureUiState(
    pressureDetails = this.toPressureDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Pressure] to [PressureDetails]
 */
fun Pressure.toPressureDetails(): PressureDetails = PressureDetails(
    id = id,
    systolic = systolic.toString(),
    diastolic = diastolic.toString(),
    pulse = pulse.toString(),
    date = date,
    note = note,
)

