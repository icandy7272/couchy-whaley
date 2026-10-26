package com.conreo.couchytv.data
import org.junit.Assert.*
import org.junit.Test

class WallpaperQueueTest {
    @Test fun stagingDoesNotReplaceCurrentReturnAddress() {
        val q = WallpaperQueue()
        q.putCurrent(1, BilibiliStreams.Stream("https://example.test/one",720),0,1_900_000_000_000)
        q.stage(2, BilibiliStreams.Stream("https://example.test/two",720),100,1_900_000_000_100)
        assertEquals(1L,q.current(200,1_900_000_000_200)?.first)
        assertEquals(2L,q.takeNext(200,1_900_000_000_200)?.first)
        assertEquals(2L,q.current(200,1_900_000_000_200)?.first)
        assertNull(q.takeNext(300,1_900_000_000_300))
    }
    @Test fun expiredStageCannotBePromoted() {
        val q = WallpaperQueue()
        q.stage(2,BilibiliStreams.Stream("https://example.test/v?deadline=1900000020",720),0,1_900_000_000_000)
        assertNull(q.takeNext(6_000,1_900_000_006_000))
    }
    @Test fun onlyPrefetchWhenKnownDurationIsNearEnd() {
        assertFalse(WallpaperQueue.shouldPrefetch(-1, 0, "new", "new", true))
        assertFalse(WallpaperQueue.shouldPrefetch(100_000, 10_000, "new", "new", true))
        assertTrue(WallpaperQueue.shouldPrefetch(100_000, 89_000, "new", "new", true))
    }
    @Test fun promotionPreservesOriginalAddressAge() {
        val q = WallpaperQueue()
        q.stage(2,BilibiliStreams.Stream("https://example.test/two",720),0,1_900_000_000_000)
        assertNotNull(q.takeNext(170_000,1_900_000_170_000))
        assertNull(q.current(181_000,1_900_000_181_000))
    }
    @Test fun oldEndedItemMustNotPrefetchForNewClip() {
        assertFalse(WallpaperQueue.shouldPrefetch(100_000, 100_000, "old", "new", true))
        assertFalse(WallpaperQueue.shouldPrefetch(100_000, 100_000, "new", "new", false))
    }
}
