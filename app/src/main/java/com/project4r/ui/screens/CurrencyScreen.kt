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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project4r.ui.components.NlpInputBar
import com.project4r.ui.theme.*
import com.project4r.viewmodel.CurrencyViewModel

@Composable
fun CurrencyScreen(viewModel: CurrencyViewModel = hiltViewModel()) {
    val rates           by viewModel.rates.collectAsState()
    val convertedResult by viewModel.convertedResult.collectAsState()
    val nlpQuery        by viewModel.nlpQuery.collectAsState()
    val isLoading       by viewModel.isLoading.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB)),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "\uD83D\uDCB1 Currency",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = Color(0xFF111827)
                )
                Text(
                    "Live AED rates for Nepali expats",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }

        item {
            NlpInputBar(
                value = nlpQuery,
                placeholder = "\"Convert 590 AED to NPR\"",
                onValueChange = { viewModel.updateQuery(it) },
                onSubmit = { viewModel.convert(nlpQuery) }
            )
        }

        convertedResult?.let { result ->
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(Green600)
                        .padding(16.dp)
                ) {
                    Text(
                        result,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Live Rates",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF111827)
                )
                Text(
                    "Base: AED",
                    fontSize = 12.sp,
                    color = Green600,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (isLoading) {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Green600)
                }
            }
        }

        if (!isLoading && rates.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text(
                        "Live rates are unavailable right now — check your connection and pull to refresh. No estimated rates are shown.",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        }

        items(rates) { rate ->
            val isNPR = rate.code == "NPR"
            val cardShape = RoundedCornerShape(16.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(if (isNPR) 6.dp else 2.dp, cardShape)
                    .clip(cardShape)
                    .background(if (isNPR) Green600 else Color.White)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(rate.flag, fontSize = 28.sp)
                        Column {
                            Text(
                                rate.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isNPR) Color.White else Color(0xFF111827)
                            )
                            Text(
                                "1 AED \u2192 ${rate.code}",
                                fontSize = 11.sp,
                                color = if (isNPR) Color.White.copy(alpha = 0.8f)
                                        else Color(0xFF9CA3AF)
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "${rate.value}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = if (isNPR) Color.White else Green600
                        )
                        Text(
                            rate.trend,
                            fontSize = 11.sp,
                            color = if (isNPR) Color.White.copy(alpha = 0.8f)
                                    else Color(0xFF6B7280)
                        )
                    }
                }
            }
        }
    }
}
