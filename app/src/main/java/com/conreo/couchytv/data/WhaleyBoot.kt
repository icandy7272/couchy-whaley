package com.conreo.couchytv.data

/** Setting value: never switch input after power-on. 1..3 = auto, preferring HDMI1..HDMI3. */
const val BOOT_INPUT_OFF = 0
const val BOOT_INPUT_COUNT = 4

/** One HDMI input as reported by the Whaley TV service. */
data class HdmiPort(val number: Int, val connected: Boolean, val cecDevices: Int) {
    /** Cable from a powered device (hot-plug/5V), or a CEC device answering (e.g. console in standby). */
    val occupied: Boolean get() = connected || cecDevices > 0
}

/**
 * Pure policy for the once-per-power-on Whaley duties that the stock launcher
 * normally performs but skips when Couchy covers it early in boot.
 */
object WhaleyBoot {
    /** Exported by com.whaley.tv.tvplayer.ui/.RootActivity (full-screen input). */
    const val TV_PLAYER_ACTION = "com.whaley.tv.tvplayer.ui.START_TV_PLAYER"
    const val EXTRA_INPUT_SOURCE = "inputSrc"

    /** Only switch input when Couchy is opened right after power-on. */
    const val BOOT_WINDOW_MS = 3 * 60_000L

    /** How long to wait for HDMI hot-plug / CEC discovery after Couchy starts. */
    const val SCAN_MS = 8_000L
    const val SCAN_INTERVAL_MS = 500L

    const val HDMI_PORTS = 3

    /** tv.whaley.api.mode.EnumWtvInputSource ordinals: HDMI1 = 4, HDMI2 = 5, HDMI3 = 6. */
    private const val HDMI1_ORDINAL = 4

    fun inputSourceFor(hdmiPort: Int): Int? =
        if (hdmiPort in 1..HDMI_PORTS) HDMI1_ORDINAL + hdmiPort - 1 else null

    fun preferredPort(bootInput: Int): Int? = bootInput.takeIf { it in 1 until BOOT_INPUT_COUNT }

    /** Preferred port if it has a device, else the lowest occupied port, else stay in Couchy. */
    fun chooseHdmi(preferred: Int, ports: List<HdmiPort>): Int? =
        ports.firstOrNull { it.number == preferred && it.occupied }?.number
            ?: ports.filter { it.occupied }.minOfOrNull { it.number }

    /** Stop early only once the preferred port is found; otherwise give discovery the full window. */
    fun scanFinished(preferred: Int, ports: List<HdmiPort>, elapsedMs: Long): Boolean =
        elapsedMs >= SCAN_MS || ports.any { it.number == preferred && it.occupied }

    fun isNewBoot(currentBootId: String?, lastHandledBootId: String?): Boolean =
        !currentBootId.isNullOrBlank() && currentBootId != lastHandledBootId

    fun withinBootWindow(uptimeMs: Long): Boolean = uptimeMs in 0 until BOOT_WINDOW_MS
}
