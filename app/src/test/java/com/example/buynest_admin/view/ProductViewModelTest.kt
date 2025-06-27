package com.example.buynest_admin.view

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.buynest_admin.model.Image
import com.example.buynest_admin.model.Option
import com.example.buynest_admin.repo.ProductRepository
import com.example.buynest_admin.viewModels.ProductViewModel
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.runner.RunWith
import com.example.buynest_admin.model.*
import io.mockk.coEvery
import io.mockk.coVerify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertTrue





@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ProductViewModelTest {

    private lateinit var viewModel: ProductViewModel
    private val repository: ProductRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private val sampleImage = Image(
        admin_graphql_api_id = "gid://shopify/ProductImage/101",
        alt = "Sample Image",
        created_at = "2024-01-01T00:00:00Z",
        height = 500,
        id = 101L,
        position = 1,
        product_id = 1L,
        src = "https://example.com/image.jpg",
        updated_at = "2024-01-02T00:00:00Z",
        variant_ids = emptyList(),
        width = 500
    )

    private val sampleOption = Option(
        id = 1L,
        name = "Size",
        position = 1,
        product_id = 1L,
        values = listOf("S", "M", "L")
    )

    private val product1 = Product(
        admin_graphql_api_id = "gid://shopify/Product/1",
        body_html = "<p>Sample Product</p>",
        created_at = "2024-01-01T00:00:00Z",
        handle = "sample-product",
        id = 1L,
        image = sampleImage,
        images = listOf(sampleImage),
        options = listOf(sampleOption),
        product_type = "Shoes",
        published_at = "2024-01-01T00:00:00Z",
        published_scope = "web",
        status = "active",
        tags = "Sport",
        template_suffix = "",
        title = "Product 1",
        updated_at = "2024-06-01T00:00:00Z",
        variants = emptyList(),
        vendor = "Nike"
    )

    val createdVariant = Variant(
        admin_graphql_api_id = "gid://shopify/ProductVariant/123",
        barcode = "", // أو null لو نوعه Nullable
        compare_at_price = "", // أو null أو 0 حسب نوعه
        created_at = "2024-06-01T00:00:00Z",
        fulfillment_service = "manual",
        grams = 500,
        id = 123L,
        image_id = "",
        inventory_item_id = 456L,
        inventory_management = "shopify",
        inventory_policy = "deny",
        inventory_quantity = 10,
        old_inventory_quantity = 5,
        option1 = "M",
        option2 = "Red",
        option3 = "",
        position = 1,
        price = "100.0",
        product_id = 1L,
        requires_shipping = true,
        sku = "SKU123",
        taxable = true,
        title = "M / Red",
        updated_at = "2024-06-02T00:00:00Z",
        weight = 0.5,
        weight_unit = "kg"
    )

    private val product2 = product1.copy(id = 2L, title = "Product 2")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ProductViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchProducts should emit product list and set loading false`() = runTest {
        // Arrange
        coEvery { repository.getProducts() } returns flowOf(listOf(product1, product2))

        // Act
        viewModel.fetchProducts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(listOf(product1, product2), viewModel.products.value)
        assertEquals(false, viewModel.isLoading.value)
        coVerify { repository.getProducts() }
    }

    @Test
    fun `onSearch should filter products by title`() = runTest {
        // Arrange
        val allProducts = listOf(
            product1.copy(title = "Running Shoes"),
            product2.copy(title = "Basketball Shoes")
        )

        coEvery { repository.getProducts() } returns flowOf(allProducts)

        viewModel = ProductViewModel(repository)

        // Act
        viewModel.onSearch("running")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val expected = listOf(product1.copy(title = "Running Shoes"))
        assertEquals(expected.map { it.title }, viewModel.products.value.map { it.title })
    }

    @Test
    fun `deleteProduct should call onDone on success`() = runTest {
        // Arrange
        var onDoneCalled = false
        coEvery { repository.deleteProduct(1L) } returns flowOf(true)

        // Act
        viewModel.deleteProduct(1L) {
            onDoneCalled = true
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(true, onDoneCalled)
        coVerify { repository.deleteProduct(1L) }
    }


    @Test
    fun `addVariant should emit success result when inventory level is updated`() = runTest {
        // Arrange
        val productId = 1L
        val locationId = 999L
        val variantPost = VariantPost(
            option1 = "M",
            option2 = "Red",
            price = "100.0",
            inventory_quantity = 10
        )

        coEvery { repository.postVariant(productId, variantPost) } returns flowOf(createdVariant)
        coEvery {
            repository.setInventoryLevel(
                inventoryItemId = createdVariant.inventory_item_id,
                locationId = locationId,
                available = variantPost.inventory_quantity
            )
        } returns flowOf(true)
        coEvery { repository.getProductById(productId) } returns flowOf(product1)

        // Act
        viewModel.addVariant(productId, variantPost, locationId)

        val result = viewModel.newVariantResult.first()
        assertTrue(result.isSuccess)
        assertEquals(createdVariant, result.getOrNull())
    }





}
