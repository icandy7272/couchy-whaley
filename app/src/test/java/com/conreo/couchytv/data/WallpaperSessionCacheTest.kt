package com.conreo.couchytv.data

import org.junit.Assert.*
import org.junit.Test

class WallpaperSessionCacheTest {
    @Test fun returnsSameClipForShortReturnButExpiresAfterThreeMinutes() {
        val cache = WallpaperSessionCache()
        val stream = BilibiliStreams.Stream("https://example.test/video?deadline=2000000000", 720)
        cache.put(42, stream, 1_000, 1_900_000_000_000)
        assertEquals(42L, cache.get(100_000, 1_900_000_099_000)?.first)
        assertNull(cache.get(181_000, 1_900_000_180_000))
    }
    @Test fun serverExpiryAndClockResetPreventReuse() {
        val cache = WallpaperSessionCache()
        cache.put(42, BilibiliStreams.Stream("https://example.test/v?deadline=1900000040", 720), 0, 1_900_000_000_000)
        assertNotNull(cache.get(10_000, 1_900_000_010_000))
        assertNull(cache.get(30_000, 1_900_000_030_000))
        cache.put(42, BilibiliStreams.Stream("https://example.test/v",720), 0, 1_900_000_000_000)
        assertNull(cache.get(1_000, 1_167_652_800_000))
    }
    @Test fun playbackFailureOrClipEndInvalidatesAddress() {
        val cache = WallpaperSessionCache()
        cache.put(42, BilibiliStreams.Stream("https://example.test/v",720), 0, 1_900_000_000_000)
        cache.invalidate()
        assertNull(cache.get(1_000, 1_900_000_001_000))
    }
}
