package io.videos.application.home

import io.videos.application.cqrs.Query
import io.videos.application.videos.VideoId
import io.videos.application.videos.VideoUploaded
import io.videos.application.videos.VideoViewed
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Service

data class Video(
    val id: VideoId,
    val name: String
)

@Service
class HomeModelRepository {

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

object HomeModelQuery : Query
