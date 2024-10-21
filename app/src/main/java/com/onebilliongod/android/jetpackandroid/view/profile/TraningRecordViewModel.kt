package com.onebilliongod.android.jetpackandroid.view.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import kotlin.random.Random

/**
 * Viewmodel for managing the training record generation
 */
@HiltViewModel
class TrainingRecordViewModel @Inject constructor() : ViewModel() {
    //control the data generation
    //It will load the next page of data when haveData is false
    private var haveData = true

    //
    private val _trainingRecords = MutableStateFlow<List<TrainingRecord>>(emptyList())
    val trainingRecords: StateFlow<List<TrainingRecord>> = _trainingRecords

    private val _historyData = MutableStateFlow(listOf(ChartData(0f, 0f, 0f, 0f)))
    val historyData: StateFlow<List<ChartData>> = _historyData

    fun loadTrainingRecords() {
        if (!haveData) {
            Log.i("TrainingRecordViewModel", "no data")
            return
        }

        //update records use mock data
        //you can use backend api if you have
        _trainingRecords.update { currentList  ->
            currentList + mockTrainingRecords()
        }

        haveData = false
    }

    fun getData(recordId: Long) {
        _historyData.value = mockChartData()
    }

}

//define training record data struct
data class TrainingRecord (val recordId: Long, val startTime: Long, val endTime: Long)

//mock data for test
fun mockTrainingRecords(): List<TrainingRecord> {
    val zoneId = ZoneId.systemDefault()
    val startDateTime = LocalDateTime.now().minusDays(2).withHour(9).withMinute(0).withSecond(0)

    return List(10) { index ->
        val startDateTimeRecord = startDateTime.plusMinutes(30L * index)
        val endDateTimeRecord = startDateTimeRecord.plusMinutes(30)

        val startTime = startDateTimeRecord.atZone(zoneId).toInstant().toEpochMilli()
        val endTime = endDateTimeRecord.atZone(zoneId).toInstant().toEpochMilli()

        TrainingRecord(recordId = index + 1L, startTime = startTime, endTime = endTime)
    }
}

//define chart data struct
data class ChartData(val time: Float, val value1: Float, val value2: Float, val value3: Float)

fun mockChartData(): List<ChartData> {
    return List(10) { index ->
        val time = (index + 1).toFloat() // 从 1 开始叠加
        val value1 = Random.nextFloat() * 100 // 随机生成 0 到 100 的浮点数
        val value2 = Random.nextFloat() * 100
        val value3 = Random.nextFloat() * 100

        ChartData(time, value1, value2, value3)
    }
}