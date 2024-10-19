package com.onebilliongod.android.jetpackandroid.view.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.onebilliongod.android.jetpackandroid.R
import com.onebilliongod.android.jetpackandroid.data.local.PreferenceKeys
import com.onebilliongod.android.jetpackandroid.data.local.PreferencesManager
import com.onebilliongod.android.jetpackandroid.data.remote.api.AuthEvent
import com.onebilliongod.android.jetpackandroid.data.remote.api.EventBus
import com.onebilliongod.android.jetpackandroid.view.login.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(windowSize: WindowWidthSizeClass) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "profile") {
        composable("profile") { ProfileScreenLayout(windowSize) }
    }
}

@Composable
fun ProfileScreenLayout(windowSize: WindowWidthSizeClass, authViewModel: AuthViewModel = hiltViewModel()) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    var isTablet = windowSize != WindowWidthSizeClass.Compact

    if (isTablet) {
        Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))) {
            ProfileHeader()
            HorizontalDivider(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.spacing_medium)))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.spacing_large)
            )) {
                Column(modifier = Modifier.weight(1f)) {
                    MyPageCard(title = "Training Record", onClick = {
                        /* TODO Training Record List */
                    }, isCompact = true)
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_large)))
                    MyPageCard(title = "Help", onClick = { /* TODO Help */ }, isCompact = true)
                }
                Column(modifier = Modifier.weight(1f)) {
                    MyPageCard(title = "Setting", onClick = {
                        //TODO setting
                    }, isCompact = true)
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_large)))
                    MyPageCard(title = "Logout", onClick = {
                        showLogoutDialog = true
                    }, isCompact = true)
                }
            }
        }
    } else {
        Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)), verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_large))) {
            ProfileHeader()
            HorizontalDivider(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.spacing_medium)))
            MyPageCard(title = "Training Record", onClick = { /* TODO */ }, isCompact = true)
            MyPageCard(title = "Help", onClick = { /* TODO */ }, isCompact = true)
            MyPageCard(title = "Setting", onClick = { /* TODO */ }, isCompact = true)
            MyPageCard(title = "Logout", onClick = { /* TODO */ }, isCompact = true)
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(text = "Confirm logout", style = MaterialTheme.typography.titleLarge)
            },
            text = {
                Text(text = "Do you really want to logout?", style = MaterialTheme.typography.bodyMedium)
            },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.removeAccessToken() //删除token
                        GlobalScope.launch(Dispatchers.IO) {
                            EventBus.sendEvent(AuthEvent.TokenExpired)
                        }
                        showLogoutDialog = false
                    }
                ) {
                    Text("confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("cancel")
                }
            }
        )
    }
}


@Composable
fun ProfileHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_large)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "avatar",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "UserName", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Something describe", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun MyPageCard(title: String, onClick: () -> Unit, isCompact: Boolean = false) {
    val icon = when (title) {
        "Training Record" -> Icons.Default.LocationOn // R.drawable.ic_training
        "Help" -> Icons.Default.Build //R.drawable.ic_settings
        "Setting" -> Icons.Default.Settings //R.drawable.ic_personal_info
        "Logout" -> Icons.Default.Info //R.drawable.ic_help
        else -> Icons.Default.Done
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isCompact) 80.dp else dimensionResource(id = R.dimen.card_height))
            .clickable { onClick() },
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.card_elevation))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
            )
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_medium)))
            Text(text = title, fontSize = 20.sp)
        }
    }
}



