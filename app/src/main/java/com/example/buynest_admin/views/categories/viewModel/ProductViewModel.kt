package com.example.buynest_admin.views.categories.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buynest_admin.model.Product
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

    private val _searchQuery = MutableSharedFlow<String>(replay = 1)
    val searchQuery = _searchQuery.asSharedFlow()

    init {
        fetchProducts()
        handleSearch()
    }


    private fun fetchProducts() {
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

    private fun handleSearch() {
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
}
