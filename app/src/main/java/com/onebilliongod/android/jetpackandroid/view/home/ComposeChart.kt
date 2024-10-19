package com.onebilliongod.android.jetpackandroid.view.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.data.rememberExtraLambda
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.compose.common.shape.rounded
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.core.cartesian.AutoScrollCondition
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawingContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.Legend
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.shape.Shape

/**
 * ComposeChart composable that renders a 3-line chart using the Vico chart library.
 * This chart displays three data series collected from ChartViewModel.
 *
 * - The chart consists of three lines representing different data sets (y, y2, and y3).
 * - Uses Vico's Cartesian chart components to render a visually dynamic and customizable chart.
 */
@Composable
fun ComposeChart(modifier: Modifier, viewModel: ChartViewModel) {
    val chartData by viewModel.chartData.collectAsState()

    // Mutable lists to keep track of x and y values for three data series
    val xValuesList = remember { mutableStateListOf<Int>() }
    val yValuesList = remember { mutableStateListOf<Float>() }
    val yValuesList2 = remember { mutableStateListOf<Float>() }
    val yValuesList3 = remember { mutableStateListOf<Float>() }

    val modelProducer = remember { CartesianChartModelProducer() }

    // Updating data values and applying them to the chart when chartData changes
    LaunchedEffect(chartData) {
        xValuesList.add(chartData.x)
        yValuesList.add(chartData.y)
        yValuesList2.add(chartData.y2)
        yValuesList3.add(chartData.y3)

        modelProducer.runTransaction {
            lineSeries {
                series(x = xValuesList, y = yValuesList)
                series(x = xValuesList, y = yValuesList2)
                series(x = xValuesList, y = yValuesList3)
            }
        }
    }

    // Create and display a Cartesian chart using the Vico library
    CartesianChartHost(
        chart =
        rememberCartesianChart(
            // Define the chart layer, providing three lines with individual colors
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    chartColors.map { color ->
                        rememberLine(
                            fill = remember { LineCartesianLayer.LineFill.single(fill(color)) },
                        )
                    }
                )
            ),
            // Configure the start (Y) axis with customized labels and styles
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
            // Configure marker to indicate points on the chart
            marker = rememberMarker(),
            legend = rememberLegend(),

            ),
        modelProducer = modelProducer,
        modifier = modifier,
        scrollState = rememberVicoScrollState(
            initialScroll = Scroll.Absolute.End,
            autoScrollCondition = AutoScrollCondition.OnModelSizeIncreased,
        ),
        zoomState = rememberVicoZoomState(zoomEnabled = false),
    )
}

@Composable
fun rememberLegend(): Legend<CartesianMeasuringContext, CartesianDrawingContext> {
    val labelComponent = rememberTextComponent(vicoTheme.textColor)
    val resources = LocalContext.current.resources
    return rememberHorizontalLegend(
        items =
        rememberExtraLambda {
            chartColors.forEachIndexed { index, color ->
                add(
                    LegendItem(
                        icon = shapeComponent(color, Shape.Pill),
                        labelComponent = labelComponent,
                        label = "Series $index",
                    )
                )
            }
        },
        iconSize = 8.dp,
        iconPadding = 8.dp,
        spacing = 4.dp,
        padding = Dimensions.of(top = 8.dp),
    )
}

val chartColors = listOf(Color(0xffb983ff), Color(0xff91b1fd), Color(0xff8fdaff))
