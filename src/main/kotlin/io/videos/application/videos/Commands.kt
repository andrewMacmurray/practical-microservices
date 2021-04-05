package io.videos.application.videos

import io.videos.application.cqrs.Command
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class UploadVideo(val name: String) : Command {
    @TargetAggregateIdentifier
    val videoId: VideoId = UUID.randomUUID()
}

data class RecordView(
    @TargetAggregateIdentifier
    val videoId: VideoId
) : Command
