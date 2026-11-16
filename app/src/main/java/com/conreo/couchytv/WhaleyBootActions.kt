package com.conreo.couchytv

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import com.conreo.couchytv.data.HdmiPort
import com.conreo.couchytv.data.WhaleyBoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Restores two stock-launcher boot duties that are skipped when the boot helper
 * brings Couchy to the front before the Whaley home has finished drawing:
 * turning off the blinking power-on LED, and entering an HDMI input that has a
 * device, which lets HDMI-CEC wake the connected console.
 */
object WhaleyBootActions {
    private const val TAG = "CouchyWhaleyBoot"
    private const val PREFS = "whaley_boot"
    private const val KEY_LAST_BOOT = "last_boot_id"
    private const val BOOT_ID_PATH = "/proc/sys/kernel/random/boot_id"
    private const val LED_MODE_OFF = 1
    /** Last scan summary, readable over adb for diagnosis (logcat rotates before adb is up). */
    private const val SCAN_REPORT = "boot-scan.txt"

    /** Claims this power-on; true only for the first call after each kernel boot. */
    fun claimBoot(context: Context): Boolean {
        val bootId = runCatching { File(BOOT_ID_PATH).readText().trim() }
            .onFailure { Log.w(TAG, "Cannot read boot id; skipping boot duties", it) }
            .getOrNull()
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!WhaleyBoot.isNewBoot(bootId, prefs.getString(KEY_LAST_BOOT, null))) return false
        // commit(): the claim must survive an immediate recreate or process death.
        return prefs.edit().putString(KEY_LAST_BOOT, bootId).commit()
    }

    /** Same call the stock launcher makes via LEDUtility.enableLED(false). */
    fun turnOffBootLed() {
        Thread({
            runCatching {
                val cls = Class.forName("com.mstar.android.tvapi.common.TvManager")
                val manager = cls.getMethod("getInstance").invoke(null)
                cls.getMethod("setLedMode", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                    .invoke(manager, LED_MODE_OFF, 0)
            }.onSuccess { Log.i(TAG, "Boot LED turned off") }
                .onFailure { Log.w(TAG, "Cannot turn off boot LED", it) }
        }, "whaley-boot-led").start()
    }

    /**
     * Polls HDMI hot-plug and CEC discovery; returns the port to enter, or null to stay
     * in Couchy. Never switches outside the power-on window or when the TV API is missing.
     */
    suspend fun findBootHdmi(context: Context, preferred: Int): Int? = withContext(Dispatchers.IO) {
        if (!WhaleyBoot.withinBootWindow(SystemClock.elapsedRealtime())) return@withContext null
        val start = SystemClock.elapsedRealtime()
        val log = StringBuilder("uptime=${start}ms preferred=HDMI$preferred\n")
        var ports: List<HdmiPort>
        while (true) {
            val elapsed = SystemClock.elapsedRealtime() - start
            ports = readHdmiPorts() ?: run {
                log.append("+${elapsed}ms TV API unavailable\n")
                writeReport(context, log)
                return@withContext null
            }
            log.append("+${elapsed}ms ").append(ports.joinToString(" ") {
                "HDMI${it.number}:conn=${it.connected},cec=${it.cecDevices}"
            }).append('\n')
            if (WhaleyBoot.scanFinished(preferred, ports, elapsed)) break
            delay(WhaleyBoot.SCAN_INTERVAL_MS)
        }
        WhaleyBoot.chooseHdmi(preferred, ports).also {
            log.append("choice=").append(it?.let { p -> "HDMI$p" } ?: "stay in Couchy").append('\n')
            Log.i(TAG, log.toString())
            writeReport(context, log)
        }
    }

    fun enterHdmi(context: Context, port: Int) {
        val source = WhaleyBoot.inputSourceFor(port) ?: return
        runCatching {
            context.startActivity(
                Intent(WhaleyBoot.TV_PLAYER_ACTION)
                    .putExtra(WhaleyBoot.EXTRA_INPUT_SOURCE, source)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }.onSuccess { Log.i(TAG, "Entered HDMI$port") }
            .onFailure { Log.w(TAG, "Cannot enter HDMI$port", it) }
    }

    /** Whaley TV service via the boot-classpath wtvapi; null when unavailable. */
    private fun readHdmiPorts(): List<HdmiPort>? = runCatching {
        val enumCls = Class.forName("tv.whaley.api.mode.EnumWtvInputSource")
        val sources = enumCls.enumConstants ?: return null
        val input = Class.forName("tv.whaley.api.manager.WtvApiInputSourceManger")
            .getMethod("getInstance").invoke(null)
        val cec = Class.forName("tv.whaley.api.manager.WtvApiCecManager")
            .getMethod("getInstance").invoke(null)
        val connected = input.javaClass.getMethod("getHdmiConnectedStatus", enumCls)
        val cecCount = cec.javaClass.getMethod("getCecListCnt", Int::class.javaPrimitiveType)
        (1..WhaleyBoot.HDMI_PORTS).map { port ->
            val source = sources[WhaleyBoot.inputSourceFor(port)!!]
            HdmiPort(
                number = port,
                connected = connected.invoke(input, source) as? Boolean ?: false,
                cecDevices = cecCount.invoke(cec, port - 1) as? Int ?: 0,
            )
        }
    }.onFailure { Log.w(TAG, "Cannot read HDMI state", it) }.getOrNull()

    private fun writeReport(context: Context, log: CharSequence) {
        runCatching {
            val dir = context.getExternalFilesDir(null) ?: return
            File(dir, SCAN_REPORT).writeText(log.toString())
        }.onFailure { Log.w(TAG, "Cannot write boot scan report", it) }
    }
}
