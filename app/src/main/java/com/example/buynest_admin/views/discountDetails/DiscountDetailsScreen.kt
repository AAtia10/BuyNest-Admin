package com.example.buynest_admin.views.discountDetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.buynest_admin.model.DiscountCode
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.buynest_admin.remote.RemoteDataSourceImpl
import com.example.buynest_admin.remote.ShopifyRetrofitBuilder
import com.example.buynest_admin.repo.ProductRepository
import com.example.buynest_admin.ui.theme.MainColor
import com.example.buynest_admin.viewModels.OffersViewModel
import com.example.buynest_admin.viewModels.OffersViewModelFactory

@Composable
fun DiscountDetailsScreen(priceRuleId: Long, navController: NavHostController) {
    val viewModel: OffersViewModel = viewModel(
        factory = OffersViewModelFactory(
            ProductRepository.getInstance(
                RemoteDataSourceImpl(ShopifyRetrofitBuilder.service)
            )
        )
    )
    var isLoading by remember { mutableStateOf(true) }
    var discountCode by remember { mutableStateOf<DiscountCode?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var newCode by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar("✅ Added successfully")
            showSnackbar = false
        }
    }



    LaunchedEffect(priceRuleId) {
        viewModel.fetchSingleDiscountCode(priceRuleId) {
            discountCode = it
            isLoading = false
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Discount Codes", style = MaterialTheme.typography.titleLarge)
            }
        },
        containerColor = Color.White,
                floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MainColor,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Discount Code")
            }
        },



    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MainColor)
            } else {
                discountCode?.let { code ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Discount Code: ${code.code}", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Usage Count: ${code.usage_count}", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                } ?: Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No discount code available",
                        style = MaterialTheme.typography.titleLarge.copy(color = MainColor),
                    )
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add Discount Code") },
                containerColor = Color.White,
                text = {
                    Column {
                        androidx.compose.material3.OutlinedTextField(
                            value = newCode,
                            onValueChange = { newCode = it },
                            label = { Text("Discount Code") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            isLoading = true
                            viewModel.addDiscountCode(priceRuleId, newCode) {
                                viewModel.fetchSingleDiscountCode(priceRuleId) {
                                    discountCode = it
                                    isLoading = false
                                    showSnackbar = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MainColor)
                    ) {
                        Text("Add", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = MainColor)
                    ) {
                        Text("Cancel")
                    }
                }

            )
        }
    }
}



