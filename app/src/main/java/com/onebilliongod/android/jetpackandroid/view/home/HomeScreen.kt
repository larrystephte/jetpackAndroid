package com.onebilliongod.android.jetpackandroid.view.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * HomeScreen composable that displays the main content of the home page.
 * It includes a chart section and settings options.
 */
@Composable
fun HomeScreen(modifier: Modifier = Modifier,
               chartViewModel: ChartViewModel = hiltViewModel()) {
    Column(
        modifier
            .verticalScroll(rememberScrollState())
    ) {
        ComposeChart(
            viewModel= chartViewModel,
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .padding(16.dp))

        SettingScreen(
            chartViewModel = chartViewModel,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )

    }
}