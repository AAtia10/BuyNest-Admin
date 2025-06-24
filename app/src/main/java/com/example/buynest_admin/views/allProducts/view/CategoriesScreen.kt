package com.example.buynest.views.categories


import android.util.Log
import com.example.buynest_admin.model.Product
import com.example.buynest_admin.model.ProductOption
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.buynest_admin.RoutesScreens
import com.example.buynest_admin.model.ImageData
import com.example.buynest_admin.model.NewProductPost
import com.example.buynest_admin.model.ProductData
import com.example.buynest_admin.model.VariantPost
import com.example.buynest_admin.ui.theme.MainColor
import com.example.buynest_admin.ui.theme.red
import com.example.buynest_admin.ui.theme.white
import com.example.buynest_admin.views.allProducts.viewModel.ProductViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AllProductsScreen(navController: NavHostController,viewModel: ProductViewModel) {

    var showAddProductDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val vendorsList = products.map { it.vendor }.distinct()
    val productTypesList = products.map { it.product_type }.distinct()
    var productDeleted by remember { mutableStateOf(false) }



    LaunchedEffect(Unit) {
        viewModel.fetchLocations()
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
            )
        }
        ,
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddProductDialog = true  }, containerColor = MainColor) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        },


        ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            Text(
                text = "All Products",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            SearchBar(viewModel = viewModel)

            if (isLoading) {

                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MainColor)
                }
            } else {
                LazyColumn {
                    items(products, key = { it.id }) { product ->
                        var showDeleteDialog by remember { mutableStateOf(false) }

                        val dismissState = rememberDismissState()

                        LaunchedEffect(dismissState.currentValue) {
                            if (dismissState.currentValue == DismissValue.DismissedToStart) {
                                showDeleteDialog = true
                                dismissState.reset()
                            }
                        }

                        if (showDeleteDialog) {
                            ConfirmDeleteDialog(
                                productTitle = product.title,
                                onConfirm = {
                                    viewModel.deleteProduct(product.id) {
                                        productDeleted = true // ← فعل الفلاج
                                        viewModel.fetchProducts() // ← حدث المنتجات
                                    }

                                    showDeleteDialog = false
                                },
                                onDismiss = {
                                    showDeleteDialog = false
                                }
                            )
                        }

                        SwipeToDismiss(
                            state = dismissState,
                            directions = setOf(DismissDirection.EndToStart),
                            background = {
                                val color = if (dismissState.dismissDirection == DismissDirection.EndToStart) red else Color.Transparent

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color)
                                        .padding(end = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    if (dismissState.dismissDirection == DismissDirection.EndToStart) {
                                        Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            ,
                            dismissContent = {
                                ProductCard(product = product) {
                                    viewModel.setSelectedProduct(product)
                                    navController.navigate(RoutesScreens.ProductInfo.route)
                                }
                            }
                        )
                    }


                }
                LaunchedEffect(productDeleted) {
                    if (productDeleted) {
                        snackbarHostState.showSnackbar("🗑️ Product deleted successfully")
                        productDeleted = false // reset الفلاج
                    }
                }

                if (showAddProductDialog) {
                    AddProductDialog(
                        vendors = vendorsList,
                        productTypes = productTypesList,
                        onDismiss = { showAddProductDialog = false },
                        onProductAdded = {
                            viewModel.fetchProducts()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("✅ Product added successfully!")
                            }
                            showAddProductDialog = false
                        },
                        viewModel = viewModel,
                        snackbarHostState = snackbarHostState
                    )
                }
            }

        }
    }
}


