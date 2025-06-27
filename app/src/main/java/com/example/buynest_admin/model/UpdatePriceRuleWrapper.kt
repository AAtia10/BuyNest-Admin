package com.example.buynest_admin.model

data class UpdatePriceRuleWrapper(
    val price_rule: UpdatePriceRuleBody
)

data class UpdatePriceRuleBody(
    val value: String,
    val ends_at: String?
)
