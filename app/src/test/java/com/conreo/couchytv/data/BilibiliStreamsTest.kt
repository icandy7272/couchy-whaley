package com.conreo.couchytv.data

import org.junit.Assert.*
import org.junit.Test

class BilibiliStreamsTest {
    @Test fun choosesHighestAvailableCompatibleVideoOnlyWithoutTrustingRequestedQuality() {
        val json = """{"code":0,"data":{"quality":80,"dash":{"video":[
            {"codecs":"hev1.1.6.L120","height":1080,"baseUrl":"https://example.test/hevc"},
            {"codecs":"avc1.640028","height":1080,"baseUrl":"https://example.test/1080"},
            {"codecs":"avc1.64001F","height":480,"baseUrl":"https://example.test/480"},
            {"codecs":"avc1.64001E","height":360,"baseUrl":"https://example.test/360"}
        ]}}}"""
        assertEquals("https://example.test/480", BilibiliStreams.parse(json).url)
        assertEquals(480, BilibiliStreams.parse(json).height)
    }

    @Test fun acceptsPublicProgressive720Mp4() {
        val json = """{"code":0,"data":{"quality":64,"format":"mp4720","durl":[{"url":"https://example.test/video.mp4"}]}}"""
        val stream = BilibiliStreams.parse(json)
        assertEquals(720, stream.height)
        assertEquals("https://example.test/video.mp4", stream.url)
    }

    @Test(expected = IllegalStateException::class)
    fun rejectsProgressiveHttpAndUnknownFormat() {
        BilibiliStreams.parse("""{"code":0,"data":{"quality":64,"format":"mp4720","durl":[{"url":"http://example.test/video.mp4"}]}}""")
    }

    @Test(expected = IllegalStateException::class)
    fun loginOrApiFailureCannotBecomePlayableStream() {
        BilibiliStreams.parse("""{"code":-101,"message":"not logged in"}""")
    }

    @Test(expected = IllegalStateException::class)
    fun rejectsNonHttpUriReturnedAsVideo() {
        BilibiliStreams.parse("""{"code":0,"data":{"dash":{"video":[{"codecs":"avc1","height":480,"baseUrl":"file:///data/private"}]}}}""")
    }
}
