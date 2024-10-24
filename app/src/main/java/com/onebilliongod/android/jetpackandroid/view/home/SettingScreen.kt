package com.onebilliongod.android.jetpackandroid.view.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.onebilliongod.android.jetpackandroid.data.socket.client.TcpClient
import com.techtrend.intelligent.chunli_clr.view.home.viewmodel.TcpViewModel

data class TrainingMode(val id: Int, val name: String)

/**
 * SettingScreen composable that
 */
@Composable
fun SettingScreen(modifier: Modifier = Modifier,
                  viewModel: TcpViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var selectedModeId by remember { mutableStateOf<Int>(1) }
    var selectedModeName by remember { mutableStateOf<String>("driving") }

    val trainingModes = listOf(
        TrainingMode(id = 1, name = "driving"),
        TrainingMode(id = 2, name = "passive"),
    )

    var trainingTime by remember { mutableStateOf(0f) }
    var walkingDistance by remember { mutableStateOf(0f) }
    var speed by remember { mutableStateOf(0f) }
    var jointAngle by remember { mutableStateOf(0f) }

    Column(
        modifier = modifier,  // Enable vertical scrolling,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.End, // 两列均匀分布
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                onClick = {
                    viewModel.start()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Magenta,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(0.dp),
                enabled = true,
                modifier = Modifier
                    .defaultMinSize(0.dp, 0.dp)
                    .height(30.dp)
                    .width(70.dp)
            ) {
                Text(
                    "start",
                    fontSize = 12.sp,
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Button(
                onClick = {
                    viewModel.stop()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(80.dp),
                contentPadding = PaddingValues(0.dp),
                enabled = true,
                modifier = Modifier
                    .defaultMinSize(0.dp, 0.dp)
                    .height(30.dp)
                    .width(70.dp)
            ) {
                Text(
                    "stop",
                    fontSize = 12.sp,
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Button(
                onClick = {
                    //TODO save data
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(80.dp),
                contentPadding = PaddingValues(0.dp),
                enabled = true,
                modifier = Modifier
                    .defaultMinSize(0.dp, 0.dp)
                    .height(30.dp)
                    .width(70.dp)
            ) {
                Text(
                    "save",
                    fontSize = 12.sp,
                )
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp) // 行的布局和内边距
        ) {
            Text(text = "select model：")

            //drop down model menu
            Row {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .width(130.dp)
                        .height(30.dp),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Icon(Icons.Default.Info, contentDescription = "select model")
                    Text(text = selectedModeName, fontSize = 12.sp)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "expand model")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    trainingModes.forEach { mode ->
                        DropdownMenuItem(
                            onClick = {
                                selectedModeId = mode.id
                                selectedModeName = mode.name
                                expanded = false
                            },
                            text = { Text(mode.name) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A4A4A),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(8.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .defaultMinSize(0.dp, 0.dp)
                    .height(30.dp)
                    .width(100.dp)
            ) {
                Text(
                    "save setting",
                    fontSize = 12.sp,
                )
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text("training time: ${trainingTime.toInt()} (minute)")
                Slider(
                    value = trainingTime,
                    onValueChange = { trainingTime = it },
                    valueRange = 0f..30f,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text("distance: ${walkingDistance.toInt()} (m)")
                Slider(
                    value = walkingDistance,
                    onValueChange = { walkingDistance = it },
                    valueRange = 0f..30f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text("speed: ${speed.toInt()} km/h")
                Slider(
                    value = speed,
                    onValueChange = { speed = it },
                    valueRange = 0f..10f,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text("joint angle: ${jointAngle.toInt()} angle")
                Slider(
                    value = jointAngle,
                    onValueChange = { jointAngle = it },
                    valueRange = 0f..180f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingPreview() {
    val navController = rememberNavController()

//    SettingScreen(chartViewModel = TcpViewModel(), modifier = Modifier
//        .fillMaxSize()
//        .padding(16.dp))
}

