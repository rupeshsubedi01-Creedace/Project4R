package com.project4r.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project4r.data.model.FlightOffer
import com.project4r.ui.components.NlpInputBar
import com.project4r.ui.components.FlightCard
import com.project4r.viewmodel.RouteViewModel

@Composable
fun RouteScreen(viewModel: RouteViewModel = hiltViewModel()) {
    val flights by viewModel.flights.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val nlpQuery by viewModel.nlpQuery.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // NLP Input Bar
        item {
            NlpInputBar(
                value = nlpQuery,
                placeholder = "\"Cheapest flight to Kathmandu next Friday\"",
                onValueChange = { viewModel.updateQuery(it) },
                onSubmit = { viewModel.searchFlights(nlpQuery) }
            )
        }

        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Flight Results", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                Text("DXB \u2192 KTM \u00b7 Live",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Loading
        if (isLoading) {
            item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
        }

        // Flight cards
        items(flights) { flight ->
            FlightCard(flight = flight)
        }

        // Arrival info
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    "\uD83D\uDD50 Arrival shown in NPT (UTC+5:45) \u00b7 Bikram Sambat: 3 Asoj 2083",
                    modifier = Modifier.padding(12.dp),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
