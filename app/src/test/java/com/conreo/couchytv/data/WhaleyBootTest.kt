package com.conreo.couchytv.data
import org.junit.Assert.*
import org.junit.Test

class WhaleyBootTest {
    private fun ports(vararg occupied: Pair<Boolean, Int>) =
        occupied.mapIndexed { i, (connected, cec) -> HdmiPort(i + 1, connected, cec) }

    @Test fun hdmiPortMapsToWhaleyInputOrdinals() {
        assertEquals(4, WhaleyBoot.inputSourceFor(1))
        assertEquals(5, WhaleyBoot.inputSourceFor(2))
        assertEquals(6, WhaleyBoot.inputSourceFor(3))
        assertNull(WhaleyBoot.inputSourceFor(0))
        assertNull(WhaleyBoot.inputSourceFor(4))
    }
    @Test fun bootInputSettingIsPreferredPortOrOff() {
        assertNull(WhaleyBoot.preferredPort(BOOT_INPUT_OFF))
        assertEquals(2, WhaleyBoot.preferredPort(2))
        assertNull(WhaleyBoot.preferredPort(7))
    }
    @Test fun staysInLauncherWhenNoHdmiHasADevice() {
        assertNull(WhaleyBoot.chooseHdmi(2, ports(false to 0, false to 0, false to 0)))
    }
    @Test fun preferredPortWinsWhenOccupied() {
        assertEquals(2, WhaleyBoot.chooseHdmi(2, ports(true to 0, false to 1, false to 0)))
    }
    @Test fun fallsBackToAnyOccupiedPort() {
        assertEquals(3, WhaleyBoot.chooseHdmi(2, ports(false to 0, false to 0, true to 0)))
        assertEquals(1, WhaleyBoot.chooseHdmi(2, ports(false to 2, false to 0, true to 0)))
    }
    @Test fun cecDeviceInStandbyCountsAsOccupied() {
        assertEquals(2, WhaleyBoot.chooseHdmi(2, ports(false to 0, false to 1, false to 0)))
    }
    @Test fun scanStopsEarlyOnlyForPreferredPort() {
        assertTrue(WhaleyBoot.scanFinished(2, ports(false to 0, true to 0, false to 0), elapsedMs = 0))
        assertFalse(WhaleyBoot.scanFinished(2, ports(true to 0, false to 0, false to 0), elapsedMs = 1_000))
        assertTrue(WhaleyBoot.scanFinished(2, ports(false to 0, false to 0, false to 0), WhaleyBoot.SCAN_MS))
    }
    @Test fun runsOncePerKernelBoot() {
        assertTrue(WhaleyBoot.isNewBoot("b1", null))
        assertTrue(WhaleyBoot.isNewBoot("b2", "b1"))
        assertFalse(WhaleyBoot.isNewBoot("b1", "b1"))
        assertFalse(WhaleyBoot.isNewBoot(null, null))
        assertFalse(WhaleyBoot.isNewBoot("  ", "b1"))
    }
    @Test fun inputSwitchOnlyRightAfterPowerOn() {
        assertTrue(WhaleyBoot.withinBootWindow(25_000L))
        assertFalse(WhaleyBoot.withinBootWindow(WhaleyBoot.BOOT_WINDOW_MS))
    }
}
