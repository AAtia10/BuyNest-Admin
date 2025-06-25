package com.example.buynest_admin.views.allProducts.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buynest_admin.model.Brand
import com.example.buynest_admin.model.CustomCollection
import com.example.buynest_admin.model.Location
import com.example.buynest_admin.model.NewProductPost
import com.example.buynest_admin.model.Product
import com.example.buynest_admin.model.Variant
import com.example.buynest_admin.model.VariantPost
import com.example.buynest_admin.repo.ProductRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _brands = MutableStateFlow<List<Brand>>(emptyList())
    val brands: StateFlow<List<Brand>> = _brands

    private val _collections = MutableStateFlow<List<CustomCollection>>(emptyList())
    val collections: StateFlow<List<CustomCollection>> = _collections


    private val _searchQuery = MutableSharedFlow<String>(replay = 1)
    val searchQuery = _searchQuery.asSharedFlow()

    private val _selectedBrand = MutableStateFlow<String?>(null)
    val selectedBrand: StateFlow<String?> = _selectedBrand

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct

    private val _newVariantResult = MutableSharedFlow<Result<Variant>>()
    val newVariantResult = _newVariantResult.asSharedFlow()

    private val _locations = MutableStateFlow<List<Location>>(emptyList())
    val locations: StateFlow<List<Location>> = _locations

    fun setSelectedProduct(product: Product) {
        _selectedProduct.value = product
    }


    init {
        fetchProducts()
        handleSearch()
    }


     fun fetchProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getProducts().collect {
                    _products.value = it
                    _searchQuery.emit("")
                }
            } catch (e: Exception) {

            } finally {
                _isLoading.value = false
            }
        }
    }

     fun handleSearch() {
        viewModelScope.launch {
            searchQuery.collect { query ->
                repository.getProducts().collect { fullList ->
                    val filtered = if (query.isBlank()) {
                        fullList
                    } else {
                        fullList.filter {
                            it.title.contains(query, ignoreCase = true)
                        }
                    }
                    _products.value = filtered
                }
            }
        }
    }

    fun onSearch(query: String) {
        viewModelScope.launch {
            _searchQuery.emit(query)
        }
    }

    fun fetchBrands() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getBrands().collect {
                    _brands.value = it
                }
            } catch (_: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun setSelectedBrand(brand: String) {
        _selectedBrand.value = brand
    }


    fun addVariant(productId: Long, variant: VariantPost, locationId: Long) {
        viewModelScope.launch {
            try {
                repository.postVariant(productId, variant).collect { createdVariant ->
                    val inventoryItemId = createdVariant.inventory_item_id
                    val quantity = variant.inventory_quantity

                    repository.setInventoryLevel(
                        inventoryItemId = inventoryItemId,
                        locationId = locationId,
                        available = quantity
                    ).collect { success ->
                        if (success) {
                            _newVariantResult.emit(Result.success(createdVariant))
                            fetchProductById(productId)
                        } else {
                            _newVariantResult.emit(Result.failure(Exception("Inventory update failed")))
                        }
                    }
                }
            } catch (e: Exception) {
                _newVariantResult.emit(Result.failure(e))
            }
        }
    }




    fun fetchProductById(productId: Long) {
        viewModelScope.launch {
            try {
                repository.getProductById(productId).collect {
                    _selectedProduct.value = it
                }
            } catch (e: Exception) {

            }
        }
    }

    fun fetchLocations() {
        viewModelScope.launch {
            try {
                repository.getLocations().collect {
                    _locations.value = it
                }
            } catch (_: Exception) {
            }
        }
    }

    fun updateVariant(variantId: Long, variant: VariantPost) {
        viewModelScope.launch {
            try {
                repository.updateVariant(variantId, variant).collect { updatedVariant ->
                    fetchProductById(selectedProduct.value!!.id)

                    val location = locations.value.firstOrNull()
                    if (location != null) {
                        repository.setInventoryLevel(
                            inventoryItemId = updatedVariant.inventory_item_id,
                            locationId = location.id,
                            available = variant.inventory_quantity
                        ).collect { success ->
                            if (success) {
                                _newVariantResult.emit(Result.success(updatedVariant))
                            } else {
                                _newVariantResult.emit(Result.failure(Exception("Inventory update failed")))
                            }
                        }
                    } else {
                        _newVariantResult.emit(Result.failure(Exception("No location selected")))
                    }
                }
            } catch (e: Exception) {
                _newVariantResult.emit(Result.failure(e))
            }
        }
    }


    fun addProductWithInventory(
        product: NewProductPost,
        locationId: Long,
        variantInfo: VariantPost,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.addProduct(product).collect { addedProduct ->
                    val newVariant = addedProduct.variants.firstOrNull()
                    if (newVariant != null) {
                        repository.setInventoryLevel(
                            inventoryItemId = newVariant.inventory_item_id,
                            locationId = locationId,
                            available = variantInfo.inventory_quantity
                        ).collect {
                            fetchProducts()
                            onSuccess()
                            Log.d("DEBUG_RESPONSE", addedProduct.toString())

                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error adding product", e)
            }
        }
    }

    fun deleteProduct(productId: Long, onDone:suspend  () -> Unit) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(productId).collect { success ->
                    if (success) {
                        onDone()
                    }
                }
            } catch (e: Exception) {
                Log.e("DELETE_ERROR", "Failed to delete: ${e.message}")
            }
        }
    }

    fun clearSelectedProduct() {
        _selectedProduct.value = null
    }

    fun updateProductTitleAndDescription(productId: Long, newTitle: String, newDesc: String) {
        viewModelScope.launch {
            Log.d("UpdateProduct", "Title: $newTitle, Desc: $newDesc")

            try {
                repository.updateProduct(productId, newTitle, newDesc).collect {
                    fetchProductById(productId)
                }
            } catch (e: Exception) {
                Log.e("UpdateProduct", "Failed to update product", e)
            }
        }
    }



}

class ProductViewModelFactory(
    private val repository: ProductRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProductViewModel(repository) as T
    }
}
