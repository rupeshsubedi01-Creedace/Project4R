package com.project4r.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project4r.data.UserPreferences
import com.project4r.data.api.CaldaysHoliday
import com.project4r.data.repository.HolidayRepository
import com.project4r.service.ReminderReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

data class Reminder(
    val id: Long,
    val title: String,
    val holidayDate: String,
    val fireAt: Long
)

@HiltViewModel
class RemindViewModel @Inject constructor(
    private val holidayRepository: HolidayRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    private val _holidays = MutableStateFlow<List<CaldaysHoliday>>(emptyList())
    val holidays: StateFlow<List<CaldaysHoliday>> = _holidays.asStateFlow()

    private val _reminders = MutableStateFlow(loadReminders())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    init {
        viewModelScope.launch {
            holidayRepository.nepalHolidays()
                .catch { /* keep empty on error — honest empty state */ }
                .collect { _holidays.value = it }
        }
    }

    /** Schedule a real alarm + notification, daysBefore the holiday. */
    fun addReminder(context: Context, holiday: CaldaysHoliday, daysBefore: Int = 3) {
        val fire = try {
            LocalDate.parse(holiday.date)
                .minusDays(daysBefore.toLong())
                .atTime(LocalTime.of(9, 0))
                .atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
        } catch (_: Exception) { return }

        val r = Reminder(
            id          = System.currentTimeMillis(),
            title       = holiday.name,
            holidayDate = holiday.date,
            fireAt      = fire
        )
        val list = _reminders.value + r
        _reminders.value = list
        prefs.remindersJson = Gson().toJson(list)
        schedule(context, r, daysBefore)
    }

    fun cancelReminder(context: Context, id: Long) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pendingIntent(context, id))
        val list = _reminders.value.filterNot { it.id == id }
        _reminders.value = list
        prefs.remindersJson = Gson().toJson(list)
    }

    private fun schedule(context: Context, r: Reminder, daysBefore: Int) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.set(
            AlarmManager.RTC_WAKEUP,
            r.fireAt,
            pendingIntent(context, r.id, r.title, "$r.title is in $daysBefore day(s) (${r.holidayDate})")
        )
    }

    private fun pendingIntent(
        context: Context,
        id: Long,
        title: String = "",
        text: String = ""
    ): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("text", text)
        }
        return PendingIntent.getBroadcast(
            context, id.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun loadReminders(): List<Reminder> = try {
        Gson().fromJson<List<Reminder>>(
            prefs.remindersJson,
            object : TypeToken<List<Reminder>>() {}.type
        ) ?: emptyList()
    } catch (_: Exception) { emptyList() }
}
