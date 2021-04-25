package io.videos.application.domains.videos

import io.videos.application.Logging
import io.videos.application.logger
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import org.slf4j.Logger
import java.util.UUID

@Aggregate
class VideoAggregate : Logging {

    @AggregateIdentifier
    private lateinit var videoId: UUID
    private val logger: Logger = logger()

    constructor()

    @CommandHandler
    constructor(cmd: PublishVideo, transcoder: Transcoder) {
        logger.info("transcoding video: $cmd")
        transcoder.transcode(
            video = cmd.toVideo(),
            onSuccess = ::videoTranscoded,
            onFailure = ::transcodingFailed
        )
    }

    @CommandHandler
    fun handle(cmd: NameVideo) {
        AggregateLifecycle.apply(
            VideoNamed(
                videoId = cmd.videoId,
                name = cmd.name
            )
        )
    }

    private fun transcodingFailed(reason: String, video: Video) {
        logger.warn("transcoding failed: $reason: $video")
        AggregateLifecycle.apply(
            VideoPublishingFailed(
                videoId = video.id,
                reason = reason,
                ownerId = video.ownerId,
                sourceUri = video.sourceUri
            )
        )
    }

    private fun videoTranscoded(video: Video.Transcoded) {
        logger.info("video transcoded $video")
        AggregateLifecycle.apply(
            VideoPublished(
                videoId = video.id,
                ownerId = video.ownerId,
                sourceUri = video.sourceUri,
                transcodedUri = video.transcodedUri
            )
        )
    }

    @CommandHandler
    fun handle(cmd: RecordView) {
        AggregateLifecycle.apply(VideoViewed(cmd.videoId))
    }

    @EventSourcingHandler
    fun on(e: VideoPublished) {
        videoId = e.videoId
    }

    @EventSourcingHandler
    fun on(e: VideoPublishingFailed) {
        videoId = e.videoId
    }

    private fun PublishVideo.toVideo() = Video(
        id = videoId,
        ownerId = ownerId,
        sourceUri = sourceUri
    )
}

