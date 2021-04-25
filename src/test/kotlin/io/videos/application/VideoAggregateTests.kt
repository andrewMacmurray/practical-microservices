package io.videos.application

import io.videos.application.domains.videos.NameVideo
import io.videos.application.domains.videos.PublishVideo
import io.videos.application.domains.videos.Transcoder
import io.videos.application.domains.videos.Video
import io.videos.application.domains.videos.VideoAggregate
import io.videos.application.domains.videos.VideoNamed
import io.videos.application.domains.videos.VideoPublished
import io.videos.application.domains.videos.VideoPublishingFailed
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.Test
import java.util.UUID

class VideoAggregateTests {

    @Test
    fun `publishes a video run through a transcoder`() {
        val publish = publishVideo()

        fixture()
            .given()
            .`when`(publish)
            .expectSuccessfulHandlerExecution()
            .expectEvents(
                VideoPublished(
                    videoId = publish.videoId,
                    ownerId = publish.ownerId,
                    sourceUri = publish.sourceUri,
                    transcodedUri = "transcoded:${publish.sourceUri}"
                )
            )
    }

    @Test
    fun `signals failure if transcoding fails`() {
        val publish = publishVideo()

        fixture(forceFailure = true)
            .given()
            .`when`(publish)
            .expectEvents(
                VideoPublishingFailed(
                    videoId = publish.videoId,
                    ownerId = publish.ownerId,
                    sourceUri = publish.sourceUri,
                    reason = "transcoding failed"
                )
            )
    }

    @Test
    fun `names an uploaded video`() {
        val publish = publishVideo()
        val nameVideo = NameVideo(
            videoId = publish.videoId,
            name = "bread video"
        )

        fixture()
            .givenCommands(publish)
            .`when`(nameVideo)
            .expectEvents(
                VideoNamed(
                    videoId = nameVideo.videoId,
                    name = nameVideo.name
                )
            )
    }

    private fun publishVideo() = PublishVideo(
        ownerId = UUID.randomUUID(),
        sourceUri = "uri"
    )

    private fun fixture(forceFailure: Boolean = false) =
        AggregateTestFixture(VideoAggregate::class.java)
            .registerInjectableResource(TranscoderStub(forceFailure))
}

private class TranscoderStub(val forceFailure: Boolean) : Transcoder {
    override fun transcode(
        video: Video,
        onSuccess: (Video.Transcoded) -> Unit,
        onFailure: (reason: String, video: Video) -> Unit
    ) {
        if (forceFailure) {
            onFailure("transcoding failed", video)
        } else {
            onSuccess(
                Video.Transcoded(
                    id = video.id,
                    ownerId = video.ownerId,
                    sourceUri = video.sourceUri,
                    transcodedUri = "transcoded:${video.sourceUri}"
                )
            )
        }
    }
}