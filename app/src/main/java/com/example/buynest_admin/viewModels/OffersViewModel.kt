package com.example.buynest_admin.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buynest_admin.model.DiscountCode
import com.example.buynest_admin.model.PriceRule
import com.example.buynest_admin.repo.ProductRepository
import com.example.buynest_admin.views.allProducts.viewModel.ProductViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OffersViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _priceRules = MutableStateFlow<List<PriceRule>>(emptyList())
    val priceRules: StateFlow<List<PriceRule>> = _priceRules

    private val _discountMap = MutableStateFlow<Map<Long, DiscountCode>>(emptyMap())
    val discountMap: StateFlow<Map<Long, DiscountCode>> = _discountMap

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchPriceRules() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getPriceRules().collect {
                    _priceRules.value = it
                    fetchDiscountCodesForPriceRules(it)
                }
            } catch (_: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun fetchDiscountCodesForPriceRules(rules: List<PriceRule>) {
        viewModelScope.launch {
            val map = mutableMapOf<Long, DiscountCode>()
            for (rule in rules) {
                try {
                    repository.getDiscountCodes(rule.id).collect { codes ->
                        if (codes.isNotEmpty()) {
                            map[rule.id] = codes.first()
                        }
                    }
                } catch (_: Exception) {}
            }
            _discountMap.value = map
        }
    }
}

class OffersViewModelFactory(
    private val repository: ProductRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OffersViewModel(repository) as T
    }
}
