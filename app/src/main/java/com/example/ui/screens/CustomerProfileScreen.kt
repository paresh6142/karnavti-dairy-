package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
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
import java.text.NumberFormat
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerProfileScreen(customerId: String, viewModel: BillBookViewModel, navController: NavController) {
    val customers by viewModel.customers.collectAsState()
    val transactions by viewModel.currentCustomerTransactions.collectAsState()
    val customer = customers.find { it.id == customerId }
    
    LaunchedEffect(customerId) {
        viewModel.loadTransactionsForCustomer(customerId)
    }

    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    format.maximumFractionDigits = 0
    val dateFormat = SimpleDateFormat("dd MMM • hh:mm a", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(customer?.name ?: "Customer Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (customer == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Customer not found")
            }
            return@Scaffold
        }

        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (customer.photoUrl.isNullOrEmpty()) {
                        Surface(
                            modifier = Modifier.size(100.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(24.dp))
                        }
                    } else {
                        AsyncImage(
                            model = customer.photoUrl,
                            contentDescription = "Profile Photo",
                            modifier = Modifier.size(100.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = customer.name, style = MaterialTheme.typography.headlineMedium)
                    Text(text = customer.mobile, style = MaterialTheme.typography.bodyLarge)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Current Udhar", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = format.format(customer.balance), 
                                style = MaterialTheme.typography.displaySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = { navController.navigate("add_udhar/${customer.id}") }) {
                            Text("ADD UDHAR")
                        }
                        Button(onClick = { navController.navigate("receive_payment/${customer.id}") },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                            Text("RECEIVE PAYMENT")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        OutlinedButton(onClick = { /* TODO */ }) {
                            Text("SEND MESSAGE")
                        }
                        OutlinedButton(onClick = { /* TODO */ }) {
                            Text("EDIT PROFILE")
                        }
                    }
                }
                
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                
                Text(
                    text = "Transaction History", 
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            items(transactions) { tx ->
                ListItem(
                    headlineContent = { 
                        Text(
                            when(tx.type) {
                                "PRODUCT_UDHAR" -> "${tx.productName ?: "Product"} Udhar"
                                "CASH_UDHAR" -> "Cash Udhar"
                                "PAYMENT" -> "Payment Received"
                                else -> "Transaction"
                            }
                        ) 
                    },
                    supportingContent = { 
                        Column {
                            if (!tx.note.isNullOrEmpty()) {
                                Text(tx.note)
                            }
                            Text(dateFormat.format(Date(tx.timestamp)))
                        }
                    },
                    trailingContent = { 
                        val sign = if (tx.type == "PAYMENT") "-" else "+"
                        val color = if (tx.type == "PAYMENT") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        Text(
                            text = "$sign${format.format(tx.amount)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = color
                        )
                    }
                )
                Divider()
            }
        }
    }
}
