package io.videos.application.identity

import io.videos.application.cqrs.Event

data class UserRegistered(
    val userId: UserId,
    val email: String,
    val passwordHash: String
) : Event

data class RegistrationRejected(
    val userId: UserId,
    val email: String,
    val passwordHash: String,
    val reason: String
) : Event