package io.videos.application.domains.videos

import io.videos.application.cqrs.Event
import io.videos.application.cqrs.EventName
import java.util.UUID

@EventName("VideoPublished")
class VideoPublished(
    val videoId: UUID,
    val ownerId: UUID,
    val sourceUri: String,
    val transcodedUri: String
) : Event

@EventName("VideoPublishingFailed")
class VideoPublishingFailed(
    val videoId: UUID,
    val reason: String,
    val ownerId: UUID,
    val sourceUri: String
) : Event

@EventName("VideoViewed")
class VideoViewed(
    val videoId: UUID
) : Event

@EventName("VideoNamed")
class VideoNamed(
    val videoId: UUID,
    val name: String
) : Event
