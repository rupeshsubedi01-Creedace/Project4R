package com.project4r.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.project4r.ui.components.FlightCard
import com.project4r.ui.components.NlpInputBar
import com.project4r.ui.theme.Green600
import com.project4r.ui.theme.GreenCard
import com.project4r.viewmodel.RouteViewModel

@Composable
fun RouteScreen(viewModel: RouteViewModel = hiltViewModel()) {
    val flights   by viewModel.flights.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val nlpQuery  by viewModel.nlpQuery.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB)),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "\u2708\uFE0F Find Flights",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = Color(0xFF111827)
                )
                Text(
                    "Dubai \u2192 Nepal & beyond",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }

        // NLP Input
        item {
            NlpInputBar(
                value = nlpQuery,
                placeholder = "\"Cheapest flight to Kathmandu next Friday\"",
                onValueChange = { viewModel.updateQuery(it) },
                onSubmit = { viewModel.searchFlights(nlpQuery) }
            )
        }

        // Route pill
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Results",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF111827)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(GreenCard)
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        "DXB \u2192 KTM \u00b7 Live",
                        color = Green600,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Loading
        if (isLoading) {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = Green600,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        }

        // Flight cards
        items(flights) { flight ->
            FlightCard(flight = flight)
        }

        // Footer note
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(GreenCard)
                    .padding(12.dp)
            ) {
                Text(
                    "\uD83D\uDD50 Arrival shown in NPT (UTC+5:45)  \u00b7  \uD83C\uDDF3\uD83C\uDDF5 Bikram Sambat dates shown",
                    fontSize = 11.sp,
                    color = Green600
                )
            }
        }
    }
}
