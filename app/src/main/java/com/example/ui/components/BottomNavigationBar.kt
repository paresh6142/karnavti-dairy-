package com.example.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BillBookBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.height(72.dp)
    ) {
        val colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
        val textStyle = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp)

        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", style = textStyle) },
            selected = currentRoute == "home",
            colors = colors,
            onClick = {
                if (currentRoute != "home") {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.People, contentDescription = "Customers") },
            label = { Text("Customers", style = textStyle) },
            selected = currentRoute == "customers",
            colors = colors,
            onClick = {
                if (currentRoute != "customers") {
                    navController.navigate("customers") {
                        popUpTo("home")
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Baki") },
            label = { Text("Baki", style = textStyle) },
            selected = currentRoute == "baki_udhar",
            colors = colors,
            onClick = {
                if (currentRoute != "baki_udhar") {
                    navController.navigate("baki_udhar") {
                        popUpTo("home")
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Products") },
            label = { Text("Products", style = textStyle) },
            selected = currentRoute == "products",
            colors = colors,
            onClick = {
                if (currentRoute != "products") {
                    navController.navigate("products") {
                        popUpTo("home")
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "More") },
            label = { Text("More", style = textStyle) },
            selected = currentRoute == "settings",
            colors = colors,
            onClick = {
                if (currentRoute != "settings") {
                    navController.navigate("settings") {
                        popUpTo("home")
                    }
                }
            }
        )
    }
}
