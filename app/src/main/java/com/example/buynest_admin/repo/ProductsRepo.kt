package com.example.buynest_admin.repo

import com.example.buynest_admin.model.Brand
import com.example.buynest_admin.model.Location
import com.example.buynest_admin.model.NewProductPost
import com.example.buynest_admin.model.PriceRule
import com.example.buynest_admin.model.Product
import com.example.buynest_admin.model.Variant
import com.example.buynest_admin.model.VariantPost
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


    suspend fun getPriceRules(): Flow<List<PriceRule>> {
        return remoteDataSource.getPriceRules()
    }

    suspend fun getDiscountCodes(priceRuleId: Long) = remoteDataSource.getDiscountCodes(priceRuleId)


    suspend fun getBrands(): Flow<List<Brand>> {
        return remoteDataSource.getProducts()
            .map { productList ->
                productList.map { it.vendor }.distinct()
                    .map { vendor ->
                        Brand(name = vendor, logoRes = getBrandLogo(vendor))
                    }
            }
    }

    suspend fun postVariant(productId: Long, variant: VariantPost): Flow<Variant> =
        remoteDataSource.addVariant(productId, variant)

    suspend fun getProductById(id: Long): Flow<Product> {
        return remoteDataSource.getProductById(id)
    }

    suspend fun setInventoryLevel(inventoryItemId: Long, locationId: Long, available: Int): Flow<Boolean> {
        return remoteDataSource.setInventoryLevel(inventoryItemId, locationId, available)
    }

    suspend fun getLocations(): Flow<List<Location>> = remoteDataSource.getLocations()

     suspend fun updateVariant(variantId: Long, variant: VariantPost): Flow<Variant> {
        return remoteDataSource.updateVariant(variantId, variant)
    }

    suspend fun addProduct(product: NewProductPost): Flow<Product> {
        return remoteDataSource.addProduct(product)
    }

    suspend fun deleteProduct(productId: Long): Flow<Boolean> {
        return remoteDataSource.deleteProduct(productId)
    }





}
