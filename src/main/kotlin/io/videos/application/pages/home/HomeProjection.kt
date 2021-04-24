package io.videos.application.pages.home

import io.videos.application.Entity
import io.videos.application.Repository
import io.videos.application.cqrs.Query
import io.videos.application.domains.identity.RegisteredUser
import io.videos.application.domains.identity.UsersRepository
import io.videos.application.domains.videos.VideoNamed
import io.videos.application.domains.videos.VideoPublished
import io.videos.application.domains.videos.VideoViewed
import io.videos.application.emptyRepository
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import java.util.UUID

data class Video(
    override val id: UUID,
    val uri: String,
    val name: String? = null,
    val user: RegisteredUser,
    val views: Int = 0
) : Entity {
    fun incrementViews(): Video =
        this.copy(views = views + 1)

    fun rename(name: String): Video =
        this.copy(name = name)
}

@Component
class HomeProjection(private val users: UsersRepository) {

    private val videos: Repository<Video> =
        emptyRepository()

    @EventHandler
    fun on(e: VideoViewed) {
        videos.update(e.videoId) { it.incrementViews() }
    }

    @EventHandler
    fun on(e: VideoPublished) {
        videos.add(
            Video(
                id = e.videoId,
                uri = e.transcodedUri,
                user = users.find(e.ownerId)!!
            )
        )
    }

    @EventHandler
    fun on(e: VideoNamed) {
        videos.update(e.videoId) { it.rename(e.name) }
    }

    private fun viewCount() =
        videos.all().sumOf { it.views }

    @QueryHandler
    fun handle(q: HomeModelQuery) = HomeModel(
        totalViews = viewCount(),
        videos = videos.all(),
        users = users.all()
    )
}

object HomeModelQuery : Query<HomeModel> {
    override val type = HomeModel::class
}
