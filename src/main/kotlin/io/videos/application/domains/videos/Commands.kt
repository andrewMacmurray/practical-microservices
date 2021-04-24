package io.videos.application.domains.videos

import io.videos.application.cqrs.Command
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

class RecordView(
    @TargetAggregateIdentifier
    val videoId: UUID
) : Command

class PublishVideo(
    @TargetAggregateIdentifier
    val videoId: UUID = UUID.randomUUID(),
    val ownerId: UUID,
    val sourceUri: String
) : Command

class NameVideo(
    @TargetAggregateIdentifier
    val videoId: UUID,
    val name: String
) : Command