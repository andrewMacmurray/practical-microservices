package io.videos.application.pages.home

import io.videos.application.Entity
import io.videos.application.Repository
import io.videos.application.cqrs.Query
import io.videos.application.domains.identity.UsersRepository
import io.videos.application.domains.videos.VideoPublished
import io.videos.application.domains.videos.VideoViewed
import io.videos.application.emptyRepository
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import java.util.UUID

class Video(
    override val id: UUID,
    val uri: String
) : Entity

@Component
class HomeProjection(private val users: UsersRepository) {

    private var viewCount: Int = 0
    private val videos: Repository<Video> = emptyRepository()

    @EventHandler
    fun on(e: VideoViewed) {
        viewCount++
    }

    @EventHandler
    fun on(e: VideoPublished) {
        videos.add(Video(e.videoId, e.transcodedUri))
    }

    @QueryHandler
    fun handle(q: HomeModelQuery) = HomeModel(
        videosWatched = viewCount,
        videos = videos.all(),
        users = users.all()
    )
}

object HomeModelQuery : Query<HomeModel> {
    override val type = HomeModel::class
}
