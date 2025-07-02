package com.example.buynest_admin.model


data class PublicationsResponse(
    val publications: List<Publication>
)

data class Publication(
    val id: Long,
    val name: String
)
