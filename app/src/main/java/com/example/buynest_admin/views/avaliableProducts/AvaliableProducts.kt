package com.example.buynest_admin.views.avaliableProducts



import com.example.buynest_admin.model.Product
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.buynest_admin.ui.theme.MainColor
import com.example.buynest_admin.ui.theme.red
import com.example.buynest_admin.ui.theme.white
import com.example.buynest_admin.viewModels.ProductViewModel



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AvaliableProductsScreen(viewModel: ProductViewModel) {

    val products by viewModel.products.collectAsState()
    val selectedBrand by viewModel.selectedBrand.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var productDeleted by remember { mutableStateOf(false) }

    val filteredProducts = remember(products, selectedBrand) {
        selectedBrand?.let { brand ->
            products.filter { it.vendor.equals(brand, ignoreCase = true) }
        } ?: products
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            Text(
                text = "Available Products",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            SearchBar(viewModel = viewModel)

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MainColor)
                }
            } else {
                LazyColumn {
                    items(filteredProducts, key = { it.id }) { product ->
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
                                        productDeleted = true
                                        viewModel.fetchProducts()
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
                            },
                            dismissContent = {
                                ProductCard(product)
                            }
                        )
                    }
                }
            }
        }
    }

    // Show Snackbar when a product is deleted
    LaunchedEffect(productDeleted) {
        if (productDeleted) {
            snackbarHostState.showSnackbar("🗑️ Product deleted successfully")
            productDeleted = false
        }
    }
}


@Composable
fun ProductCard(product: Product) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
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

@Composable
fun ConfirmDeleteDialog(
    productTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to delete $productTitle?") },
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




