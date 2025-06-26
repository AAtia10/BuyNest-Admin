package com.example.buynest_admin.model

data class DiscountCodePost(
    val code: String
)
data class DiscountCodePostWrapper(
    val discount_code: DiscountCodePost
)
