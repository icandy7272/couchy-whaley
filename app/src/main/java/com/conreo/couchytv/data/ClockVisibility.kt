package com.conreo.couchytv.data

/** UI policy only: never sets the clock or changes automatic time settings. */
object ClockVisibility {
    fun canDisplay(wall: Long, syncMarker: String?): Boolean =
        wall >= 1_704_067_200_000L && syncMarker != "false"

    // Android 5's vendor sync marker is not part of the public SDK.
    // Missing/restricted properties fall back to the plausible-date guard.
    private val propertyGetter by lazy {
        runCatching { Class.forName("android.os.SystemProperties")
            .getMethod("get", String::class.java) }.getOrNull()
    }
    fun vendorSyncMarker(): String? = runCatching {
        propertyGetter?.invoke(null, "whaley.synctime") as? String
    }.getOrNull()
}
