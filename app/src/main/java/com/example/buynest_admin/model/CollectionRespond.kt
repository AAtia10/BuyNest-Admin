package com.example.buynest_admin.model

data class CollectionsResponse(
    val smart_collections: List<CustomCollection>
)

data class CustomCollection(
    val id: Long,
    val title: String,
    val image: CollectionImage?
)

data class CollectionImage(
    val src: String
)
