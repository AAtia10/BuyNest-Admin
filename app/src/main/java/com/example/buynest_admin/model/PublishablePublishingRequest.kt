package com.example.buynest_admin.model

data class PublishablePublishingRequest(
    val publishable_publishing: PublishablePublishingData
)

data class PublishablePublishingData(
    val product_id: Long,
    val published_scope: String
)
