package com.example.buynest_admin.repo

import android.util.Log
import com.example.buynest_admin.model.AddPriceRulePost
import com.example.buynest_admin.model.Brand
import com.example.buynest_admin.model.CustomCollection
import com.example.buynest_admin.model.Location
import com.example.buynest_admin.model.NewProductPost
import com.example.buynest_admin.model.PriceRule
import com.example.buynest_admin.model.Product
import com.example.buynest_admin.model.Variant
import com.example.buynest_admin.model.VariantPost
import com.example.buynest_admin.model.getBrandLogo
import com.example.buynest_admin.remote.RemoteDataSource
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
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
        val productsFlow = remoteDataSource.getProducts()
        val collectionsFlow = remoteDataSource.getCollections()

        return combine(productsFlow, collectionsFlow) { products, collections ->
            val vendors = products.map { it.vendor }.distinct()

            vendors.mapNotNull { vendor ->
                val matchingCollection = collections.find {
                    normalize(it.title) == normalize(vendor)
                }

                Log.d("BrandDebug", "Vendors: ${vendors.joinToString()}")

                collections.forEach {
                    Log.d("BrandDebug", "Smart Collection: title=${it.title}, image=${it.image?.src}")
                }


                matchingCollection?.let {
                    Log.d("BrandMatch", "Vendor: $vendor matched with ${it.title}")


                    Brand(
                        name = vendor,
                        logoUrl = it.image?.src ?: ""
                    )
                }

            }
        }
    }

    fun normalize(text: String): String {
        return text.trim().lowercase().replace(" ", "")
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



    suspend fun updateProduct(productId: Long, newTitle: String, newDesc: String): Flow<Product> {
        return remoteDataSource.updateProduct(productId, newTitle, newDesc)
    }

    suspend fun deleteVariant(productId: Long, variantId: Long): Flow<Boolean> {
        return remoteDataSource.deleteVariant(productId, variantId)
    }

    suspend fun connectInventoryLevel(inventoryItemId: Long, locationId: Long): Flow<Boolean> {
        return remoteDataSource.connectInventoryLevel(inventoryItemId, locationId)
    }

     suspend fun addPriceRule(request: AddPriceRulePost): Flow<PriceRule> {
         Log.d("API_REQUEST", Gson().toJson(request))
        return remoteDataSource.addPriceRule(request)
    }

    suspend fun deletePriceRule(id: Long): Flow<Unit> {
        return remoteDataSource.deletePriceRule(id)
    }

    suspend fun addDiscountCode(priceRuleId: Long, code: String): Flow<Unit> = remoteDataSource.addDiscountCode(priceRuleId, code)

    suspend fun deleteDiscountCode(priceRuleId: Long, codeId: Long): Flow<Unit> {
        return remoteDataSource.deleteDiscountCode(priceRuleId, codeId)
    }














}
