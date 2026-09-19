package com.project4r.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HistoryItem(val route: String, val airline: String, val date: String, val price: String)

val sampleHistory = listOf(
    HistoryItem("DXB \u2192 KTM", "IndiGo", "19 Sep", "AED 590"),
    HistoryItem("DXB \u2192 BOM", "Air Arabia", "12 Sep", "AED 340"),
    HistoryItem("DXB \u2192 LHR", "Emirates", "5 Sep", "AED 2,100"),
)

@Composable
fun ReviewScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Trend card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("DXB \u2192 KTM Trend", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        Text("\u2193 Falling", color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("Price (AED) \u00b7 Last 6 months",
                        fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    Spacer(Modifier.height(14.dp))

                    // Simple bar chart
                    val bars = listOf(
                        Pair("Apr", 0.75f), Pair("May", 0.68f),
                        Pair("Jun", 0.85f), Pair("Jul", 0.62f),
                        Pair("Aug", 0.55f), Pair("Sep", 0.43f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        bars.forEachIndexed { i, (month, fraction) ->
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(fraction)
                                        .background(
                                            color = if (i == bars.lastIndex)
                                                MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.primaryContainer,
                                            shape = MaterialTheme.shapes.extraSmall
                                        )
                                )
                                Text(month, fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("\uD83D\uDCA1 Book 3 weeks early for best price",
                            fontSize = 11.sp, color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold)
                        Text("AED 590", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        // History header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Search History", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                Text("All \u2192", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold)
            }
        }

        // History rows
        sampleHistory.forEach { item ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(item.route, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${item.date} \u00b7 ${item.airline}",
                                fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Text(item.price, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
