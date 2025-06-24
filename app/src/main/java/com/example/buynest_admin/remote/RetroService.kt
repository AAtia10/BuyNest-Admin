package com.example.buynest_admin.remote

import com.example.buynest_admin.model.AddProductResponse
import com.example.buynest_admin.model.DiscountCodesResponse
import com.example.buynest_admin.model.InventoryLevelRequest
import com.example.buynest_admin.model.LocationResponse
import com.example.buynest_admin.model.NewProductPost
import com.example.buynest_admin.model.PriceRulesResponse
import com.example.buynest_admin.model.ProductsResponse
import com.example.buynest_admin.model.VariantRequest
import com.example.buynest_admin.model.VariantWrapper
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ShopifyService {
    @GET("admin/api/2024-04/products.json")
    suspend fun getProducts(): Response<ProductsResponse>

    @GET("admin/api/2024-04/products/{id}.json")
    suspend fun getProductById(
        @Path("id") productId: Long
    ): Response<ProductsResponse>


    @GET("admin/api/2024-04/price_rules.json")
    suspend fun getPriceRules(): Response<PriceRulesResponse>

    @GET("admin/api/2024-04/price_rules/{id}/discount_codes.json")
    suspend fun getDiscountCodes(
        @Path("id") priceRuleId: Long
    ): Response<DiscountCodesResponse>

    @POST("admin/api/2024-04/products/{product_id}/variants.json")
    suspend fun addVariant(
        @Path("product_id") productId: Long,
        @Body variantRequest: VariantRequest
    ): Response<VariantWrapper>

    @POST("admin/api/2024-04/inventory_levels/set.json")
    suspend fun setInventoryLevel(
        @Body request: InventoryLevelRequest
    ): Response<Unit>

    @GET("admin/api/2024-04/locations.json")
    suspend fun getLocations(): Response<LocationResponse>

    @PUT("admin/api/2024-04/variants/{variant_id}.json")
    suspend fun updateVariant(
        @Path("variant_id") variantId: Long,
        @Body variantRequest: VariantRequest
    ): Response<VariantWrapper>

    @POST("admin/api/2024-04/products.json")
    suspend fun addProduct(@Body product: NewProductPost): Response<AddProductResponse>

    @DELETE("admin/api/2024-04/products/{product_id}.json")
    suspend fun deleteProduct(
        @Path("product_id") productId: Long
    ): Response<Unit>














}
