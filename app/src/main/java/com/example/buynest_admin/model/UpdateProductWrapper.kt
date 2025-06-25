package com.example.buynest_admin.model

data class UpdateProductWrapper(
    val product: UpdateProductBody
)

data class UpdateProductBody(
    val id: Long,
    val title: String,
    val body_html: String
)