@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = product.image.src,
                contentDescription = product.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.title.uppercase(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${product.vendor}, ${product.product_type}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("${product.variants.firstOrNull()?.inventory_quantity ?: 0} In Stock", style = MaterialTheme.typography.bodySmall)
                Text("${product.variants.firstOrNull()?.price ?: "0.0"} EGP", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(viewModel: ProductViewModel) {
    var query by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(48.dp)
            .background(Color.White, shape = RoundedCornerShape(25.dp))
            .border(1.dp, color = MainColor.copy(alpha = 0.5f), shape = RoundedCornerShape(25.dp)),
    ) {
        TextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.onSearch(it)
            },
            placeholder = { Text("Search", fontSize = 14.sp) },
            singleLine = true,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            trailingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    vendors: List<String>,
    productTypes: List<String>,
    onProductAdded: () -> Unit,
    viewModel: ProductViewModel,
    snackbarHostState: SnackbarHostState

) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var vendor by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var imageUrls by remember { mutableStateOf(mutableListOf<String>()) }
    var newImageUrl by remember { mutableStateOf("") }


    val coroutineScope = rememberCoroutineScope()
    val locationId by viewModel.locations.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Product") },
        containerColor = white,
        text = {
            Column(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 1500.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MainColor,
                        unfocusedBorderColor = MainColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MainColor,
                        unfocusedBorderColor = MainColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                var expandedVendor by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expandedVendor,
                    onExpandedChange = { expandedVendor = !expandedVendor },
                    modifier = Modifier.background(Color.White)
                ) {
                    OutlinedTextField(
                        value = vendor,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vendor") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedVendor) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MainColor,
                            unfocusedBorderColor = MainColor
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedVendor,
                        onDismissRequest = { expandedVendor = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        vendors.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    vendor = it
                                    expandedVendor = false
                                }
                            )
                        }
                    }
                }

                var expandedType by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expandedType,
                    onExpandedChange = { expandedType = !expandedType },
                    modifier = Modifier.background(Color.White)

                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Product Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = MainColor,
                            unfocusedIndicatorColor = MainColor
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        productTypes.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    type = it
                                    expandedType = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = size,
                    onValueChange = { size = it },
                    label = { Text("Size") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MainColor,
                        unfocusedBorderColor = MainColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Color") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = MainColor,
                        unfocusedIndicatorColor = MainColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MainColor,
                        unfocusedBorderColor = MainColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MainColor,
                        unfocusedBorderColor = MainColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newImageUrl,
                    onValueChange = { newImageUrl = it },
                    label = { Text("Enter Image URL") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MainColor,
                        unfocusedBorderColor = MainColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (newImageUrl.isNotBlank()) {
                            imageUrls.add(newImageUrl)
                            newImageUrl = ""
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MainColor)
                ) {
                    Text("Add Image URL", color = Color.White)
                }

                imageUrls.forEachIndexed { index, url ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "📷 $url",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { imageUrls.removeAt(index) },
                            colors = ButtonDefaults.buttonColors(containerColor = red),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Remove", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && price.isNotBlank() && quantity.isNotBlank()) {
                        val variant = VariantPost(
                            option1 = if (size.isNotBlank()) size else null,
                            option2 = if (color.isNotBlank()) color else null,
                            price = price,
                            inventory_quantity = quantity.toIntOrNull() ?: 0
                        )

                        val imagesList = imageUrls.map { ImageData(it) }

                        val optionsList = mutableListOf<ProductOption>()
                        if (size.isNotBlank()) optionsList.add(ProductOption("Size"))
                        if (color.isNotBlank()) optionsList.add(ProductOption("Color"))




                        val newProduct = NewProductPost(
                            ProductData(
                                title = title,
                                body_html = desc,
                                vendor = vendor,
                                product_type = type,
                                variants = listOf(variant),
                                images = imagesList,
                                options = if (optionsList.isNotEmpty()) optionsList else null
                            )
                        )

                        val locId = locationId.firstOrNull()?.id
                        if (locId != null) {
                            coroutineScope.launch {
                                val gson = Gson()
                                Log.d("PRODUCT", gson.toJson(newProduct))

                                viewModel.addProductWithInventory(newProduct, locId, variant) {
                                    onProductAdded()
                                }
                            }
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please fill in all required fields")
                        }
                    }
                }
                ,
                colors = ButtonDefaults.buttonColors(containerColor = MainColor)
            ) {
                Text("Add", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = MainColor)) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ConfirmDeleteDialog(
    productTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Product") },
        text = { Text("Are you sure you want to delete \"$productTitle\"?") },
        containerColor = white,
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = red)) {
                Text("Delete", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = MainColor)) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}









