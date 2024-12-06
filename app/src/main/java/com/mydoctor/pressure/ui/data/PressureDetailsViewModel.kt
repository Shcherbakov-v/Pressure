package com.mydoctor.pressure.ui.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mydoctor.pressure.data.PressuresRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel to retrieve and delete an pressure from the [PressuresRepository]'s data source.
 */
@HiltViewModel
class PressureDetailsViewModel @Inject constructor(
    private val pressuresRepository: PressuresRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /**
     * Holds current pressure ui state
     */
    var pressureUiState by mutableStateOf(PressureDetailsUiState())
        private set

    /**
     * Stores the id received from the previous screen
     */
    private val pressureId: Long =
        checkNotNull(savedStateHandle[PressureDetailsDestination.pressureIdArg])

    init {
        viewModelScope.launch {
            pressureUiState = PressureDetailsUiState(
                pressureDetails = pressuresRepository.getPressureStream(pressureId)
                    .filterNotNull()
                    .first()
                    .toPressureDetails()
            )

        }
    }

    /**
     * Updates the [pressureUiState] with the value provided in the argument.
     */
    fun updateUiState(isDeleteDialog: Boolean) {
        pressureUiState =
            PressureDetailsUiState(
                pressureDetails = pressureUiState.pressureDetails.copy(),
                isDeleteDialog = isDeleteDialog,
            )
    }

    /**
     * Deletes the pressure from the [PressuresRepository]'s data source.
     */
    suspend fun deleteItem() {
        pressuresRepository.deletePressure(pressureUiState.pressureDetails.toPressure())
    }
}

/**
 * Ui State for PressureDetailsScreen
 */
data class PressureDetailsUiState(
    val pressureDetails: PressureDetails = PressureDetails(),
    val isDeleteDialog: Boolean = false
)