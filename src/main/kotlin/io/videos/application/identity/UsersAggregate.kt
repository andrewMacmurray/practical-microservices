package io.videos.application.identity

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.util.UUID

typealias UserId = UUID

@Aggregate
class UsersAggregate {

    @AggregateIdentifier
    private var userId: UserId? = null

    constructor()

    @CommandHandler
    constructor(cmd: RegisterUser, users: RegisteredUsers) {
        if (users.emailTaken(cmd.email)) {
            reject(cmd)
        } else {
            register(cmd)
        }
    }

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