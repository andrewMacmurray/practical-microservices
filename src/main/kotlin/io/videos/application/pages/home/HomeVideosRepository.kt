package io.videos.application.pages.home

import io.videos.application.Entity
import io.videos.application.Repository
import io.videos.application.cqrs.Query
import io.videos.application.domains.videos.VideoUploaded
import io.videos.application.domains.videos.VideoViewed
import io.videos.application.emptyRepository
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Service
import java.util.UUID

data class Video(
    override val id: UUID,
    val name: String
) : Entity

@Service
class HomeModelRepository {

    private var viewCount: Int = 0
    private val videos: Repository<Video> = emptyRepository()

    @EventHandler
    fun on(e: VideoViewed) {
        viewCount++
    }

    @EventHandler
    fun on(e: VideoUploaded) {
        videos.add(Video(e.videoId, e.name))
    }

    @QueryHandler
    fun handle(q: HomeModelQuery) = HomeModel(
        videosWatched = viewCount,
        videos = videos.all()
    )
}

object HomeModelQuery : Query<HomeModel> {
    override val type = HomeModel::class
}
