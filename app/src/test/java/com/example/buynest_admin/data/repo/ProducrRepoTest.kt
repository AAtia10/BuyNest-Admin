package com.example.buynest_admin.data.repo


import com.example.buynest_admin.model.*
import com.example.buynest_admin.remote.RemoteDataSource
import com.example.buynest_admin.repo.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.hamcrest.CoreMatchers.`is`

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

class ProductRepositoryTest {

    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var repository: ProductRepository

    val sampleImage = Image(
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

    val sampleOption = Option(
        id = 1L,
        name = "Size",
        position = 1,
        product_id = 1L,
        values = listOf("S", "M", "L")
    )



    val sampleProduct = Product(
        admin_graphql_api_id = "gid://shopify/Product/1",
        body_html = "<p>Sample Product Description</p>",
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
        tags = "Sport, Running",
        template_suffix = "",
        title = "Sample Product",
        updated_at = "2024-06-01T00:00:00Z",
        variants = emptyList(),
        vendor = "Nike"
    )


    private val sampleRule = PriceRule(
        admin_graphql_api_id = "",
        allocation_limit = 0,
        allocation_method = "across",
        created_at = "2024-06-01T00:00:00Z",
        customer_segment_prerequisite_ids = emptyList(),
        customer_selection = "all",
        ends_at = "2024-06-30T00:00:00Z",
        entitled_collection_ids = emptyList(),
        entitled_country_ids = emptyList(),
        entitled_product_ids = emptyList(),
        entitled_variant_ids = emptyList(),
        id = 1,
        once_per_customer = false,
        prerequisite_collection_ids = emptyList(),
        prerequisite_customer_ids = emptyList(),
        prerequisite_product_ids = emptyList(),
        prerequisite_quantity_range = Any(), // أو dummy object مناسب لو عندك class له
        prerequisite_shipping_price_range = Any(),
        prerequisite_subtotal_range = Any(),
        prerequisite_to_entitlement_purchase = PrerequisiteToEntitlementPurchase(0), // عدّل على حسب الكلاس
        prerequisite_to_entitlement_quantity_ratio = PrerequisiteToEntitlementQuantityRatio(0, 0), // برضو عدلها
        prerequisite_variant_ids = emptyList(),
        starts_at = "2024-06-01T00:00:00Z",
        target_selection = "all",
        target_type = "line_item",
        title = "Summer Sale",
        updated_at = "2024-06-01T00:00:00Z",
        usage_limit = 100,
        value = "-10",
        value_type = "percentage"
    )


    @Before
    fun setup() {
        remoteDataSource = mockk()
        repository = ProductRepository(remoteDataSource)
    }

    @Test
    fun `getProducts should return list of products`() = runTest {
        coEvery { remoteDataSource.getProducts() } returns flowOf(listOf(sampleProduct))

        val result = repository.getProducts().firstOrNull()

        assertNotNull(result)
        assertThat(result!!.size, `is`(1))
        assertThat(result[0].title, `is`("Sample Product"))

        coVerify { remoteDataSource.getProducts() }
    }

    @Test
    fun `getPriceRules should return list of rules`() = runTest {
        coEvery { remoteDataSource.getPriceRules() } returns flowOf(listOf(sampleRule))

        val result = repository.getPriceRules().firstOrNull()

        assertNotNull(result)
        assertThat(result!!.size, `is`(1))
        assertThat(result[0].title, `is`("Summer Sale"))

        coVerify { remoteDataSource.getPriceRules() }
    }

    @Test
    fun `updatePriceRule should call remoteDataSource and return true`() = runTest {
        coEvery { remoteDataSource.updatePriceRule(1, "-20", "2024-07-01T00:00:00Z") } returns true

        val result = repository.updatePriceRule(1, "-20", "2024-07-01T00:00:00Z")

        assertThat(result, `is`(true))

        coVerify { remoteDataSource.updatePriceRule(1, "-20", "2024-07-01T00:00:00Z") }
    }
}
