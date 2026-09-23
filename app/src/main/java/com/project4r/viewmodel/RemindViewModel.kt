package com.project4r.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project4r.data.api.CaldaysHoliday
import com.project4r.data.repository.HolidayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemindViewModel @Inject constructor(
    private val holidayRepository: HolidayRepository
) : ViewModel() {

    private val _holidays = MutableStateFlow<List<CaldaysHoliday>>(emptyList())
    val holidays: StateFlow<List<CaldaysHoliday>> = _holidays.asStateFlow()

    init {
        viewModelScope.launch {
            holidayRepository.nepalHolidays()
                .catch { /* keep empty on error — UI simply hides the section */ }
                .collect { _holidays.value = it }
        }
    }
}
