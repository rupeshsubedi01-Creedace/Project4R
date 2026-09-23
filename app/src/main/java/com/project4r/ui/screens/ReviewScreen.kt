package com.project4r.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project4r.viewmodel.ReviewViewModel

@Composable
fun ReviewScreen(viewModel: ReviewViewModel = hiltViewModel()) {
    val points by viewModel.points.collectAsState()

    val cheapest = points.minByOrNull { it.minPrice }
    val average  = if (points.isEmpty()) 0 else points.sumOf { it.minPrice } / points.size
    val last     = points.lastOrNull()
    val belowAvg = last != null && average > 0 && last.minPrice <= average

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("\uD83D\uDCCA Review", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp,
                    color = Color(0xFF111827))
                Text("Your real searched fares — nothing estimated",
                    fontSize = 13.sp, color = Color(0xFF6B7280))
            }
        }

        if (points.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("No price history yet",
                            fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Search a route on the Route tab. Every live search saves its cheapest fare here, so your trend chart is built from your own searches — never from mock data.",
                            fontSize = 12.sp, color = Color(0xFF6B7280))
                    }
                }
            }
        } else {
            // Stats row — all computed from real observations
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Cheapest seen", "AED ${cheapest?.minPrice}", "\u2708\uFE0F ${cheapest?.airline ?: ""}")
                    StatCard("Average", "AED $average", "${points.size} searches")
                    StatCard(
                        "Latest",
                        "AED ${last?.minPrice}",
                        if (belowAvg) "\u25bc below avg" else "\u25b2 above avg"
                    )
                }
            }

            // Real trend chart from the last 12 observed points
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Your fare trend", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            Text(if (belowAvg) "\u25bc Good time to book"
                                 else "\u25b2 Above your average",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(14.dp))
                        val shown = points.takeLast(12)
                        val maxP  = (shown.maxOf { it.minPrice }).coerceAtLeast(1)
                        Row(
                            modifier = Modifier.fillMaxWidth().height(70.dp),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            shown.forEach { p ->
                                val fraction = (p.minPrice.toFloat() / maxP).coerceIn(0.15f, 1f)
                                Column(
                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(fraction)
                                            .background(
                                                color = if (p == shown.last())
                                                    MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.primaryContainer,
                                                shape = RoundedCornerShape(3.dp)
                                            )
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text("Cheapest live fare per search (AED)",
                            fontSize = 10.sp, color = Color(0xFF9CA3AF))
                    }
                }
            }

            // Real history rows
            item {
                Text("Search history", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            }
            points.asReversed().take(20).forEach { p ->
                item {
                    Card(modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(1.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(p.route, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${p.date} \u00b7 ${p.airline}",
                                    fontSize = 11.sp, color = Color(0xFF9CA3AF))
                            }
                            Text("AED ${p.minPrice}", fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.StatCard(title: String, value: String, sub: String) {
    Card(
        modifier = Modifier.weight(1f),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, fontSize = 10.sp, color = Color(0xFF9CA3AF), fontWeight = FontWeight.SemiBold)
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary)
            Text(sub, fontSize = 9.sp, color = Color(0xFF9CA3AF))
        }
    }
}
