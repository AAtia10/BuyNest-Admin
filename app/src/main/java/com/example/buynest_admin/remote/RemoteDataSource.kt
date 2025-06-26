package com.example.buynest_admin.remote

import com.example.buynest_admin.model.AddPriceRulePost
import com.example.buynest_admin.model.CustomCollection
import com.example.buynest_admin.model.DiscountCode
import com.example.buynest_admin.model.Location
import com.example.buynest_admin.model.NewProductPost
import com.example.buynest_admin.model.PriceRule
import com.example.buynest_admin.model.Product
import com.example.buynest_admin.model.Variant
import com.example.buynest_admin.model.VariantPost
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    suspend fun getProducts(): Flow<List<Product>>
    suspend fun getPriceRules(): Flow<List<PriceRule>>
    suspend fun getDiscountCodes(priceRuleId: Long): Flow<List<DiscountCode>>
    suspend fun addVariant(productId: Long, variant: VariantPost): Flow<Variant>
    suspend fun getProductById(id: Long): Flow<Product>
    suspend fun setInventoryLevel(
        inventoryItemId: Long,
        locationId: Long,
        available: Int
    ): Flow<Boolean>

    suspend fun getLocations(): Flow<List<Location>>
    suspend fun updateVariant(
        variantId: Long,
        variant: VariantPost
    ): Flow<Variant>

    suspend fun addProduct(product: NewProductPost): Flow<Product>
    suspend fun deleteProduct(productId: Long): Flow<Boolean>
    suspend fun getCollections(): Flow<List<CustomCollection>>

    suspend fun updateProduct(
        productId: Long,
        newTitle: String,
        newDesc: String
    ): Flow<Product>

    suspend fun deleteVariant(productId: Long, variantId: Long): Flow<Boolean>

    suspend fun connectInventoryLevel(
        inventoryItemId: Long,
        locationId: Long
    ): Flow<Boolean>

    suspend fun addPriceRule(priceRule: AddPriceRulePost): Flow<PriceRule>



}
