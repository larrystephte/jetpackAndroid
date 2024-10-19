package com.onebilliongod.android.jetpackandroid.view.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

data class ChartData(
    val x : Int,
    val y : Float,
    val y2 : Float,
    val y3 : Float,
)

/**
 * ViewModel for managing the chart data generation.
 * Handles generating random data for the chart and maintains the state for data points.
 */
@HiltViewModel
class ChartViewModel @Inject constructor() : ViewModel() {
    var isGeneratingData by mutableStateOf(false)
        private set

    private var dataGenerationJob: Job? = null

    //StateFlow to provide chart data to the UI in a reactive manner
    private val startData = ChartData(0, 0f, 0f, 0f)
    private val _chartData = MutableStateFlow(startData)
    val chartData: StateFlow<ChartData> = _chartData

    private val starts = mutableIntStateOf(0)


    /**
     * Starts generating random data points for the chart.
     * Runs on a loop and updates the data every 500ms until stopped.
     * notice: this random data is mock for test
     */
    fun startDataGeneration() {
        if (isGeneratingData) return

        isGeneratingData = true

        dataGenerationJob  = viewModelScope.launch {
            while (isGeneratingData) {
                val nextY = Random.nextFloat() * 15

                delay(500)

                starts.intValue += 1
                _chartData.value = ChartData(
                    x = starts.intValue,
                    y = nextY,
                    y2 = nextY * Random.nextFloat(),
                    y3 = nextY * Random.nextFloat()
                )
            }
        }
    }

    fun stopDataGeneration() {
        isGeneratingData = false

        dataGenerationJob?.cancel()
        dataGenerationJob = null
    }
}