package io.videos.application.domains.videos

import io.videos.application.cqrs.Event
import io.videos.application.cqrs.EventName
import java.util.UUID

@EventName("VideoUploaded")
data class VideoUploaded(
    val videoId: UUID,
    val name: String
) : Event

@EventName("VideoViewed")
data class VideoViewed(
    val videoId: UUID
) : Event


