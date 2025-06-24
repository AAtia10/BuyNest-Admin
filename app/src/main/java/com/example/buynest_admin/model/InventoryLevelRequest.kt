package com.example.buynest_admin.model

data class InventoryLevelRequest(
    val location_id: Long,
    val inventory_item_id: Long,
    val available: Int
)
