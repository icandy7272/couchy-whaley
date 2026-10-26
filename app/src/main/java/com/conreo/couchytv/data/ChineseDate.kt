package com.conreo.couchytv.data
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object ChineseDate {
    fun format(wall: Long, zone: TimeZone = TimeZone.getDefault()): String =
        SimpleDateFormat("M 月 d 日 EEE", Locale.SIMPLIFIED_CHINESE).apply { timeZone = zone }.format(Date(wall))
}
