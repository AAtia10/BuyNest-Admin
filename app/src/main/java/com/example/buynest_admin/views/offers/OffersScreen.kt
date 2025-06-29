package com.example.buynest.views.favourites

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.buynest_admin.model.AddPriceRulePost
import com.example.buynest_admin.model.DiscountCode
import com.example.buynest_admin.model.PriceRule
import com.example.buynest_admin.remote.RemoteDataSourceImpl
import com.example.buynest_admin.remote.ShopifyRetrofitBuilder
import com.example.buynest_admin.repo.ProductRepository
import com.example.buynest_admin.ui.theme.MainColor
import com.example.buynest_admin.ui.theme.red
import com.example.buynest_admin.ui.theme.white
import com.example.buynest_admin.viewModels.OffersViewModel
import com.example.buynest_admin.viewModels.OffersViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OffersScreen(navController: NavHostController) {
    val viewModel: OffersViewModel = viewModel(
        factory = OffersViewModelFactory(
            ProductRepository.getInstance(
                RemoteDataSourceImpl(ShopifyRetrofitBuilder.service)
            )
        )
    )

    val priceRules by viewModel.priceRules.collectAsState()
    val discountMap by viewModel.discountMap.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        viewModel.fetchPriceRules()
    }

    if (showDialog) {
        AddOfferDialog(
            onDismiss = { showDialog = false },
            onAdd = { request ->
                showDialog = false
                viewModel.addPriceRule(request) {
                    viewModel.fetchPriceRules()

                    scope.launch {
                        snackbarHostState.showSnackbar("✅ Added successfully")
                    }
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor =  MainColor,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Offer")
            }
        },
        containerColor = Color.White
    ) { padding ->


        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MainColor)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 12.dp)
            ) {
                item {
                    Text(
                        text = "Price Rules",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
                items(priceRules, key = { it.id }) { rule ->
                    val dismissState = rememberDismissState()
                    var showDeleteConfirm by remember { mutableStateOf(false) }
                    val code = discountMap[rule.id]

                    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                        LaunchedEffect(rule.id) {
                            dismissState.reset()
                        }
                        showDeleteConfirm = true
                    }


                    if (showDeleteConfirm) {
                        AlertDialog(
                            onDismissRequest = { showDeleteConfirm = false },
                            title = { Text("Delete Offer?") },
                            text = { Text("Are you sure you want to delete this offer?") },
                            containerColor = white,
                            confirmButton = {
                                Button(onClick = {
                                    showDeleteConfirm = false
                                    viewModel.deletePriceRule(
                                        id = rule.id,
                                        onSuccess = {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("✅ Deleted successfully")
                                            }
                                        },
                                        onError = {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("❌ Failed to delete: $it")
                                            }
                                        }
                                    )
                                },colors = ButtonDefaults.buttonColors(containerColor = red)) {
                                    Text("Delete")
                                }
                            },
                            dismissButton = {
                                OutlinedButton(onClick = { showDeleteConfirm = false },colors = ButtonDefaults.buttonColors(containerColor = MainColor)) {
                                    Text("Cancel")
                                }
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
                            OfferCard(
                                rule = rule,
                                discountCode = code,
                                navController = navController,
                                viewModel = viewModel,
                                snackbarHostState = snackbarHostState,
                                scope = scope
                            )
                        }
                    )
                }

            }
            }
        }
    }




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OfferCard(rule: PriceRule, discountCode: DiscountCode?, navController: NavHostController,viewModel: OffersViewModel,snackbarHostState: SnackbarHostState,
              scope: CoroutineScope
) {


    var isEditingValue by remember { mutableStateOf(false) }
    var isEditingEndDate by remember { mutableStateOf(false) }

    var editedValue by remember { mutableStateOf(rule.value.trimStart('-')) }
    var editedEndDate by remember {
        mutableStateOf(
            try {
                ZonedDateTime.parse((rule.ends_at ?: "").toString()).toLocalDateTime()
            } catch (e: Exception) {
                LocalDateTime.now().plusDays(7)
            }
        )
    }

    LaunchedEffect(rule) {
        editedValue = rule.value.trimStart('-')
        editedEndDate = try {
            ZonedDateTime.parse((rule.ends_at ?: "").toString()).toLocalDateTime()
        } catch (e: Exception) {
            LocalDateTime.now().plusDays(7)
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DateTimePicker(
            initial = editedEndDate,
            onDateTimeSelected = {
                editedEndDate = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    val formatter = DateTimeFormatter.ofPattern("d/M/yyyy 'at' H:mm")

    val startFormatted = try {
        ZonedDateTime.parse(rule.starts_at)
            .withZoneSameInstant(ZoneId.systemDefault())
            .format(formatter)
    } catch (e: Exception) {
        rule.starts_at
    }

    val endFormatted = rule.ends_at?.let {
        try {
            ZonedDateTime.parse(it.toString())
                .withZoneSameInstant(ZoneId.systemDefault())
                .format(formatter)
        } catch (e: Exception) {
            it
        }
    }

    val valueText = if (rule.value_type == "percentage") {
        "${rule.value.trimStart('-')}%"
    } else {
        "${rule.value.trimStart('-')}"
    }

    val valueWithCondition = "-$valueText"



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                navController.navigate("discount_details/${rule.id}")
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalOffer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = rule.title ?: "Untitled", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OfferRow(icon = Icons.Default.Schedule, text = "Starts: $startFormatted")

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OfferRow(
                    icon = Icons.Default.Event,
                    text = if (isEditingEndDate)
                        "Ends:"
                    else
                        "Ends: $endFormatted"
                )

                if (isEditingEndDate) {
                    OutlinedButton(onClick = { showDatePicker = true }) {
                        Text(editedEndDate.toLocalDate().toString(), color = MainColor)
                    }
                }

                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit End Date",
                    tint = MainColor,
                    modifier = Modifier
                        .clickable { isEditingEndDate  = true }
                        .padding(start = 8.dp)
                )
            }


            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OfferRow(
                    icon = Icons.Default.AttachMoney,
                    text = if (isEditingValue ) "Value:" else "Value: $valueWithCondition"
                )

                if (isEditingValue ) {
                    OutlinedTextField(
                        value = editedValue,
                        onValueChange = { editedValue = it },
                        modifier = Modifier.width(100.dp),
                        singleLine = true
                    )
                }

                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Value",
                    tint = MainColor,
                    modifier = Modifier
                        .clickable { isEditingValue  = true }
                        .padding(start = 8.dp)
                )
            }

            if (isEditingValue || isEditingEndDate) {
                Button( colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                    onClick = {
                        val cairoZone = ZoneId.of("Africa/Cairo")
                        val endsAtZoned = if (isEditingEndDate) {
                            ZonedDateTime.of(editedEndDate, cairoZone).toString()
                        } else null
                        val editedVal = "-${editedValue}"

                        viewModel.updatePriceRule(
                            rule.id,
                            editedVal,
                            endsAtZoned,
                            onSuccess = {
                                isEditingValue = false
                                isEditingEndDate = false
                                viewModel.fetchPriceRules()
                                scope.launch {
                                    snackbarHostState.showSnackbar("✅ Saved successfully")
                                }
                            },
                                    onError = {
                                        isEditingValue = false
                                        isEditingEndDate = false
                                    }
                        )
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Save")
                }
            }

        }


        }
    }



