package io.videos.application.domains.identity

import io.videos.application.cqrs.Event
import io.videos.application.cqrs.EventName
import java.util.UUID

@EventName("UserRegistered")
class UserRegistered(
    val userId: UUID,
    val email: String,
    val passwordHash: String
) : Event

@EventName("RegistrationRejected")
class RegistrationRejected(
    val userId: UUID,
    val email: String,
    val passwordHash: String,
    val reason: String
) : Event

@EventName("LoginSucceeded")
class LoginSucceeded(
    val userId: UUID,
    val email: String
) : Event

@EventName("LoginFailed")
class LoginFailed(
    val userId: UUID? = null,
    val email: String,
    val reason: String
) : Event