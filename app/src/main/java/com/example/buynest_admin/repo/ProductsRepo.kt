package com.example.buynest_admin.repo

import com.example.buynest_admin.model.Brand
import com.example.buynest_admin.model.Product
import com.example.buynest_admin.model.getBrandLogo
import com.example.buynest_admin.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepository(
    private val remoteDataSource: RemoteDataSource
) {

    companion object {
        @Volatile
        private var INSTANCE: ProductRepository? = null

        fun getInstance(remoteDataSource: RemoteDataSource): ProductRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = ProductRepository(remoteDataSource)
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun getProducts(): Flow<List<Product>> {
        return remoteDataSource.getProducts()
    }

    suspend fun getBrands(): Flow<List<Brand>> {
        return remoteDataSource.getProducts()
            .map { productList ->
                productList.map { it.vendor }.distinct()
                    .map { vendor ->
                        Brand(name = vendor, logoRes = getBrandLogo(vendor))
                    }
            }
    }
}
