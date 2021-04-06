package io.videos.application.identity

import io.videos.application.cqrs.Query
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Service

@Service
class RegisteredUsersRepository {

    private val users: MutableMap<UserId, User> =
        mutableMapOf()

    @EventHandler
    fun on(e: UserRegistered) {
        users[e.userId] = e.toUser()
    }

    @QueryHandler
    fun handle(q: RegisteredUsersQuery) =
        RegisteredUsers(users)
}

class RegisteredUsers(private val users: MutableMap<UserId, User>) {

    fun emailTaken(email: String): Boolean =
        users.filterValues { it.email == email }.isNotEmpty()
}

object RegisteredUsersQuery : Query<RegisteredUsers> {
    override val type = RegisteredUsers::class
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