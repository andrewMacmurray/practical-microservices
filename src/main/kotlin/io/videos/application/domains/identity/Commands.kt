package io.videos.application.domains.identity

import io.videos.application.cqrs.Command
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

class RegisterUser(
    val email: String,
    val passwordHash: String
) : Command {

    @TargetAggregateIdentifier
    val userId: UUID = UUID.randomUUID()
}

class LoginUser(
    @TargetAggregateIdentifier
    val userId: UUID,
    val email: String
) : Command

class ConfirmEmailSent(
    @TargetAggregateIdentifier
    val userId: UUID,
    val emailId: UUID
) : Command