package io.videos.application.identity

import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Service

@Service
class RegisteredUsers {

    private val users: MutableMap<UserId, User> =
        mutableMapOf()

    @EventHandler
    fun on(e: UserRegistered) {
        users[e.userId] = e.toUser()
        println(users.values.toList())
    }

    fun emailTaken(email: String): Boolean =
        users.filterValues { it.email == email }.isNotEmpty()
}

private fun UserRegistered.toUser() = User(
    id = userId,
    email = email,
    passwordHash = passwordHash
)

data class User(
    val id: UserId,
    val email: String,
    val passwordHash: String
)