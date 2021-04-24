package io.videos.application.pages.home

import io.videos.application.cqrs.Commands
import io.videos.application.cqrs.Queries
import io.videos.application.domains.identity.RegisteredUser
import io.videos.application.domains.videos.PublishVideo
import io.videos.application.domains.videos.RecordView
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class HomeService(
    private val commands: Commands,
    private val queries: Queries
) {

    fun uploadVideo(upload: Upload, user: RegisteredUser) {
        commands.issue(
            PublishVideo(
                sourceUri = upload.uri,
                ownerId = user.id
            )
        )
    }

    fun recordView(id: UUID) {
        commands.issue(RecordView(id))
    }

    fun model(): HomeModel =
        queries.get(HomeModelQuery)
}

class HomeModel(
    val videosWatched: Int = 0,
    val videos: List<Video>,
    val users: List<RegisteredUser>
)

