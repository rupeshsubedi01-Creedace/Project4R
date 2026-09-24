package com.project4r.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project4r.ui.components.FlightCard
import com.project4r.ui.components.NlpInputBar
import com.project4r.ui.theme.Green600
import com.project4r.ui.theme.GreenCard
import com.project4r.viewmodel.RouteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteScreen(viewModel: RouteViewModel = hiltViewModel()) {
    val flights    by viewModel.flights.collectAsState()
    val isLoading  by viewModel.isLoading.collectAsState()
    val nlpQuery   by viewModel.nlpQuery.collectAsState()
    val weather    by viewModel.weather.collectAsState()
    val originCity by viewModel.originCity.collectAsState()
    val originIata by viewModel.originIata.collectAsState()
    val tripType   by viewModel.tripType.collectAsState()
    val returnDate by viewModel.returnDate.collectAsState()
    val note       by viewModel.note.collectAsState()

    val context = LocalContext.current
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.any { it }) viewModel.detectLocation(context)
    }
    LaunchedEffect(Unit) {
        val granted = context.checkSelfPermission(
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
        context.checkSelfPermission(
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (granted) viewModel.detectLocation(context)
        else permLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    val pullRefreshState = rememberPullToRefreshState()
    LaunchedEffect(pullRefreshState.isRefreshing) {
        if (pullRefreshState.isRefreshing) viewModel.searchFlights(nlpQuery)
    }
    LaunchedEffect(isLoading) {
        if (!isLoading) pullRefreshState.endRefresh()
    }

    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(pullRefreshState.nestedScrollConnection)
    ) {
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
                    "$originCity → Nepal & beyond",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
                Text(
                    "📍 from $originIata · auto-detected",
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF)
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

        // Trip type: one way / round trip + return date
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TripPill("One way", tripType == "oneway") { viewModel.setTripType("oneway") }
                TripPill("Round trip", tripType == "round") { viewModel.setTripType("round") }
                if (tripType == "round") {
                    TextButton(
                        onClick = {
                            openReturnDatePicker(context, returnDate) { viewModel.setReturnDate(it) }
                        }
                    ) {
                        Text(
                            "↩ $returnDate",
                            color = Green600,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TextButton(onClick = { viewModel.searchFlights(nlpQuery) }) {
                        Text(
                            "⟳ Refresh",
                            color = Green600,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                            note ?: "Pull to refresh to retry the live search. We never show estimated or mock fares.",
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

        PullToRefreshContainer(
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            containerColor = Color.White,
            contentColor = Green600
        )
    }
}

@Composable
fun TripPill(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) Green600 else Color(0xFFE5E7EB))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            label,
            color = if (selected) Color.White else Color(0xFF374151),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun openReturnDatePicker(
    context: android.content.Context,
    current: String,
    onPicked: (String) -> Unit
) {
    val parts = current.split("-")
    val y = parts.getOrNull(0)?.toIntOrNull() ?: 2026
    val m = parts.getOrNull(1)?.toIntOrNull() ?: 1
    val d = parts.getOrNull(2)?.toIntOrNull() ?: 1
    android.app.DatePickerDialog(context, { _, yy, mm, dd ->
        onPicked(String.format("%04d-%02d-%02d", yy, mm + 1, dd))
    }, y, m - 1, d).show()
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
