package com.example.buynest_admin.remote

import com.example.buynest_admin.model.ProductsResponse
import retrofit2.Response
import retrofit2.http.GET

interface ShopifyService {
    @GET("admin/api/2024-04/products.json")
    suspend fun getProducts(): Response<ProductsResponse>
}
