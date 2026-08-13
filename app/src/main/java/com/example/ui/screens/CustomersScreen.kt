package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ui.BillBookViewModel
import com.example.ui.components.BillBookBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(viewModel: BillBookViewModel, navController: NavController) {
    val customers by viewModel.customers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filtered = if (searchQuery.isBlank()) {
        customers
    } else {
        customers.filter { 
            it.name.contains(searchQuery, ignoreCase = true) || it.mobile.contains(searchQuery) 
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Customers") }) },
        bottomBar = { BillBookBottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO Add Customer */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Customer")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Search by name / mobile") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(filtered) { customer ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { navController.navigate("customer_profile/${customer.id}") },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (customer.photoUrl.isNullOrEmpty()) {
                                Surface(
                                    modifier = Modifier.size(64.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(16.dp))
                                }
                            } else {
                                AsyncImage(
                                    model = customer.photoUrl,
                                    contentDescription = "Profile Photo",
                                    modifier = Modifier.size(64.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = customer.name, 
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
