package com.onebilliongod.android.jetpackandroid.view.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.onebilliongod.android.jetpackandroid.view.home.chartColors
import com.onebilliongod.android.jetpackandroid.view.home.rememberLegend
import com.onebilliongod.android.jetpackandroid.view.home.rememberMarker
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shape.rounded
import com.patrykandpatrick.vico.core.cartesian.AutoScrollCondition
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shape.Shape
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Composable function for the training record screen.
 */
@Composable
fun TrainingRecordScreen(viewModel: TrainingRecordViewModel = hiltViewModel()) {
    val trainingRecords by viewModel.trainingRecords.collectAsState()
    val listState = rememberLazyListState()

    //Detect scroll to bottom and load more data.
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex == listState.layoutInfo.totalItemsCount - 1) {
                    viewModel.loadTrainingRecords()
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "record id",
                        modifier = Modifier.weight(1f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "start time",
                        modifier = Modifier.weight(1f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "end time",
                        modifier = Modifier.weight(1f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "operator",
                        modifier = Modifier.weight(1f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        items(trainingRecords) { record ->
            TrainingRecordRow(record, viewModel)
        }
    }

}

/**
 * Composable function for the TrainingRecordRow
 */
@Composable
fun TrainingRecordRow(record: TrainingRecord, viewModel: TrainingRecordViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${record.recordId}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "${formatTimestamp(record.startTime)}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "${formatTimestamp(record.endTime)}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { /* TODO: report */ },
                    shape = RoundedCornerShape(80.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .defaultMinSize(0.dp, 0.dp)
                        .height(30.dp)
                        .width(70.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "report", fontSize = 12.sp)
                }
                Button(
                    onClick = {
                        viewModel.getData(record.recordId)
                        showDialog = true
                    },
                    shape = RoundedCornerShape(80.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .defaultMinSize(0.dp, 0.dp)
                        .height(30.dp)
                        .width(70.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "chart", fontSize = 12.sp)
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog= false  },
            title = {
                Text(text = "history chart", style = MaterialTheme.typography.titleLarge)
            },
            text = {
                Box(modifier = Modifier
                    .height(300.dp)
                    .width(1000.dp)) {
                    HistoryChartScreen(viewModel = viewModel)
                }
            },
            confirmButton = {
                Button(onClick = { showDialog= false  }) {
                    Text("close")
                }
            }
        )
    }

}


/**
 * Composable function for History Chart
 */
@Composable
fun HistoryChartScreen(viewModel: TrainingRecordViewModel) {
    val historyData by viewModel.historyData.collectAsState()

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(historyData) {
        val xValuesList = historyData.map { v -> v.time }.toList()
        val yValuesList = historyData.map { it.value1 }.toList()
        val yValuesList2 = historyData.map { it.value2 }.toList()
        val yValuesList3 = historyData.map { it.value3 }.toList()
        modelProducer.runTransaction {
            lineSeries {
                series(x = xValuesList, y = yValuesList)
                series(x = xValuesList, y = yValuesList2)
                series(x = xValuesList, y = yValuesList3)
            }
        }
    }

    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    chartColors.map { color ->
                        rememberLine(
                            fill = remember { LineCartesianLayer.LineFill.single(fill(color)) },
                        )
                    }
                )
            ),
            startAxis =
            rememberStartAxis(
                label = rememberAxisLabelComponent(
                    color = Color.Black,
                    margins = Dimensions.of(4.dp),
                    padding = Dimensions.of(8.dp, 2.dp),
                    background = rememberShapeComponent(Color(0xfffab94d), Shape.rounded(4.dp)),
                ),
                horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
            ),
            bottomAxis = rememberBottomAxis(),
            marker = rememberMarker(),
            legend = rememberLegend(),

            ),
        modelProducer = modelProducer,
        modifier = Modifier.width(1000.dp).height(270.dp),
        scrollState = rememberVicoScrollState(
            initialScroll = Scroll.Absolute.End,
            autoScrollCondition = AutoScrollCondition.OnModelSizeIncreased,
        ),
        zoomState = rememberVicoZoomState(zoomEnabled = false),
    )
}

fun formatTimestamp(timestamp: Long): String {
    val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return localDateTime.format(formatter)
}

@Preview(showBackground = true)
@Composable
fun PreviewTrainingRecordPage(viewModel: TrainingRecordViewModel = hiltViewModel()) {

    TrainingRecordScreen(viewModel)
}

