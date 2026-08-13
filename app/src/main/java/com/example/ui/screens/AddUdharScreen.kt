package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.BillBookViewModel
import com.example.data.models.UdharTransaction
import com.example.data.models.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUdharScreen(customerId: String, viewModel: BillBookViewModel, navController: NavController) {
    val customers by viewModel.customers.collectAsState()
    val products by viewModel.products.collectAsState()
    val customer = customers.find { it.id == customerId }
    
    var selectedTab by remember { mutableStateOf(0) } // 0: Product, 1: Cash
    
    var cashAmount by remember { mutableStateOf("") }
    var cashNote by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Udhar to ${customer?.name ?: ""}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Product Udhar") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Cash Udhar") })
            }
            
            if (selectedTab == 0) {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    if (products.isEmpty()) {
                        item {
                            Text("No products available. Add from Products screen.")
                        }
                    }
                    items(products) { product ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            onClick = {
                                if (customer != null) {
                                    val tx = UdharTransaction(
                                        customerId = customer.id,
                                        type = TransactionType.PRODUCT_UDHAR.name,
                                        amount = product.price,
                                        productId = product.id,
                                        productName = product.name,
                                        productQuantity = 1
                                    )
                                    viewModel.addTransaction(tx, customer)
                                    navController.popBackStack()
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(product.name, style = MaterialTheme.typography.titleMedium)
                                Text("₹${product.price}", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    OutlinedTextField(
                        value = cashAmount,
                        onValueChange = { cashAmount = it },
                        label = { Text("Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = cashNote,
                        onValueChange = { cashNote = it },
                        label = { Text("Note (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            val amount = cashAmount.toDoubleOrNull()
                            if (amount != null && amount > 0 && customer != null) {
                                val tx = UdharTransaction(
                                    customerId = customer.id,
                                    type = TransactionType.CASH_UDHAR.name,
                                    amount = amount,
                                    note = cashNote
                                )
                                viewModel.addTransaction(tx, customer)
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("SAVE UDHARI")
                    }
                }
            }
        }
    }
}
