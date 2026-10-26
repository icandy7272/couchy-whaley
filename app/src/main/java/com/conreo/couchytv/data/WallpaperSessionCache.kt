package com.conreo.couchytv.data

import java.net.URI

/** A single short-lived URL, in memory only. Never owns a player or video buffers. */
class WallpaperSessionCache {
    private data class Entry(val cid: Long, val stream: BilibiliStreams.Stream,
        val elapsed: Long, val wall: Long, val expires: Long)
    private var entry: Entry? = null

    @Synchronized fun put(cid: Long, stream: BilibiliStreams.Stream, elapsed: Long, wall: Long) {
        val deadline = runCatching {
            URI(stream.url).rawQuery.orEmpty().split('&')
                .firstOrNull { it.startsWith("deadline=") }?.substringAfter('=')?.toLong()
        }.getOrNull()
        val expires = minOf(wall + 180_000L, deadline?.let { it * 1000L - 15_000L } ?: Long.MAX_VALUE)
        entry = Entry(cid, stream, elapsed, wall, expires)
    }

    @Synchronized fun get(elapsed: Long, wall: Long): Pair<Long, BilibiliStreams.Stream>? {
        val saved = entry ?: return null
        if (!clockReady(wall) || elapsed < saved.elapsed || elapsed - saved.elapsed >= 180_000L ||
            wall < saved.wall || wall >= saved.expires) {
            entry = null
            return null
        }
        return saved.cid to saved.stream
    }

    @Synchronized fun invalidate() { entry = null }

    companion object {
        fun clockReady(wall: Long): Boolean = wall >= 1_704_067_200_000L // 2024-01-01
    }
}
