package com.example.taskapp.data

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter


enum class Recurrence {
    NONE, EVERY_DAY, WEEKLY_MON, WEEKLY_TUE, WEEKLY_WED,
    WEEKLY_THU, WEEKLY_FRI, WEEKLY_SAT, WEEKLY_SUN
}

fun LocalDate.next(targetDay: DayOfWeek): LocalDate {
    val currentDow = this.dayOfWeek
    val daysUntil = (targetDay.value - currentDow.value + 7) % 7
    return this.plusDays(if (daysUntil == 0) 7 else daysUntil.toLong())
}

fun Recurrence.recurrenceNextDate(fromDate: LocalDate): LocalDate? {
    return when (this) {
        Recurrence.NONE -> null
        Recurrence.EVERY_DAY -> fromDate.plusDays(1)
        Recurrence.WEEKLY_MON -> fromDate.next(DayOfWeek.MONDAY)
        Recurrence.WEEKLY_TUE -> fromDate.next(DayOfWeek.TUESDAY)
        Recurrence.WEEKLY_WED -> fromDate.next(DayOfWeek.WEDNESDAY)
        Recurrence.WEEKLY_THU -> fromDate.next(DayOfWeek.THURSDAY)
        Recurrence.WEEKLY_FRI -> fromDate.next(DayOfWeek.FRIDAY)
        Recurrence.WEEKLY_SAT -> fromDate.next(DayOfWeek.SATURDAY)
        Recurrence.WEEKLY_SUN -> fromDate.next(DayOfWeek.SUNDAY)
    }
}

fun Recurrence.recurrenceNextDue(): LocalDate? {
    val from = LocalDate.now()
    return when (this) {
        Recurrence.NONE -> null
        Recurrence.EVERY_DAY -> from
        Recurrence.WEEKLY_MON -> from.nextOrSame(DayOfWeek.MONDAY)
        Recurrence.WEEKLY_TUE -> from.nextOrSame(DayOfWeek.TUESDAY)
        Recurrence.WEEKLY_WED -> from.nextOrSame(DayOfWeek.WEDNESDAY)
        Recurrence.WEEKLY_THU -> from.nextOrSame(DayOfWeek.THURSDAY)
        Recurrence.WEEKLY_FRI -> from.nextOrSame(DayOfWeek.FRIDAY)
        Recurrence.WEEKLY_SAT -> from.nextOrSame(DayOfWeek.SATURDAY)
        Recurrence.WEEKLY_SUN -> from.nextOrSame(DayOfWeek.SUNDAY)
    }
}

private fun LocalDate.nextOrSame(day: DayOfWeek): LocalDate {
    val daysAhead = (day.value - this.dayOfWeek.value + 7) % 7
    return this.plusDays(daysAhead.toLong())
}

fun Recurrence.displayName(): String = when (this) {
    Recurrence.NONE -> ""
    Recurrence.EVERY_DAY -> "Every day"
    Recurrence.WEEKLY_MON -> "Every Mon"
    Recurrence.WEEKLY_TUE -> "Every Tue"
    Recurrence.WEEKLY_WED -> "Every Wed"
    Recurrence.WEEKLY_THU -> "Every Thu"
    Recurrence.WEEKLY_FRI -> "Every Fri"
    Recurrence.WEEKLY_SAT -> "Every Sat"
    Recurrence.WEEKLY_SUN -> "Every Sun"
}

fun recurrenceLabel(due: String?, recurrence: Recurrence): String {
    if (due == null && recurrence == Recurrence.NONE) return "No due date"
    val base = due?.let {
        LocalDate.parse(it).format(DateTimeFormatter.ofPattern("dd MMM"))
    } ?: "No due date"
    return if (recurrence != Recurrence.NONE) "$base, ${recurrence.displayName()}" else base
}
