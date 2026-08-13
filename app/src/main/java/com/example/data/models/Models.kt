package com.example.data.models

data class Customer(
    val id: String = "",
    val name: String = "",
    val mobile: String = "",
    val photoUrl: String? = null,
    val balance: Double = 0.0,
    val createdAt: Long = 0L
)

data class Product(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String? = null,
    val isActive: Boolean = true
)

enum class TransactionType {
    PRODUCT_UDHAR, CASH_UDHAR, PAYMENT
}

enum class TransactionStatus {
    COMPLETED, CANCELLED
}

data class UdharTransaction(
    val id: String = "",
    val customerId: String = "",
    val type: String = TransactionType.PRODUCT_UDHAR.name,
    val amount: Double = 0.0,
    val timestamp: Long = 0L,
    val note: String? = null,
    val productId: String? = null,
    val productName: String? = null,
    val productQuantity: Int? = null,
    val status: String = TransactionStatus.COMPLETED.name
)
