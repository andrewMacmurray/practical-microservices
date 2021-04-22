package io.videos.application.domains.email

import io.videos.application.cqrs.Command
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class SendEmail(
    @TargetAggregateIdentifier
    val emailId: UUID,
    val to: String,
    val subject: String,
    val text: String,
    val html: String
) : Command

data class ReSendEmail(
    @TargetAggregateIdentifier
    val emailId: UUID,
    val to: String,
    val subject: String,
    val text: String,
    val html: String
) : Command