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
import com.project4r.ui.components.NlpInputBar
import com.project4r.viewmodel.CurrencyViewModel

@Composable
fun CurrencyScreen(viewModel: CurrencyViewModel = hiltViewModel()) {
    val rates by viewModel.rates.collectAsState()
    val convertedResult by viewModel.convertedResult.collectAsState()
    val nlpQuery by viewModel.nlpQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            NlpInputBar(
                value = nlpQuery,
                placeholder = "\"Convert 590 AED to NPR\"",
                onValueChange = { viewModel.updateQuery(it) },
                onSubmit = { viewModel.convert(nlpQuery) }
            )
        }

        // Conversion result
        convertedResult?.let { result ->
            item {
                Card(colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )) {
                    Text(
                        result,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("\uD83D\uDCB1 Live Rates", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                Text("Base: AED", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary)
            }
        }

        if (isLoading) {
            item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
        }

        items(rates) { rate ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("${rate.flag} ${rate.name}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("1 AED \u2192 ${rate.code}",
                            fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(rate.value.toString(),
                            fontWeight = FontWeight.ExtraBold, fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary)
                        Text(rate.trend, fontSize = 10.sp,
                            color = if (rate.trend.startsWith("\u2191")) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
