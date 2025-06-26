package com.example.buynest_admin.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buynest_admin.model.AddPriceRulePost
import com.example.buynest_admin.model.DiscountCode
import com.example.buynest_admin.model.PriceRule
import com.example.buynest_admin.repo.ProductRepository
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

                }
            } catch (_: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }





    fun addPriceRule(newRule: AddPriceRulePost, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.addPriceRule(newRule).collect {
                    fetchPriceRules()
                    onComplete()
                }
            } catch (e: Exception) {
                Log.e("AddPriceRule", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePriceRule(id: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deletePriceRule(id).collect {
                    fetchPriceRules() // Refresh after delete
                    onSuccess()
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchSingleDiscountCode(
        ruleId: Long,
        onResult: (DiscountCode?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.getDiscountCodes(ruleId).collect { codes ->
                    onResult(codes.firstOrNull())
                }
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }

    fun addDiscountCode(priceRuleId: Long, code: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.addDiscountCode(priceRuleId, code).collect {
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("AddDiscountCode", "Error: ${e.message}")
            }
        }
    }


    fun deleteDiscountCode(priceRuleId: Long, codeId: Long, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("DELETE", "Trying to delete code $codeId from price rule $priceRuleId")
                repository.deleteDiscountCode(priceRuleId, codeId).collect {
                    Log.d("DELETE", "Deleted successfully from API")
                }

            } catch (e: Exception) {
                Log.e("DELETE_ERROR", e.message ?: "Unknown error")
            }
            onComplete()
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
