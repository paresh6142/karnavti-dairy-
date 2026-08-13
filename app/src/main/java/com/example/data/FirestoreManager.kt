package com.example.data

import com.example.data.models.Customer
import com.example.data.models.Product
import com.example.data.models.UdharTransaction
import com.example.data.models.TransactionStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirestoreManager {
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    private val uid get() = auth.currentUser?.uid

    // Collections
    private val usersCol get() = db.collection("users")
    private val shopCol get() = uid?.let { usersCol.document(it) }
    private val customersCol get() = shopCol?.collection("customers")
    private val productsCol get() = shopCol?.collection("products")
    private val transactionsCol get() = shopCol?.collection("transactions")

    suspend fun getCustomers(): List<Customer> {
        val col = customersCol ?: return emptyList()
        val snapshot = col.get().await()
        return snapshot.toObjects(Customer::class.java).sortedBy { it.name }
    }

    suspend fun getCustomer(id: String): Customer? {
        val col = customersCol ?: return null
        val doc = col.document(id).get().await()
        return doc.toObject(Customer::class.java)
    }

    suspend fun saveCustomer(customer: Customer) {
        val col = customersCol ?: return
        val customerToSave = if (customer.id.isEmpty()) {
            customer.copy(id = UUID.randomUUID().toString(), createdAt = System.currentTimeMillis())
        } else {
            customer
        }
        col.document(customerToSave.id).set(customerToSave).await()
    }

    suspend fun getBakiUdharCustomers(): List<Customer> {
        val col = customersCol ?: return emptyList()
        val snapshot = col.whereGreaterThan("balance", 0.0).get().await()
        return snapshot.toObjects(Customer::class.java).sortedByDescending { it.balance }
    }

    suspend fun getProducts(): List<Product> {
        val col = productsCol ?: return emptyList()
        val snapshot = col.get().await()
        return snapshot.toObjects(Product::class.java).sortedBy { it.name }
    }

    suspend fun saveProduct(product: Product) {
        val col = productsCol ?: return
        val productToSave = if (product.id.isEmpty()) {
            product.copy(id = UUID.randomUUID().toString())
        } else {
            product
        }
        col.document(productToSave.id).set(productToSave).await()
    }

    suspend fun getTransactionsForCustomer(customerId: String): List<UdharTransaction> {
        val col = transactionsCol ?: return emptyList()
        val snapshot = col.whereEqualTo("customerId", customerId)
            .get().await()
        return snapshot.toObjects(UdharTransaction::class.java).sortedByDescending { it.timestamp }
    }

    suspend fun saveTransaction(transaction: UdharTransaction, customer: Customer) {
        val col = transactionsCol ?: return
        val cCol = customersCol ?: return
        
        val newTx = if (transaction.id.isEmpty()) {
            transaction.copy(id = UUID.randomUUID().toString(), timestamp = System.currentTimeMillis())
        } else {
            transaction
        }
        
        // Transaction run for atomic update
        db.runTransaction { tx ->
            val customerRef = cCol.document(customer.id)
            val customerSnap = tx.get(customerRef)
            val currentBalance = customerSnap.getDouble("balance") ?: 0.0
            
            // Calculate new balance
            var newBalance = currentBalance
            if (newTx.status == TransactionStatus.COMPLETED.name) {
                 when (newTx.type) {
                     "PRODUCT_UDHAR", "CASH_UDHAR" -> newBalance += newTx.amount
                     "PAYMENT" -> newBalance -= newTx.amount
                 }
            }
            
            val txRef = col.document(newTx.id)
            tx.set(txRef, newTx)
            tx.update(customerRef, "balance", newBalance)
            null
        }.await()
    }
}
