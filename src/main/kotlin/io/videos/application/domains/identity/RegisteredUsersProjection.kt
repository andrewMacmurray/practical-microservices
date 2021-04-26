package io.videos.application.domains.identity

import io.videos.application.Entity
import io.videos.application.InMemoryRepository
import io.videos.application.cqrs.Queries
import io.videos.application.cqrs.Query
import io.videos.application.emptyRepository
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class RegisteredUsersProjection {

    private val users: InMemoryRepository<RegisteredUser> =
        emptyRepository()

    @EventHandler
    fun on(e: UserRegistered) {
        users.add(e.toUser())
    }

    @EventHandler
    fun on(e: RegistrationEmailSendFailed) {
        users.delete(e.userId)
    }

    @QueryHandler
    fun handle(q: RegisteredUsersQuery) =
        RegisteredUsers(users)
}

interface UsersRepository {
    fun exists(email: String): Boolean
}

@Component
class InMemoryUsers(private val queries: Queries) : UsersRepository {

    private val users: InMemoryRepository<RegisteredUser>
        get() = RegisteredUsersQuery
            .exec(queries)
            .repository

    override fun exists(email: String): Boolean =
        users.all().any { it.email == email }

    fun findByEmail(email: String): RegisteredUser? =
        users.find { it.email == email }

    fun find(id: UUID) =
        users.find(id)

    fun all(): List<RegisteredUser> =
        users.all()
}

class RegisteredUsers(val repository: InMemoryRepository<RegisteredUser>)

object RegisteredUsersQuery : Query<RegisteredUsers> {
    override val type = RegisteredUsers::class
}

private fun UserRegistered.toUser() = RegisteredUser(
    id = userId,
    email = email,
    passwordHash = passwordHash
)

data class RegisteredUser(
    override val id: UUID,
    val email: String,
    val passwordHash: String
) : Entity