package com.betkey.utils

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.*

fun Double.roundOffDecimal(): String? {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(this)
}

fun createDateString(sec: Long): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = sec * 1000
    val hh = convertDate(cal.get(Calendar.HOUR_OF_DAY))
    val mm = convertDate(cal.get(Calendar.MINUTE))
    val ss = convertDate(cal.get(Calendar.SECOND))
    val dd = convertDate(cal.get(Calendar.DATE))
    val month = convertDate(cal.get(Calendar.MONTH) + 1)
    val yyyy = cal.get(Calendar.YEAR)
    return "$dd/$month/$yyyy $hh:$mm:$ss"
}

private fun convertDate(num: Int): String {
    return if (num < 10) "0$num" else num.toString()
}

fun String.toFullDate(): Date {
    return try {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
            .parse(this)
    } catch (e: java.lang.Exception) {
        Date(0)
    }
}

fun String.toFullDate2(): Date {
    return try {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(this)
    } catch (e: java.lang.Exception) {
        Date(0)
    }
}

fun Date.dateToString(): String {
    if (this.time == 0L) return ""
    return SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
        .format(this)
}

fun Date.dateToString2(): String {
    if (this.time == 0L) return ""
    return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        .format(this)
}