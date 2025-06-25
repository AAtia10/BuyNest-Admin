package com.example.buynest_admin.model

data class VariantRequest(
    val variant: VariantPost
)

data class VariantPost(
    val option1: String?,
    val option2: String?,
    val price: String,
    val inventory_quantity: Int,
    val inventory_management: String = "shopify",
    val sku: String = "sku-${System.currentTimeMillis()}",
    val requires_shipping: Boolean = true,
    val taxable: Boolean = true
)