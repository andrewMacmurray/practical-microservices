package io.videos.application.domains.identity

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.util.UUID

@Aggregate
class UserAggregate {

    @AggregateIdentifier
    private lateinit var userId: UUID

    constructor()

    @CommandHandler
    constructor(cmd: RegisterUser, users: UsersRepository) {
        if (users.exists(cmd.email)) {
            rejectedEvent(cmd)
        } else {
            register(cmd)
        }
    }

    @CommandHandler
    fun handle(cmd: SignalLoginSuccess) {
        AggregateLifecycle.apply(
            LoginSucceeded(
                userId = userId,
                email = cmd.email
            )
        )
    }

    @CommandHandler
    fun handle(cmd: ConfirmRegistrationEmailSent) {
        AggregateLifecycle.apply(
            RegistrationEmailSent(
                userId = cmd.userId,
                emailId = cmd.emailId
            )
        )
    }

    @CommandHandler
    fun handle(cmd: ConfirmRegistrationEmailFailed) {
        AggregateLifecycle.apply(
            RegistrationEmailSendFailed(
                userId = cmd.userId,
                emailId = cmd.emailId
            )
        )
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

    private fun rejectedEvent(cmd: RegisterUser) {
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