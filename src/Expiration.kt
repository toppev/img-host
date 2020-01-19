package dev.toppe.img.host

import java.util.concurrent.TimeUnit

enum class TimeFormat(val letter: Char, val toMillis: (number: Long) -> Long) {

    YEAR('Y', { TimeUnit.DAYS.toMillis(365 * it) }),
    MONTH('M', { TimeUnit.DAYS.toMillis(30 * it) }),
    WEEK('w', { TimeUnit.DAYS.toMillis(7 * it) }),
    DAY('D', { TimeUnit.DAYS.toMillis(it) }),
    HOUR('H', { TimeUnit.HOURS.toMillis(it) });
}

fun getInMillis(format: String): Long? {
    val letter = format[format.length - 1]
    for (value in TimeFormat.values()) {
        if (value.letter == letter || value.letter.toLowerCase() == letter) {
            val numbers = format.substring(format.length - 1)
            return numbers.toLongOrNull()?.let { value.toMillis(it) }
        }
    }
    return null
}