package io.videos.application

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.TargetAggregateIdentifier
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.stereotype.Service
import java.util.UUID

typealias VideoId = UUID

data class UploadVideo(val name: String) : Command {
    @TargetAggregateIdentifier
    val videoId: VideoId = UUID.randomUUID()
}

data class VideoUploaded(
    val videoId: VideoId,
    val name: String
) : Event

data class ViewVideo(
    @TargetAggregateIdentifier
    val videoId: VideoId
) : Command

data class VideoViewed(
    val videoId: VideoId
) : Event

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
    fun handle(cmd: ViewVideo) {
        AggregateLifecycle.apply(VideoViewed(cmd.videoId))
    }

    @EventSourcingHandler
    fun on(e: VideoUploaded) {
        videoId = e.videoId
    }
}

data class Video(
    val id: VideoId,
    val name: String
)

@Service
class VideoViews {

    private var viewCount: Int = 0
    private val videos: MutableMap<VideoId, Video> = mutableMapOf()

    @EventHandler
    fun on(e: VideoViewed) {
        viewCount++
    }

    @EventHandler
    fun on(e: VideoUploaded) {
        videos[e.videoId] = Video(e.videoId, e.name)
    }

    @QueryHandler
    fun handle(q: HomeModelQuery) = HomeModel(
        videosWatched = viewCount,
        videos = videos.values.toList()
    )
}
