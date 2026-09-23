package com.project4r.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project4r.data.api.CaldaysHoliday
import com.project4r.ui.components.NlpInputBar
import com.project4r.viewmodel.RemindViewModel

data class Reminder(
    val icon: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val badgeType: BadgeType
)

enum class BadgeType { GREEN, ORANGE, PURPLE }

val sampleReminders = listOf(
    Reminder("\u2708\uFE0F", "DXB → KTM Price Drop", "IndiGo · below AED 500", "Active", BadgeType.GREEN),
    Reminder("\uD83C\uDF82", "Mom's Birthday", "Kartik 15, 2083 · 9:00 AM NPT", "28 days", BadgeType.ORANGE),
    Reminder("\uD83D\uDD01", "Weekly Price Check", "Every Monday · 9:00 AM Dubai", "Mon", BadgeType.PURPLE),
    Reminder("\uD83C\uDF89", "Dashain 2083", "Asoj 29, 2083 · Nepal calendar", "38 days", BadgeType.ORANGE),
)

@Composable
fun RemindScreen(viewModel: RemindViewModel = hiltViewModel()) {
    var nlpQuery by remember { mutableStateOf("") }
    val holidays by viewModel.holidays.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            NlpInputBar(
                value = nlpQuery,
                placeholder = "\"Alert when IndiGo drops below AED 500\"",
                onValueChange = { nlpQuery = it },
                onSubmit = { /* TODO: NLP process reminder */ }
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Active Alerts", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                Text("Manage", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold)
            }
        }
        sampleReminders.forEach { reminder ->
            item { ReminderCard(reminder) }
        }

        if (holidays.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "🇳🇵 Nepal Holidays · Live"
                    Text("\uD83C\uDDF3\uD83C\uDDF5 Nepal Holidays · Live",
                        fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    Text("caldays.com", fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline)
                }
            }
            holidays.take(6).forEach { holiday ->
                item { HolidayCard(holiday) }
            }
        }
    }
}

@Composable
fun ReminderCard(reminder: Reminder) {
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
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(reminder.icon, fontSize = 18.sp)
                }
            }
            Column(Modifier.weight(1f)) {
                Text(reminder.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(reminder.subtitle, fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline)
            }
            val badgeColor = when (reminder.badgeType) {
                BadgeType.GREEN  -> MaterialTheme.colorScheme.primaryContainer
                BadgeType.ORANGE -> MaterialTheme.colorScheme.errorContainer
                BadgeType.PURPLE -> MaterialTheme.colorScheme.secondaryContainer
            }
            Surface(shape = MaterialTheme.shapes.small, color = badgeColor) {
                Text(reminder.badge, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(niceDate, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
