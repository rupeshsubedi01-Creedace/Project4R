package com.project4r.ui.screens

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project4r.data.api.CaldaysHoliday
import com.project4r.viewmodel.RemindViewModel
import com.project4r.viewmodel.Reminder
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindScreen(viewModel: RemindViewModel = hiltViewModel()) {
    val holidays  by viewModel.holidays.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    val context   = LocalContext.current

    val notifLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* reminder is stored either way; notification shows only if granted */ }

    fun remind(holiday: CaldaysHoliday) {
        if (Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notifLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        viewModel.addReminder(context, holiday, 3)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("\uD83D\uDD14 Remind", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp,
                    color = Color(0xFF111827))
                Text("Live Nepal holidays + your own reminders — nothing fake",
                    fontSize = 13.sp, color = Color(0xFF6B7280))
            }
        }

        // ---- Your reminders (real, scheduled alarms) ----
        if (reminders.isNotEmpty()) {
            item {
                Text("Your reminders", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            }
            reminders.forEach { r ->
                item { ReminderCard(r) { viewModel.cancelReminder(context, r.id) } }
            }
        }

        // ---- Live holidays ----
        if (holidays.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Live holiday data is unavailable right now. Check your connection and come back.",
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
                    Text("\uD83C\uDDF3\uD83C\uDDF5 Nepal Holidays · Live",
                        fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    Text("caldays.com", fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline)
                }
            }
            holidays.forEach { holiday ->
                item { HolidayCard(holiday) { remind(holiday) } }
            }
        }
    }
}

@Composable
fun ReminderCard(r: Reminder, onCancel: () -> Unit) {
    val when_ = Instant.ofEpochMilli(r.fireAt)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("MMM d, yyyy · HH:mm"))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5EE))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("\uD83D\uDD14", fontSize = 16.sp)
            Column(Modifier.weight(1f)) {
                Text(r.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("alerts $when_", fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline)
            }
            TextButton(onClick = onCancel) {
                Text("Cancel", color = Color(0xFFB91C1C), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun HolidayCard(holiday: CaldaysHoliday, onRemind: () -> Unit) {
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
                    Text("\uD83D\uDCC5", fontSize = 18.sp)
                }
            }
            Column(Modifier.weight(1f)) {
                Text(holiday.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(holiday.date, fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline)
            }
            TextButton(onClick = onRemind) {
                Text("\uD83D\uDD14 3d before", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
