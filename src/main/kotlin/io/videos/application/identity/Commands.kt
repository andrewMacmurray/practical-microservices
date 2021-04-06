package io.videos.application.identity

import io.videos.application.cqrs.Command
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class RegisterUser(
    val email: String,
    val passwordHash: String
) : Command {

    @TargetAggregateIdentifier
    val userId: UserId = UUID.randomUUID()
}
