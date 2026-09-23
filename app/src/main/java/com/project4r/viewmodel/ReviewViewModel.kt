package com.project4r.viewmodel

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project4r.data.UserPreferences
import com.project4r.data.model.PricePoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val prefs: UserPreferences
) : ViewModel() {

    private val _points = MutableStateFlow<List<PricePoint>>(emptyList())
    val points: StateFlow<List<PricePoint>> = _points.asStateFlow()

    init {
        _points.value = try {
            val type = object : TypeToken<List<PricePoint>>() {}.type
            Gson().fromJson<List<PricePoint>>(prefs.priceHistoryJson, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
