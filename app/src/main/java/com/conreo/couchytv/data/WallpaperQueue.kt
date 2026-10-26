package com.conreo.couchytv.data

/** One current address and one prepared address; neither owns video buffers. */
class WallpaperQueue {
    private var active = WallpaperSessionCache()
    private var pending = WallpaperSessionCache()
    @Synchronized fun current(elapsed: Long, wall: Long) = active.get(elapsed, wall)
    @Synchronized fun putCurrent(cid: Long, stream: BilibiliStreams.Stream, elapsed: Long, wall: Long) = active.put(cid, stream, elapsed, wall)
    @Synchronized fun stage(cid: Long, stream: BilibiliStreams.Stream, elapsed: Long, wall: Long) = pending.put(cid, stream, elapsed, wall)
    @Synchronized fun clearPending() = pending.invalidate()
    @Synchronized fun invalidate() { active.invalidate(); pending.invalidate() }
    @Synchronized fun takeNext(elapsed: Long, wall: Long): Pair<Long, BilibiliStreams.Stream>? {
        val next = pending.get(elapsed, wall)
        active.invalidate()
        // Transfer the cache itself so fetch age and server deadline remain intact.
        if (next != null) active = pending
        pending = WallpaperSessionCache()
        return next
    }
    companion object {
        fun shouldPrefetch(duration: Long, position: Long, currentUri: String?, expectedUri: String,
            ready: Boolean): Boolean = ready && currentUri == expectedUri &&
            duration > 0 && position >= 0 && duration - position in 0..12_000L
    }
}
