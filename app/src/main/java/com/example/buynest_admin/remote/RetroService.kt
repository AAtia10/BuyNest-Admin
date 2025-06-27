package com.example.buynest_admin.remote

import com.example.buynest_admin.model.AddPriceRuleResponse
import com.example.buynest_admin.model.AddPriceRuleWrapper
import com.example.buynest_admin.model.AddProductResponse
import com.example.buynest_admin.model.CollectionsResponse
import com.example.buynest_admin.model.DiscountCodePostWrapper
import com.example.buynest_admin.model.DiscountCodesResponse
import com.example.buynest_admin.model.InventoryLevelRequest
import com.example.buynest_admin.model.LocationResponse
import com.example.buynest_admin.model.NewProductPost
import com.example.buynest_admin.model.PriceRulesResponse
import com.example.buynest_admin.model.ProductsResponse
import com.example.buynest_admin.model.SingleProductResponse
import com.example.buynest_admin.model.UpdateProductWrapper
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
    ): Response<SingleProductResponse>




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

    @GET("admin/api/2024-04/smart_collections.json")
    suspend fun getCollections(): Response<CollectionsResponse>


    @PUT("admin/api/2024-04/products/{product_id}.json")
    suspend fun updateProduct(
        @Path("product_id") productId: Long,
        @Body product: UpdateProductWrapper
    ): Response<AddProductResponse>

    @DELETE("admin/api/2024-04/products/{product_id}/variants/{variant_id}.json")
    suspend fun deleteVariant(
        @Path("product_id") productId: Long,
        @Path("variant_id") variantId: Long
    ): Response<Unit>


    @POST("inventory_levels/connect.json")
    suspend fun connectInventoryLevel(
        @Body body: Map<String, Long>
    ): Response<Unit>

    @POST("admin/api/2024-04/price_rules.json")
    suspend fun addPriceRule(
        @Body priceRule: AddPriceRuleWrapper
    ): Response<AddPriceRuleResponse>

    @DELETE("admin/api/2024-04/price_rules/{id}.json")
    suspend fun deletePriceRule(
        @Path("id") id: Long
    ): Response<Unit>

    @POST("admin/api/2024-04/price_rules/{price_rule_id}/discount_codes.json")
    suspend fun addDiscountCode(
        @Path("price_rule_id") priceRuleId: Long,
        @Body body: DiscountCodePostWrapper
    ): Response<DiscountCodesResponse>

    @DELETE("admin/api/2024-04/price_rules/{price_rule_id}/discount_codes/{discount_code_id}.json")
    suspend fun deleteDiscountCode(
        @Path("price_rule_id") priceRuleId: Long,
        @Path("discount_code_id") discountCodeId: Long
    ): Response<Unit>



























}
