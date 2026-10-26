package com.conreo.couchytv.data
import org.junit.Assert.*
import org.junit.Test
import java.util.Locale
import java.util.TimeZone

class ChineseDateTest {
    @Test fun formatsCompleteChineseMonthDayAndWeekday() {
        assertEquals("10 月 26 日 周一", ChineseDate.format(1792972800000L, TimeZone.getTimeZone("Asia/Shanghai")))
    }
}
