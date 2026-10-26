package com.conreo.couchytv.data
import org.junit.Assert.*
import org.junit.Test

class ClockVisibilityTest {
    @Test fun hidesResetClockEvenWhenStaleSyncMarkerSaysTrue() {
        assertFalse(ClockVisibility.canDisplay(1_167_652_800_000L, "true"))
    }
    @Test fun respectsWhaleySyncCompletionAndCanHideAgain() {
        val now = 1_792_977_424_000L
        assertFalse(ClockVisibility.canDisplay(now, "false"))
        assertTrue(ClockVisibility.canDisplay(now, "true"))
        assertFalse(ClockVisibility.canDisplay(now, "false"))
    }
    @Test fun absentVendorPropertyUsesPlausibleTimeFallback() {
        assertTrue(ClockVisibility.canDisplay(1_792_977_424_000L, null))
        assertTrue(ClockVisibility.canDisplay(1_792_977_424_000L, ""))
        assertFalse(ClockVisibility.canDisplay(0L, null))
    }
}
