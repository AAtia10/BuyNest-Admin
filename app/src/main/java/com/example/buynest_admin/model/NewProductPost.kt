package com.example.buynest_admin.model

import com.google.gson.annotations.SerializedName

data class NewProductPost(
    val product: ProductData
)
data class ImageData(
    val src: String
)

data class ProductOption(
    val name: String
)




data class ProductData(
    val title: String,
    val body_html: String,
    val vendor: String,
    val product_type: String,
    val variants: List<VariantPost>,
    val images: List<ImageData>? = null,
    @SerializedName("options") val options: List<ProductOption>? = null

)
