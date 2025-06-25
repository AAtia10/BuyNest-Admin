package com.example.buynest_admin.views.ProductInfo

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.buynest_admin.model.VariantPost
import com.example.buynest_admin.ui.theme.MainColor
import com.example.buynest_admin.ui.theme.red
import com.example.buynest_admin.views.allProducts.viewModel.ProductViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductInfoScreen(viewModel: ProductViewModel,navController: NavHostController) {
    val product by viewModel.selectedProduct.collectAsState()
    if (product == null) return

    val sizes = remember(product) {
        product!!.options.firstOrNull { it.name.lowercase() == "size" }?.values ?: emptyList()
    }
    val colors = remember(product) {
        product!!.options.firstOrNull { it.name.lowercase() == "color" }?.values ?: emptyList()
    }

    val pagerState = rememberPagerState()

    val defaultSize = sizes.firstOrNull()
    val defaultColor = colors.firstOrNull()

    var selectedSize by remember { mutableStateOf(defaultSize) }
    var selectedColor by remember { mutableStateOf(defaultColor) }

    val variant = product?.variants?.firstOrNull {
        it.option1 == selectedSize && it.option2 == selectedColor
    }

    var showDeleteDialog by remember { mutableStateOf(false) }



    var editedPrice by remember { mutableStateOf(variant?.price ?: "N/A") }
    var editedAvailability by remember { mutableStateOf(variant?.inventory_quantity?.toString() ?: "") }
    var isEditingPrice by remember { mutableStateOf(false) }
    var isEditingAvailability by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val locationId by viewModel.locations.collectAsState()
    val selectedLocationId = locationId.firstOrNull()?.id

    var isEditingTitle by remember { mutableStateOf(false) }
    var isEditingDesc by remember { mutableStateOf(false) }
    var editedTitle by remember { mutableStateOf(product!!.title) }
    var editedDesc by remember { mutableStateOf(product!!.body_html) }



    LaunchedEffect(selectedSize, selectedColor, product) {
        val selectedVariant = product!!.variants.firstOrNull {
            it.option1 == selectedSize && it.option2 == selectedColor
        }
        editedPrice = selectedVariant?.price ?: ""
        editedAvailability = selectedVariant?.inventory_quantity?.toString() ?: ""
    }

    LaunchedEffect(true) {
        viewModel.fetchLocations()
        viewModel.newVariantResult.collectLatest { result ->
            result.onSuccess {
                Log.d("ProductInfoScreen", "Variant added successfully")
                snackbarHostState.showSnackbar("Product Saved successfully")

                viewModel.fetchProductById(product!!.id)


            }.onFailure {
                snackbarHostState.showSnackbar("Product Saved successfully")
            }
        }
    }

    LaunchedEffect(product) {
        selectedSize = product!!.options.firstOrNull { it.name.lowercase() == "size" }?.values?.firstOrNull()
        selectedColor = product!!.options.firstOrNull { it.name.lowercase() == "color" }?.values?.firstOrNull()

        editedTitle = product!!.title
        editedDesc = product!!.body_html
    }



    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                HorizontalPager(
                    count = product!!.images.size,
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                ) { page ->
                    ZoomableAsyncImage(
                        model = product!!.images[page].src,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                    )
                }

                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Product",
                        tint = red
                    )
                }
            }


            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(product!!.images.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == pagerState.currentPage) 12.dp else 8.dp)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(if (index == pagerState.currentPage) MainColor else Color.Gray)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title row
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isEditingTitle) {
                    OutlinedTextField(
                        value = editedTitle,
                        onValueChange = { editedTitle = it },
                        modifier = Modifier.weight(1f)
                    )
                    Text("✔", modifier = Modifier
                        .clickable {
                            isEditingTitle = false
                        }
                        .padding(start = 8.dp),
                        color = MainColor
                    )
                } else {
                    Text(
                        text = editedTitle,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                        modifier = Modifier.weight(1f)
                    )
                    Text("✏️", modifier = Modifier.clickable { isEditingTitle = true }.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

// Description row
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isEditingDesc) {
                    OutlinedTextField(
                        value = editedDesc,
                        onValueChange = { editedDesc = it },
                        modifier = Modifier.weight(1f)
                    )
                    Text("✔", modifier = Modifier
                        .clickable {
                            isEditingDesc = false
                        }
                        .padding(start = 8.dp),
                        color = MainColor
                    )
                } else {
                    Text(
                        text = editedDesc,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                    Text("✏️", modifier = Modifier.clickable { isEditingDesc = true }.padding(start = 8.dp))
                }
            }


            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Size:", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "+ Add Variant",
                    color = MainColor,
                    modifier = Modifier.clickable { showDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                sizes.forEach { size ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (selectedSize == size) MainColor.copy(alpha = 0.3f) else Color.Transparent)
                            .border(2.dp, if (selectedSize == size) MainColor else Color.Gray, CircleShape)
                            .clickable { selectedSize = size },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = size)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Color:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selectedColor == color) MainColor.copy(alpha = 0.2f) else Color.Transparent)
                            .border(2.dp, if (selectedColor == color) MainColor else Color.Gray, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable { selectedColor = color }
                    ) {
                        Text(text = color)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditingPrice) {
                    OutlinedTextField(
                        value = editedPrice,
                        onValueChange = { editedPrice = it },
                        label = { Text("Price") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("✔", fontSize = 20.sp, color = MainColor, modifier = Modifier.clickable {
                        isEditingPrice = false
                    })
                } else {
                    Text(
                        text = if (editedPrice.isNotBlank()) "Price: $editedPrice EGP" else "Price: N/A", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("✏️", fontSize = 18.sp, modifier = Modifier.clickable { isEditingPrice = true })
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditingAvailability) {
                    OutlinedTextField(
                        value = editedAvailability,
                        onValueChange = { editedAvailability = it },
                        label = { Text("Availability") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("✔", fontSize = 20.sp, color = MainColor, modifier = Modifier.clickable {
                        isEditingAvailability = false
                    })
                } else {
                    Text(
                        text = if (editedAvailability.isNotBlank()) {
                            "Availability: $editedAvailability in stock"
                        } else {
                            "Availability: N/A"
                        }, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("✏️", fontSize = 18.sp, modifier = Modifier.clickable { isEditingAvailability = true })
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (selectedSize != null && selectedColor != null && variant != null) {
                        coroutineScope.launch {
                            viewModel.updateVariant(
                                variantId = variant!!.id,
                                variant = VariantPost(
                                    option1 = selectedSize!!,
                                    option2 = selectedColor!!,
                                    price = editedPrice,
                                    inventory_quantity = editedAvailability.toIntOrNull() ?: 0
                                )
                            )

                            viewModel.updateProductTitleAndDescription(product!!.id, editedTitle, editedDesc)
                            viewModel.fetchProductById(product!!.id)
                            isEditingTitle = false
                            isEditingDesc = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MainColor)
            ) {
                Text(text = "Save Product", color = Color.White)
            }



            if (showDialog) {
                AddVariantDialog(
                    onDismiss = { showDialog = false },
                    onSubmit = {
                        if (selectedLocationId != null) {
                            viewModel.addVariant(product!!.id, it, selectedLocationId)
                        }
                        showDialog = false
                    }
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Product") },
            text = { Text("Are you sure you want to delete this product?") },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        viewModel.deleteProduct(product!!.id) {
                            showDeleteDialog = false
                            snackbarHostState.showSnackbar("🗑️ Product deleted successfully")
                            navController.popBackStack()
                            viewModel.clearSelectedProduct()
                            viewModel.fetchProducts()
                        }

                    }
                }) {
                    Text("Delete", color = red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }


}


@Composable
fun AddVariantDialog(
    onDismiss: () -> Unit,
    onSubmit: (VariantPost) -> Unit
) {
    var size by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onSubmit(
                    VariantPost(
                        option1 = size,
                        option2 = color,
                        price = price,
                        inventory_quantity = quantity.toIntOrNull() ?: 0
                    )
                )
            },
                colors = ButtonDefaults.buttonColors(containerColor = MainColor))
            { Text("Done") }
        },
        title = { Text("Add Variant") },
        text = {
            Column {
                OutlinedTextField(value = size, onValueChange = { size = it }, label = { Text("Size") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MainColor,
                        unfocusedBorderColor = MainColor
                    )
                    )
                OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MainColor,
                        unfocusedBorderColor = MainColor
                    )
                    )
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MainColor,
                        unfocusedBorderColor = MainColor
                    )
                    )
                OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantity") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MainColor,
                        unfocusedBorderColor = MainColor
                    )
                    )
            }
        }
    )
}
