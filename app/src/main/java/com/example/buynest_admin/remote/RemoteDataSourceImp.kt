package com.example.buynest_admin.remote

import android.util.Log
import com.example.buynest_admin.model.AddPriceRulePost
import com.example.buynest_admin.model.AddPriceRuleWrapper
import com.example.buynest_admin.model.CustomCollection
import com.example.buynest_admin.model.DiscountCode
import com.example.buynest_admin.model.DiscountCodePost
import com.example.buynest_admin.model.DiscountCodePostWrapper
import com.example.buynest_admin.model.InventoryLevelRequest
import com.example.buynest_admin.model.Location
import com.example.buynest_admin.model.NewProductPost
import com.example.buynest_admin.model.PriceRule
import com.example.buynest_admin.model.Product
import com.example.buynest_admin.model.UpdateProductBody
import com.example.buynest_admin.model.UpdateProductWrapper
import com.example.buynest_admin.model.Variant
import com.example.buynest_admin.model.VariantPost
import com.example.buynest_admin.model.VariantRequest
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

    override suspend fun getPriceRules(): Flow<List<PriceRule>> = flow {
        val response = service.getPriceRules()
        if (response.isSuccessful) {
            emit(response.body()?.price_rules ?: emptyList())
        } else {
            throw Exception("Error: ${response.code()}")
        }
    }

    override suspend fun getDiscountCodes(priceRuleId: Long): Flow<List<DiscountCode>> = flow {
        val response = service.getDiscountCodes(priceRuleId)
        if (response.isSuccessful) {
            emit(response.body()?.discount_codes ?: emptyList())
        } else {
            throw Exception("Failed to load discount codes")
        }
    }

    override suspend fun addVariant(productId: Long, variant: VariantPost): Flow<Variant> = flow {
        val response = service.addVariant(productId, VariantRequest(variant))
        if (response.isSuccessful) {
            emit(response.body()?.variant ?: error("No variant returned"))
        } else {
            throw Exception("Failed to post variant: ${response.code()}")
        }
    }

    override suspend fun getProductById(id: Long): Flow<Product> = flow {
        val response = service.getProductById(id)
        if (response.isSuccessful) {
            val product = response.body()?.products?.firstOrNull()
            if (product != null) {
                emit(product)
            } else {
                throw Exception("Product not found")
            }
        } else {
            throw Exception("Failed to fetch product: ${response.code()}")
        }
    }

    override suspend fun setInventoryLevel(
        inventoryItemId: Long,
        locationId: Long,
        available: Int
    ): Flow<Boolean> = flow {
        Log.d("SET_INVENTORY", "Sending => itemId=$inventoryItemId, location=$locationId, quantity=$available")
        val response = service.setInventoryLevel(
            InventoryLevelRequest(
                inventory_item_id = inventoryItemId,
                location_id = locationId,
                available = available
            )
        )
        Log.d("SET_INVENTORY", "Response => success=${response.isSuccessful}, code=${response.code()}")
        emit(response.isSuccessful)
    }

    override suspend fun getLocations(): Flow<List<Location>> = flow {
        val response = service.getLocations()
        if (response.isSuccessful) {
            emit(response.body()?.locations ?: emptyList())
        } else {
            throw Exception("Failed to fetch locations: ${response.code()}")
        }
    }

    override suspend fun updateVariant(
        variantId: Long,
        variant: VariantPost
    ): Flow<Variant> = flow {
        val response = service.updateVariant(variantId, VariantRequest(variant))
        if (response.isSuccessful) {
            emit(response.body()?.variant ?: error("No variant returned"))
        } else {
            throw Exception("Failed to update variant: ${response.code()}")
        }
    }

    override suspend fun addProduct(product: NewProductPost): Flow<Product> = flow {
        val response = service.addProduct(product)
        Log.e("ADD_PRODUCT_ERROR", "Code: ${response.code()}")

        if (!response.isSuccessful) {
            val error = response.errorBody()?.string()
            Log.e("ADD_PRODUCT_ERROR_BODY", error ?: "No error body")
        }

        if (response.isSuccessful) {
            val addedProduct = response.body()?.product
            if (addedProduct != null) {
                emit(addedProduct)
            } else {
                throw IllegalStateException("No product returned")
            }
        } else {
            throw Exception("Failed to add product: ${response.code()}")
        }
    }

    override suspend fun deleteProduct(productId: Long): Flow<Boolean> = flow {
        val response = service.deleteProduct(productId)
        emit(response.isSuccessful)
    }

    override suspend fun getCollections(): Flow<List<CustomCollection>> = flow {
        val response = service.getCollections()
        if (response.isSuccessful) {
            response.body()?.smart_collections?.let {
                emit(it)
            }
        } else {
            throw Exception("Failed to load smart collections")
        }
    }

    override suspend fun updateProduct(
        productId: Long,
        newTitle: String,
        newDesc: String
    ): Flow<Product> = flow {
        val body = UpdateProductWrapper(
            product = UpdateProductBody(
                id = productId,
                title = newTitle,
                body_html = newDesc
            )
        )

        Log.d("UpdateProduct", "Request body = $body")

        val response = service.updateProduct(productId, body)

        if (response.isSuccessful) {
            val updatedProduct = response.body()?.product
            if (updatedProduct != null) {
                emit(updatedProduct)
            } else {
                throw Exception("No product returned")
            }
        } else {
            throw Exception("Failed to update product: ${response.code()}")
        }
    }


    override suspend fun deleteVariant(productId: Long, variantId: Long): Flow<Boolean> = flow {
        val response = service.deleteVariant(productId, variantId)
        emit(response.isSuccessful)
    }


    override suspend fun connectInventoryLevel(
        inventoryItemId: Long,
        locationId: Long
    ): Flow<Boolean> = flow {
        val response = service.connectInventoryLevel(
            mapOf(
                "inventory_item_id" to inventoryItemId,
                "location_id" to locationId
            )
        )
        Log.d("CONNECT_RESPONSE", "status=${response.code()}, success=${response.isSuccessful}")
        emit(response.isSuccessful)
    }


    override suspend fun addPriceRule(priceRule: AddPriceRulePost): Flow<PriceRule> = flow {
        val response = service.addPriceRule(AddPriceRuleWrapper(priceRule))
        Log.d("API_RESPONSE", response.code().toString())
        Log.d("API_ERROR_BODY", response.errorBody()?.string() ?: "null")
        if (response.isSuccessful) {
            emit(response.body()?.price_rule ?: error("No rule returned"))
        } else {
            throw Exception("Failed to add price rule: ${response.code()}")
        }
    }

    override suspend fun deletePriceRule(id: Long): Flow<Unit> = flow {
        val response = service.deletePriceRule(id)
        Log.d("DELETE_RESPONSE", response.code().toString())
        if (response.isSuccessful) {
            emit(Unit)
        } else {
            throw Exception("Failed to delete price rule: ${response.code()}")
        }
    }

    override suspend fun addDiscountCode(priceRuleId: Long, code: String): Flow<Unit> = flow {
        val response = service.addDiscountCode(
            priceRuleId,
            DiscountCodePostWrapper(discount_code = DiscountCodePost(code))
        )

        if (response.isSuccessful) emit(Unit)
        else throw Exception("Failed to add code")
    }










}
