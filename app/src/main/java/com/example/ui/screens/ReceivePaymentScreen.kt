package com.example.ui.screens

import androidx.compose.foundation.layout.*
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
fun ReceivePaymentScreen(customerId: String, viewModel: BillBookViewModel, navController: NavController) {
    val customers by viewModel.customers.collectAsState()
    val customer = customers.find { it.id == customerId }
    
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Receive Payment") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Current Balance: ₹${customer?.balance ?: 0.0}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Payment Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull()
                    if (amt != null && amt > 0 && customer != null) {
                        val tx = UdharTransaction(
                            customerId = customer.id,
                            type = TransactionType.PAYMENT.name,
                            amount = amt,
                            note = note
                        )
                        viewModel.addTransaction(tx, customer)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("SAVE PAYMENT")
            }
        }
    }
}
