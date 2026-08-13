package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.BillBookViewModel

@Composable
fun LoginScreen(viewModel: BillBookViewModel, navController: NavController) {
    val isLoggedIn by viewModel.isUserLoggedIn.collectAsState()
    
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }
    
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Karnavati BillBook",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Digital Udhari Khata",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = { viewModel.signInWithGoogle() },
                modifier = Modifier.fillMaxWidth().height(56.dp).testTag("login_button")
            ) {
                Text("Sign in with Google")
            }
        }
    }
}
