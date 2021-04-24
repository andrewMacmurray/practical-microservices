package io.videos.application.domains.identity

import io.videos.application.Entity
import io.videos.application.Repository
import io.videos.application.cqrs.Queries
import io.videos.application.cqrs.Query
import io.videos.application.emptyRepository
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class RegisteredUsersProjection {

    private val users: Repository<RegisteredUser> =
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

@Component
class UsersRepository(private val queries: Queries) {

    private val users: Repository<RegisteredUser>
        get() = RegisteredUsersQuery
            .exec(queries)
            .repository

    fun emailTaken(email: String): Boolean =
        users.all().any { it.email == email }

    fun findByEmail(email: String): RegisteredUser? =
        users.find { it.email == email }

    fun find(id: UUID) =
        users.find(id)

    fun all(): List<RegisteredUser> =
        users.all()
}

class RegisteredUsers(val repository: Repository<RegisteredUser>)

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