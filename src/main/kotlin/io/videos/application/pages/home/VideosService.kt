package io.videos.application.pages.home

import io.videos.application.cqrs.Commands
import io.videos.application.domains.identity.RegisteredUser
import io.videos.application.domains.videos.NameVideo
import io.videos.application.domains.videos.PublishVideo
import io.videos.application.domains.videos.RecordView
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class VideosService(private val commands: Commands) {

    fun upload(upload: Upload, user: RegisteredUser) {
        commands.issue(
            PublishVideo(
                sourceUri = upload.uri,
                ownerId = user.id
            )
        )
    }

    fun recordView(id: UUID) {
        commands.issue(
            RecordView(
                videoId = id
            )
        )
    }

    fun name(id: UUID, name: Name) {
        commands.issue(
            NameVideo(
                videoId = id,
                name = name.name
            )
        )
    }
}