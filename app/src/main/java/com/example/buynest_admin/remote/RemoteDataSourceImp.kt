package com.example.buynest_admin.remote

import com.example.buynest_admin.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteDataSourceImpl(
    private val service: ShopifyService
) : RemoteDataSource {
    override suspend fun getProducts(): Flow<List<Product>> = flow {
        val response = service.getProducts()
        if (response.isSuccessful) {
            emit(response.body()?.products ?: emptyList())
        } else {
            throw Exception("Failed: ${response.code()}")
        }
    }
}
