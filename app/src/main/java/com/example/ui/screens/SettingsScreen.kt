package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.BillBookViewModel
import com.example.ui.components.BillBookBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: BillBookViewModel, navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("More / Settings") }) },
        bottomBar = { BillBookBottomBar(navController) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            ListItem(
                headlineContent = { Text("Shop Profile") },
                leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) }
            )
            Divider()
            ListItem(
                headlineContent = { Text("Logout") },
                leadingContent = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                modifier = Modifier.clickable {
                    viewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
