package com.example.buynest_admin.model

data class DiscountCodesResponse(
    val discount_codes: List<DiscountCode>
)

data class DiscountCode(
    val id: Long,
    val code: String,
    val usage_count: Int,
    val price_rule_id: Long
)
