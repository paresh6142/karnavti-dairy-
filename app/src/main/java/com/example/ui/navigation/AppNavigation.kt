package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.BillBookViewModel
import com.example.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: BillBookViewModel = viewModel()
    val context = LocalContext.current
    
    val isLoggedIn by viewModel.isUserLoggedIn.collectAsState()
    val isFirebaseConfigured by viewModel.isFirebaseConfigured.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.initAuth(context)
    }

    if (!isFirebaseConfigured) {
        SetupScreen()
        return
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "home" else "login"
    ) {
        composable("login") {
            LoginScreen(viewModel = viewModel, navController = navController)
        }
        composable("home") {
            HomeScreen(viewModel = viewModel, navController = navController)
        }
        composable("customers") {
            CustomersScreen(viewModel = viewModel, navController = navController)
        }
        composable("baki_udhar") {
            BakiUdharScreen(viewModel = viewModel, navController = navController)
        }
        composable("products") {
            ProductsScreen(viewModel = viewModel, navController = navController)
        }
        composable("customer_profile/{customerId}") { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
            CustomerProfileScreen(customerId = customerId, viewModel = viewModel, navController = navController)
        }
        composable("add_udhar/{customerId}") { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
            AddUdharScreen(customerId = customerId, viewModel = viewModel, navController = navController)
        }
        composable("receive_payment/{customerId}") { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
            ReceivePaymentScreen(customerId = customerId, viewModel = viewModel, navController = navController)
        }
        composable("settings") {
            SettingsScreen(viewModel = viewModel, navController = navController)
        }
    }
}
