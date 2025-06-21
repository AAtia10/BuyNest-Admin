package com.example.buynest.views.favourites

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.buynest_admin.ui.theme.MainColor

data class Offer(
    val title: String,
    val startDate: String,
    val endDate: String?,
    val discountInfo: String,
    val maxUsages: Int
)

@Composable
fun OffersScreen() {
    val offers = listOf(
        Offer("50% OFF", "30 June 2025 at 10:16", "2 July 2025 at 12:07", "-50.0% After 300.0$", 10),
        Offer("80% OFF", "24 June 2025 at 12:07", "30 June 2025 at 12:07", "-100.0$ After 500.0$", 10),
        Offer("100 Discount", "4 July 2025 at 19:48", "5 July 2025 at 19:48", "-10.0$ After 1000.0$", 3),
        Offer("50% OFF", "30 June 2025 at 10:16", "2 July 2025 at 12:07", "-50.0% After 300.0$", 10),
        Offer("80% OFF", "24 June 2025 at 12:07", "30 June 2025 at 12:07", "-100.0$ After 500.0$", 10),
        Offer("100 Discount", "4 July 2025 at 19:48", "5 July 2025 at 19:48", "-10.0$ After 1000.0$", 3)

    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: navigate to add offer screen */ },
                containerColor =  MainColor,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Offer")
            }
        },
        containerColor = Color.White
    ) { padding ->
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
            items(offers) { offer ->
                OfferCard(offer)
            }
        }
    }
}


@Composable
fun OfferCard(offer: Offer) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalOffer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = offer.title, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            offer.startDate?.let {
                OfferRow(icon = Icons.Default.Schedule, text = "Start: $it")
            }
            offer.endDate?.let {
                OfferRow(icon = Icons.Default.Event, text = "End: $it")
            }

            OfferRow(icon = Icons.Default.AttachMoney, text = offer.discountInfo)
            OfferRow(icon = Icons.Default.ThumbUp, text = "${offer.maxUsages} Max usages")
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



