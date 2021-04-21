package io.videos.application.domains.identity

import io.videos.application.cqrs.Queries
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.util.UUID

@Aggregate
class UsersAggregate {

    @AggregateIdentifier
    private var userId: UUID? = null

    constructor()

    @CommandHandler
    constructor(cmd: RegisterUser, queries: Queries) {
        if (queries.emailTaken(cmd.email)) {
            reject(cmd)
        } else {
            register(cmd)
        }
    }

    @CommandHandler
    fun handle(cmd: LoginUser) {
        AggregateLifecycle.apply(
            LoginSucceeded(
                userId = userId!!,
                email = cmd.email
            )
        )
    }

    private fun Queries.emailTaken(email: String): Boolean =
        RegisteredUsersQuery.exec(this).emailTaken(email)

    private fun register(cmd: RegisterUser) {
        AggregateLifecycle.apply(
            UserRegistered(
                userId = cmd.userId,
                email = cmd.email,
                passwordHash = cmd.passwordHash
            )
        )
    }

    private fun reject(cmd: RegisterUser) {
        AggregateLifecycle.apply(
            RegistrationRejected(
                userId = cmd.userId,
                email = cmd.email,
                passwordHash = cmd.passwordHash,
                reason = "email taken"
            )
        )
    }

    @EventSourcingHandler
    fun on(e: UserRegistered) {
        this.userId = e.userId
    }

    @EventSourcingHandler
    fun on(e: RegistrationRejected) {
        this.userId = e.userId
    }
}