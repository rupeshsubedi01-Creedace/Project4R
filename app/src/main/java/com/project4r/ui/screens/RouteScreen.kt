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
    val weather   by viewModel.weather.collectAsState()

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
                    "✈️ Find Flights",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = Color(0xFF111827)
                )
                Text(
                    "Dubai → Nepal & beyond",
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

        // Live destination weather (Open-Meteo, keyless)
        weather?.let { w ->
            item { WeatherCard(w) }
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
                        "One way · Live",
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

        // Honest empty state — no fake flights ever shown
        if (!isLoading && flights.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "No live one-way flights found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF111827)
                        )
                        Text(
                            "Pull to refresh to retry the live search. We never show estimated or mock fares.",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
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
                    "\uD83D\uDD50 Arrival shown in NPT (UTC+5:45)  ·  \uD83C\uDDF3\uD83C\uDDF5 Bikram Sambat dates shown",
                    fontSize = 11.sp,
                    color = Green600
                )
            }
        }
    }
}

@Composable
fun WeatherCard(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE8F5EE))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )
            Text(
                "open-meteo",
                fontSize = 9.sp,
                color = Color(0xFF66BB6A)
            )
        }
    }
}
