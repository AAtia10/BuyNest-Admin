package com.example.buynest_admin.model

data class AddPriceRuleWrapper(
    val price_rule: AddPriceRulePost
)

data class AddPriceRulePost(
    val title: String,
    val target_type: String = "line_item",
    val target_selection: String = "all",
    val allocation_method: String = "across",
    val value_type: String, // "percentage" or "fixed_amount"
    val value: String, // "-10.0" or "-50.0"
    val customer_selection: String = "all",
    val starts_at: String,
    val ends_at: String,
    val usage_limit: Int?=null,
    val once_per_customer: Boolean = false,
    val prerequisite_subtotal_range: PrerequisiteSubtotalRange? = null,
)

data class PrerequisiteSubtotalRange(
    val greater_than_or_equal_to: Double
)
