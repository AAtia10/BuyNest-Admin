package com.example.buynest_admin.views.ProductInfo


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.buynest_admin.ui.theme.MainColor
import com.example.buynest_admin.views.allProducts.viewModel.ProductViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage




@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductInfoScreen(viewModel: ProductViewModel) {
    val product = viewModel.selectedProduct.collectAsState().value ?: return
    val sizes = product.options.firstOrNull { it.name.lowercase() == "size" }?.values ?: emptyList()
    val colors = product.options.firstOrNull { it.name.lowercase() == "color" }?.values ?: emptyList()
    val initialPrice = product.variants.firstOrNull()?.price ?: "N/A"
    val availability = product.variants.firstOrNull()?.inventory_quantity ?: 0
    val description = product.body_html

    val scrollState = rememberScrollState()
    var selectedSize by remember { mutableStateOf<String?>(null) }
    var selectedColor by remember { mutableStateOf<String?>(null) }

    val pagerState = rememberPagerState()

    // Editing price
    var isEditingPrice by remember { mutableStateOf(false) }
    var editedPrice by remember { mutableStateOf(initialPrice) }
    var currentPrice by remember { mutableStateOf(initialPrice) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        HorizontalPager(
            count = product.images.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) { page ->
            ZoomableAsyncImage(
                model = product.images[page].src,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // -- Dots Indicator --
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(product.images.size) { index ->
                val isSelected = index == pagerState.currentPage
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 12.dp else 8.dp)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) MainColor else Color.Gray)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = product.title,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
        )

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
                modifier = Modifier.clickable {
                    // TODO: Navigate to add variant screen or show dialog
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            sizes.forEach { size ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (selectedSize == size) MainColor.copy(alpha = 0.3f) else Color.Transparent)
                        .border(
                            2.dp,
                            if (selectedSize == size) MainColor else Color.Gray,
                            CircleShape
                        )
                        .clickable { selectedSize = size },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = size)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Color:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedColor == color) MainColor.copy(alpha = 0.2f) else Color.Transparent)
                        .border(
                            2.dp,
                            if (selectedColor == color) MainColor else Color.Gray,
                            RoundedCornerShape(20.dp)
                        )
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEditingPrice) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.OutlinedTextField(
                        value = editedPrice,
                        onValueChange = { editedPrice = it },
                        singleLine = true,
                        modifier = Modifier.width(140.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "✔",
                        fontSize = 20.sp,
                        color = MainColor,
                        modifier = Modifier.clickable {
                            currentPrice = editedPrice
                            isEditingPrice = false
                        }
                    )
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Price: $currentPrice EGP",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "✏️",
                        fontSize = 18.sp,
                        modifier = Modifier.clickable {
                            isEditingPrice = true
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Availability: $availability in stock",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Description:", style = MaterialTheme.typography.titleMedium)
        Text(text = description)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // TODO: Save product changes
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MainColor)
        ) {
            Text(text = "Save Product", color = Color.White)
        }
    }
}






