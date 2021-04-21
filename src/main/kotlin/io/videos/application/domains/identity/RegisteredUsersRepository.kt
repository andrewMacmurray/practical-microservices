package io.videos.application.domains.identity

import io.videos.application.Entity
import io.videos.application.Repository
import io.videos.application.cqrs.Queries
import io.videos.application.cqrs.Query
import io.videos.application.emptyRepository
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RegisteredUsersRepository {

    private val users: Repository<User> =
        emptyRepository()

    @EventHandler
    fun on(e: UserRegistered) {
        users.add(e.toUser())
    }

    @QueryHandler
    fun handle(q: RegisteredUsersQuery) =
        RegisteredUsers(users)
}

class RegisteredUsers(private val users: Repository<User>) {

    fun emailTaken(email: String): Boolean =
        users.all().any { it.email == email }

    fun findByEmail(email: String): User? =
        users.all().find { it.email == email }
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
    override val id: UUID,
    val email: String,
    val passwordHash: String
) : Entity