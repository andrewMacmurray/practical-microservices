package io.videos.application.videos

import io.videos.application.cqrs.Event

data class VideoUploaded(
    val videoId: VideoId,
    val name: String
) : Event

data class VideoViewed(
    val videoId: VideoId
) : Event


