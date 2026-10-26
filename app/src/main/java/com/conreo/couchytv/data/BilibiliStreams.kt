package com.conreo.couchytv.data

import kotlinx.serialization.json.*

/** Parse only public, already-authorized API responses; no cookies, login or quality bypass. */
object BilibiliStreams {
    data class Stream(val url: String, val height: Int)

    fun parse(text: String): Stream {
        val root = Json.parseToJsonElement(text).jsonObject
        check(root["code"]?.jsonPrimitive?.intOrNull == 0) { "B站暂未提供可用播放地址" }
        val data = root["data"]?.jsonObject
        // The public progressive MP4 endpoint supplies 720p where DASH is limited to 480p.
        // Accept only the known MP4 format/quality pair and one HTTPS resource.
        val parts = data?.get("durl")?.jsonArray
        if (data?.get("format")?.jsonPrimitive?.content == "mp4720" &&
            data["quality"]?.jsonPrimitive?.intOrNull == 64 && parts?.size == 1) {
            val url = parts.single().jsonObject["url"]?.jsonPrimitive?.content.orEmpty()
            if (url.startsWith("https://")) return Stream(url, 720)
        }
        val candidates = root["data"]?.jsonObject?.get("dash")?.jsonObject
            ?.get("video")?.jsonArray.orEmpty()
        return candidates.mapNotNull { element ->
            val item = element.jsonObject
            val codec = item["codecs"]?.jsonPrimitive?.content.orEmpty()
            val height = item["height"]?.jsonPrimitive?.intOrNull ?: return@mapNotNull null
            val url = (item["baseUrl"] ?: item["base_url"])?.jsonPrimitive?.content.orEmpty()
            if (!codec.startsWith("avc1") || height !in 1..720 || !url.startsWith("https://")) null
            else Stream(url, height)
        }.maxByOrNull { it.height } ?: error("B站未提供免登录且适合此电视的视频流")
    }
}
