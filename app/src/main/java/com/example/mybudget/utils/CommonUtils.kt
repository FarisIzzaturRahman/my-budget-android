package com.example.mybudget.utils

import com.example.mybudget.data.model.Time
import java.text.SimpleDateFormat
import java.util.*

class CommonUtils {

    companion object {
        fun getCurrentTimeInIndo(): Time {
            val calendar = Calendar.getInstance()
            val yearFormat = SimpleDateFormat("yyyy", Locale.US)
            val monthFormat = SimpleDateFormat("MMMM", Locale.US)
            val dateFormat = SimpleDateFormat("d", Locale.US)
            val dayFormat = SimpleDateFormat("EEEE", Locale.US)

            val year = yearFormat.format(calendar.time)
            val month = monthFormat.format(calendar.time)
            val date = dateFormat.format(calendar.time)
            val day = dayFormat.format(calendar.time)

            return Time(year, Constants.monthsMap[month], date, Constants.daysMap[day])
        }
    }
}