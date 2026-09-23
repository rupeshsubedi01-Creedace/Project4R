package com.project4r.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project4r.data.api.CaldaysHoliday
import com.project4r.viewmodel.RemindViewModel

@Composable
fun RemindScreen(viewModel: RemindViewModel = hiltViewModel()) {
    val holidays by viewModel.holidays.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("\uD83D\uDD14 Remind", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp,
                    color = Color(0xFF111827))
                Text("Live Nepal public holidays · no sample data",
                    fontSize = 13.sp, color = Color(0xFF6B7280))
            }
        }

        if (holidays.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Live holiday data is unavailable right now. Pull to refresh later.",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        } else {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "🇳 Nepal Holidays · Live"
                    Text("\uD83C\uDDF3\uD83C\uDDF5 Nepal Holidays · Live",
                        fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    Text("caldays.com", fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline)
                }
            }
            holidays.forEach { holiday ->
                item { HolidayCard(holiday) }
            }
        }
    }
}

@Composable
fun HolidayCard(holiday: CaldaysHoliday) {
    // "2026-01-14" -> "Jan 14"
    val parts = holiday.date.split("-")
    val monthIdx = parts.getOrNull(1)?.toIntOrNull() ?: 0
    val months = listOf("Jan","Feb","Mar","Apr","May","Jun",
                        "Jul","Aug","Sep","Oct","Nov","Dec")
    val niceDate = "${months.getOrElse(monthIdx - 1) { "Jan" }} ${parts.getOrNull(2) ?: ""}"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // "📅"
                    Text("\uD83D\uDCC5", fontSize = 18.sp)
                }
            }
            Column(Modifier.weight(1f)) {
                Text(holiday.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(holiday.date, fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline)
            }
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(niceDate, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
