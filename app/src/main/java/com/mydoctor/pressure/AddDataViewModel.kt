package com.mydoctor.pressure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mydoctor.pressure.data.Pressure
import com.mydoctor.pressure.data.PressuresRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDataViewModel @Inject constructor(private val pressuresRepository: PressuresRepository) : ViewModel() {
    fun saveItem(pressure: Pressure) = viewModelScope.launch {
        pressuresRepository.insertPressure(pressure)
    }
}

/*
class PostViewModel @ViewModelInject constructor(private val postRepository: PostRepository) : ViewModel() {

    fun saveItem(pressure: Pressure) = viewModelScope.launch {
        postRepository.insert(pressure)
    }
}*/
