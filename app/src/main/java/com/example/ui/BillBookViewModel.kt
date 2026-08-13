package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AuthManager
import com.example.data.FirestoreManager
import com.example.data.models.Customer
import com.example.data.models.Product
import com.example.data.models.UdharTransaction
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillBookViewModel : ViewModel() {

    private var authManager: AuthManager? = null
    private val firestoreManager by lazy { FirestoreManager() }

    private val _isFirebaseConfigured = MutableStateFlow(true)
    val isFirebaseConfigured = _isFirebaseConfigured.asStateFlow()

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn = _isUserLoggedIn.asStateFlow()

    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers = _customers.asStateFlow()

    private val _bakiCustomers = MutableStateFlow<List<Customer>>(emptyList())
    val bakiCustomers = _bakiCustomers.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _currentCustomerTransactions = MutableStateFlow<List<UdharTransaction>>(emptyList())
    val currentCustomerTransactions = _currentCustomerTransactions.asStateFlow()
    
    private val _totalUdhar = MutableStateFlow(0.0)
    val totalUdhar = _totalUdhar.asStateFlow()

    fun initAuth(context: Context) {
        try {
            FirebaseApp.getInstance()
            authManager = AuthManager(context)
            _isUserLoggedIn.value = authManager?.currentUser != null
            if (_isUserLoggedIn.value) {
                refreshData()
            }
        } catch (e: IllegalStateException) {
            _isFirebaseConfigured.value = false
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            val success = authManager?.signInWithGoogle() == true
            if (success) {
                _isUserLoggedIn.value = true
                refreshData()
            }
        }
    }
    
    fun signOut() {
        authManager?.signOut()
        _isUserLoggedIn.value = false
        _customers.value = emptyList()
        _bakiCustomers.value = emptyList()
        _products.value = emptyList()
        _currentCustomerTransactions.value = emptyList()
        _totalUdhar.value = 0.0
    }

    fun refreshData() {
        viewModelScope.launch {
            val allCustomers = firestoreManager.getCustomers()
            _customers.value = allCustomers
            
            val baki = allCustomers.filter { it.balance > 0.0 }.sortedByDescending { it.balance }
            _bakiCustomers.value = baki
            
            _totalUdhar.value = allCustomers.sumOf { it.balance }

            _products.value = firestoreManager.getProducts()
        }
    }

    fun saveCustomer(customer: Customer) {
        viewModelScope.launch {
            firestoreManager.saveCustomer(customer)
            refreshData()
        }
    }

    fun saveProduct(product: Product) {
        viewModelScope.launch {
            firestoreManager.saveProduct(product)
            refreshData()
        }
    }

    fun loadTransactionsForCustomer(customerId: String) {
        viewModelScope.launch {
            _currentCustomerTransactions.value = firestoreManager.getTransactionsForCustomer(customerId)
        }
    }

    fun addTransaction(transaction: UdharTransaction, customer: Customer) {
        viewModelScope.launch {
            firestoreManager.saveTransaction(transaction, customer)
            refreshData()
            loadTransactionsForCustomer(customer.id)
        }
    }
}
