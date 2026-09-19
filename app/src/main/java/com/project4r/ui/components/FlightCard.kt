package com.project4r.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project4r.data.model.FlightOffer

@Composable
fun FlightCard(flight: FlightOffer) {
    val isBest = flight.isBestDeal

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isBest)
                MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isBest)
            CardDefaults.outlinedCardBorder()
        else null,
        elevation = CardDefaults.cardElevation(if (isBest) 4.dp else 1.dp)
    ) {
        Column {
            // Main row
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(flight.airlineCode, color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                        }
                    }
                    Column {
                        Text(flight.airlineName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${flight.stops} \u00b7 ${flight.duration}",
                            fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("AED ${flight.priceAed}",
                        fontWeight = FontWeight.ExtraBold, fontSize = 17.sp,
                        color = if (isBest) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface)
                    Text("≈ NPR ${flight.priceNpr}",
                        fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                }
            }

            // Book section
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("\uD83D\uDED2 Buy at:", fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline)
                flight.bookingLinks.forEach { link ->
                    AssistChip(
                        onClick = { /* Open URL */ },
                        label = { Text(link, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.height(26.dp)
                    )
                }
            }

            // Best deal badge
            if (isBest) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text("\u2605 BEST DEAL",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        fontSize = 8.sp, fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
