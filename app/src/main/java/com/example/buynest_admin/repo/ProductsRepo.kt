package com.example.buynest_admin.repo

import com.example.buynest_admin.model.Product
import com.example.buynest_admin.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val remoteDataSource: RemoteDataSource
) {
    suspend fun getProducts(): Flow<List<Product>> {
        return remoteDataSource.getProducts()
    }
}
