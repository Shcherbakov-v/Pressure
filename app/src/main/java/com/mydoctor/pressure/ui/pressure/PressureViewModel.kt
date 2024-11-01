package com.mydoctor.pressure.ui.pressure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mydoctor.pressure.data.Pressure
import com.mydoctor.pressure.data.PressuresRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PressureViewModel @Inject constructor(private val pressuresRepository: PressuresRepository) :
    ViewModel() {

    val pressureUiState: StateFlow<HomeUiState> =
        pressuresRepository.getAllPressuresStream().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 0L//5_000L
    }
}

/**
 * Ui State for HomeScreen
 */
data class HomeUiState(val pressureList: List<Pressure> = listOf())