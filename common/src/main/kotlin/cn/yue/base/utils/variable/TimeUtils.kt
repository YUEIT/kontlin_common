package cn.yue.base.utils.variable

import android.annotation.SuppressLint
import java.lang.StringBuilder
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*

object TimeUtils {

    private const val SECONDS_PER_MINUTE = 60
    private const val SECONDS_PER_HOUR = 60 * 60
    private const val SECONDS_PER_DAY = 24 * 60 * 60
    private val DEFAULT_FORMAT = TimeFormat("  ", ":", ":", "", "")

    private val SDF_THREAD_LOCAL: ThreadLocal<MutableMap<String, SimpleDateFormat>> = object : ThreadLocal<MutableMap<String, SimpleDateFormat>>() {
        override fun initialValue(): MutableMap<String, SimpleDateFormat> {
            return HashMap()
        }
    }

    fun getDefaultFormat(): SimpleDateFormat {
        return getSafeDateFormat("yyyy-MM-dd HH:mm:ss")
    }

    @SuppressLint("SimpleDateFormat")
    fun getSafeDateFormat(pattern: String): SimpleDateFormat {
        val sdfMap = SDF_THREAD_LOCAL.get()!!
        var simpleDateFormat = sdfMap[pattern]
        if (simpleDateFormat == null) {
            simpleDateFormat = SimpleDateFormat(pattern)
            sdfMap[pattern] = simpleDateFormat
        }
        return simpleDateFormat
    }

    /**
     * Formatted time string to the date.
     *
     * @param time    The formatted time string.
     * @param pattern The pattern of date format, such as yyyy/MM/dd HH:mm
     * @return the date
     */
    fun string2Date(time: String?, pattern: String): Date? {
        return string2Date(time, getSafeDateFormat(pattern))
    }

    /**
     * Formatted time string to the date.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @return the date
     */
    fun string2Date(time: String?, format: DateFormat): Date? {
        try {
            return format.parse(time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Date to the formatted time string.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param date The date.
     * @return the formatted time string
     */
    fun date2String(date: Date?): String? {
        return date2String(date, getDefaultFormat()!!)
    }

    /**
     * Date to the formatted time string.
     *
     * @param date    The date.
     * @param pattern The pattern of date format, such as yyyy/MM/dd HH:mm
     * @return the formatted time string
     */
    fun date2String(date: Date?, pattern: String): String? {
        return getSafeDateFormat(pattern).format(date)
    }

    /**
     * Date to the formatted time string.
     *
     * @param date   The date.
     * @param format The format.
     * @return the formatted time string
     */
    fun date2String(date: Date?, format: DateFormat): String? {
        return format.format(date)
    }

    fun getWeekStr(week: Int): String {
       return when (week) {
           SUNDAY -> "周日"
           MONDAY -> "周一"
           TUESDAY -> "周二"
           WEDNESDAY -> "周三"
           THURSDAY -> "周四"
           FRIDAY -> "周五"
           SATURDAY -> "周六"
            else -> ""
        }
    }

    fun getWeekStartMonday(): Date {
        val cal = Calendar.getInstance()
        var currentWeek = cal[Calendar.DAY_OF_WEEK]
        if (currentWeek == SUNDAY) currentWeek = 8
        val currentTime = cal.time.time
        val startWeekTime = currentTime - (currentWeek - MONDAY) * 24 * 60 * 60 * 1000
        return Date(startWeekTime)
    }

    fun getMonthDayStr(date: Date = Date(), format: String = "MM-dd"): String {
        return getSafeDateFormat(format).format(date)
    }

    fun getYearMonthStr(date: Date = Date(), format: String = "yyyy-MM"): String {
        return getSafeDateFormat(format).format(date)
    }

    fun getYearMonthDayStr(date: Date = Date(), format: String = "yyyy-MM-dd"): String {
        return getSafeDateFormat(format).format(date)
    }

    fun getFullTimeStr(date: Date = Date(), format: String = "yyyy-MM-dd HH:mm:ss"): String {
        return getSafeDateFormat(format).format(date)
    }

    fun getYearMonthDayDate(timeStr: String, format: String = "yyyy-MM-dd"): Date? {
        return getSafeDateFormat(format).parse(timeStr)
    }

    fun getFullTimeDate(timeStr: String, format: String = "yyyy-MM-dd HH:mm:ss"): Date? {
        return getSafeDateFormat(format).parse(timeStr)
    }

    fun formatDuration(duration: Long, format: TimeFormat = DEFAULT_FORMAT, showMillis: Boolean = false): String {
        if (duration == 0L) {
            return ""
        }
        val millis = (duration % 1000).toInt()
        var seconds = Math.floor((duration / 1000).toDouble()).toInt()
        var days = 0
        var hours = 0
        var minutes = 0
        if (seconds > SECONDS_PER_DAY) {
            days = seconds / SECONDS_PER_DAY
            seconds -= days * SECONDS_PER_DAY
        }
        if (seconds > SECONDS_PER_HOUR) {
            hours = seconds / SECONDS_PER_HOUR
            seconds -= hours * SECONDS_PER_HOUR
        }
        if (seconds > SECONDS_PER_MINUTE) {
            minutes = seconds / SECONDS_PER_MINUTE
            seconds -= minutes * SECONDS_PER_MINUTE
        }
        val dayString = if (days != 0) {
            "$days${format.day}"
        } else {
            ""
        }
        val hoursString = if (days != 0 && hours != 0) {
            "${printField(hours, 2)}${format.hours}"
        } else {
            ""
        }
        val millisString = if (showMillis) {
            "${printField(millis, 3)}${format.millis}"
        } else {
            ""
        }
        return StringBuilder()
            .append(dayString)
            .append(hoursString)
            .append("${printField(minutes, 2)}${format.minute}")
            .append("${printField(seconds, 2)}${format.second}")
            .append(millisString)
            .toString()
    }

    private fun printField(time: Int, length: Int): String {
        var cur = length - time.toString().length
        if (cur == 1) {
            return "0$time"
        } else if (cur == 2) {
            return "00$time"
        }
        return "$time"
    }

    class TimeFormat(val day: String, val hours: String, val minute: String, val second: String, val millis: String)
}