package com.example.buynest_admin.remote

import com.example.buynest_admin.model.Product
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    suspend fun getProducts(): Flow<List<Product>>
}
