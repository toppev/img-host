package dev.toppe.img.host

enum class TimeFormat(val letter: Char, toMillis: (number: Long) -> Long) {

    YEAR('Y', { java.util.concurrent.TimeUnit.DAYS.toMillis(365 * it) }),
    MONTH('M', { java.util.concurrent.TimeUnit.DAYS.toMillis(30 * it) }),
    WEEK('w', { java.util.concurrent.TimeUnit.DAYS.toMillis(7 * it) }),
    DAY('D', { java.util.concurrent.TimeUnit.DAYS.toMillis(it) }),
    HOUR('H', { java.util.concurrent.TimeUnit.HOURS.toMillis(it) });
}

fun getInMillis(format: String): Long? {
    val letter = format[format.length - 1]
    for (value in TimeFormat.values()) {
        if (value.letter == letter || value.letter.toLowerCase() == letter) {
            val numbers = format.substring(format.length - 1)
            return numbers.toLongOrNull()
        }
    }
    return null
}