@Composable
fun OfferRow(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MainColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddOfferDialog(
    onDismiss: () -> Unit,
    onAdd: (AddPriceRulePost) -> Unit
) {
    val now = remember { LocalDateTime.now() }
    var title by remember { mutableStateOf("") }
    var discountType by remember { mutableStateOf("percentage") }
    var discountValue by remember { mutableStateOf("") }


    var startDateTime by remember { mutableStateOf(now) }
    var endDateTime by remember { mutableStateOf(now.plusDays(7)) }

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    val cairoZone = ZoneId.of("Africa/Cairo")
    val startZoned = ZonedDateTime.of(startDateTime, cairoZone).toString()
    val endZoned = ZonedDateTime.of(endDateTime, cairoZone).toString()

    if (showStartPicker) {
        DateTimePicker(
            initial = startDateTime,
            onDateTimeSelected = { startDateTime = it; showStartPicker = false },
            onDismiss = { showStartPicker = false }
        )
    }

    if (showEndPicker) {
        DateTimePicker(
            initial = endDateTime,
            onDateTimeSelected = { endDateTime = it; showEndPicker = false },
            onDismiss = { showEndPicker = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = { Text("Add Price Rule") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Text("Start Date & Time", style = MaterialTheme.typography.bodyMedium)
                val timeFormatter = DateTimeFormatter.ofPattern("H:mm")
                OutlinedButton(
                    onClick = { showStartPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text("${startDateTime.toLocalDate()} ${startDateTime.toLocalTime().format(timeFormatter)}", color = MainColor)

                }

                Spacer(Modifier.height(12.dp))

                Text("End Date & Time", style = MaterialTheme.typography.bodyMedium)
                OutlinedButton(
                    onClick = { showEndPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("${endDateTime.toLocalDate()} ${endDateTime.toLocalTime().format(timeFormatter)}", color = MainColor)
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { discountType = "percentage" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (discountType == "percentage") MainColor else Color.LightGray
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Percentage",
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = { discountType = "fixed_amount" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (discountType == "fixed_amount") MainColor else Color.LightGray
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Fixed",
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall
                        )

                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = discountValue,
                    onValueChange = { discountValue = it },
                    label = { Text("Discount amount") },
                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = Color.Black)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            val request = AddPriceRulePost(
                                title = title,
                                value = "-${discountValue.toDoubleOrNull()}",
                                value_type = discountType,
                                starts_at = startZoned,
                                ends_at = endZoned,
                                usage_limit = null,
                                prerequisite_subtotal_range = null
                            )
                            onAdd(request)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MainColor)
                    ) {
                        Text("Done", color = Color.White)
                    }
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}


    @RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateTimePicker(
    initial: LocalDateTime,
    onDateTimeSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val picker = remember { mutableStateOf(initial) }

    val showDate = remember { mutableStateOf(true) }

    if (showDate.value) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val selectedDate = LocalDate.of(year, month + 1, day)
                picker.value = picker.value.withYear(year).withMonth(month + 1).withDayOfMonth(day)
                showDate.value = false
            },
            initial.year,
            initial.monthValue - 1,
            initial.dayOfMonth
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
        }.show()
    } else {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                picker.value = picker.value.withHour(hour).withMinute(minute)
                onDateTimeSelected(picker.value)
            },
            initial.hour,
            initial.minute,
            true
        ).show()
    }
}







