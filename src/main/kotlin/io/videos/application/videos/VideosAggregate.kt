package io.videos.application.videos

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.util.UUID

typealias VideoId = UUID

@Aggregate
class VideosAggregate {

    @AggregateIdentifier
    private var videoId: VideoId? = null

    constructor()

    @CommandHandler
    constructor(cmd: UploadVideo) {
        AggregateLifecycle.apply(VideoUploaded(cmd.videoId, cmd.name))
    }

    @CommandHandler
    fun handle(cmd: RecordView) {
        AggregateLifecycle.apply(VideoViewed(cmd.videoId))
    }

    @EventSourcingHandler
    fun on(e: VideoUploaded) {
        videoId = e.videoId
    }
}

