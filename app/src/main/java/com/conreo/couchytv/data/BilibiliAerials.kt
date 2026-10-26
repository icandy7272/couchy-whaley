package com.conreo.couchytv.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import android.os.SystemClock
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** Metadata only is bundled. Each expiring media URL is requested when its clip is selected. */
object BilibiliAerials {
    const val REFERER = "https://www.bilibili.com/video/BV1tK4y1w7Wb/"
    // This CDN rejects the custom Android-TV UA (403); its standard public playback UA succeeds.
    const val USER_AGENT = "Mozilla/5.0"
    val headers = mapOf("Referer" to REFERER, "User-Agent" to USER_AGENT)
    val client: OkHttpClient by lazy {
        OkHttpClient.Builder().connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS).build()
    }

    private val queue = WallpaperQueue()
    fun invalidate() = queue.invalidate()
    fun clearPending() = queue.clearPending()

    private suspend fun requestText(request: Request): String = suspendCancellableCoroutine { continuation ->
        val call = client.newCall(request)
        continuation.invokeOnCancellation { call.cancel() }
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (continuation.isActive) continuation.resumeWithException(e)
            }
            override fun onResponse(call: Call, response: Response) {
                try {
                    val body = response.use {
                        check(it.isSuccessful) { "B站播放请求失败（${it.code}）" }
                        checkNotNull(it.body).string()
                    }
                    if (continuation.isActive) continuation.resume(body)
                } catch (e: Exception) {
                    if (continuation.isActive) continuation.resumeWithException(e)
                }
            }
        })
    }

    suspend fun next(context: Context, previousCid: Long?, advance: Boolean = false): Pair<Long, BilibiliStreams.Stream> = withContext(Dispatchers.IO) {
        val now = SystemClock.elapsedRealtime()
        val wall = System.currentTimeMillis()
        val cached = if (advance) queue.takeNext(now, wall) else queue.current(now, wall)
        if (cached != null) {
            android.util.Log.i("WhaleyBilibili", "${if (advance) "Prepared" else "Reusing"} ${cached.second.height}p stream for cid=${cached.first}")
            return@withContext cached
        }
        fetch(context, previousCid).also {
            coroutineContext.ensureActive()
            queue.putCurrent(it.first, it.second, SystemClock.elapsedRealtime(), System.currentTimeMillis())
        }
    }

    suspend fun prefetch(context: Context, previousCid: Long) = withContext(Dispatchers.IO) {
        val next = fetch(context, previousCid)
        coroutineContext.ensureActive()
        queue.stage(next.first, next.second, SystemClock.elapsedRealtime(), System.currentTimeMillis())
        android.util.Log.i("WhaleyBilibili", "Prefetched ${next.second.height}p stream for cid=${next.first}")
    }

    private suspend fun fetch(context: Context, previousCid: Long?): Pair<Long, BilibiliStreams.Stream> {
        val entries = context.assets.open("bilibili-sonoma.json").bufferedReader().use {
            Json.parseToJsonElement(it.readText()).jsonArray
        }
        val candidates = entries.filter { it.jsonObject.getValue("cid").jsonPrimitive.long != previousCid }.ifEmpty { entries }
        val entry = candidates.random().jsonObject
        val cid = entry.getValue("cid").jsonPrimitive.long
        val bvid = entry["bvid"]?.jsonPrimitive?.content ?: "BV1tK4y1w7Wb"
        var lastFailure: Exception? = null
        for (flags in listOf(0, 16)) {
            coroutineContext.ensureActive()
            val url = "https://api.bilibili.com/x/player/playurl?bvid=$bvid&cid=$cid&qn=64&fnval=$flags"
            val request = Request.Builder().url(url).apply { headers.forEach { (key, value) -> header(key, value) }; header("Referer", "https://www.bilibili.com/video/$bvid/") }.build()
            try {
                val stream = BilibiliStreams.parse(requestText(request))
                coroutineContext.ensureActive()
                android.util.Log.i("WhaleyBilibili", "Selected ${stream.height}p stream for cid=$cid")
                return cid to stream
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                lastFailure = e
            }
        }
        throw checkNotNull(lastFailure)
    }
}
