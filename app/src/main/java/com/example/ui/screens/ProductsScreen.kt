package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.BillBookViewModel
import com.example.ui.components.BillBookBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(viewModel: BillBookViewModel, navController: NavController) {
    val products by viewModel.products.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Products") }) },
        bottomBar = { BillBookBottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO Add Product Dialog */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(products) { product ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.padding(12.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, style = MaterialTheme.typography.titleMedium)
                        }
                        Text("₹${product.price}", style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}
