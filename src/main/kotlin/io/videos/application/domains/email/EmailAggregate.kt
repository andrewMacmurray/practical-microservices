package io.videos.application.domains.email

import io.videos.application.Logging
import io.videos.application.logger
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.util.UUID

@Aggregate
class EmailAggregate : Logging {

    @AggregateIdentifier
    private var emailId: UUID? = null

    constructor()

    @CommandHandler
    constructor(cmd: SendEmail, mailer: Mailer) {
        doSend(cmd.toEmail(), mailer)
    }

    @CommandHandler
    fun handle(cmd: ReSendEmail, mailer: Mailer) {
        doSend(cmd.toEmail(), mailer)
    }

    private fun doSend(email: Email, mailer: Mailer) {
        mailer.send(
            email = email,
            onSuccess = { emailSuccess(email) },
            onFailure = { emailFailure(it, email) }
        )
    }

    private fun emailFailure(reason: String, email: Email) {
        logFailure(email, reason)
        AggregateLifecycle.apply(
            EmailSendFailed(
                emailId = email.id,
                reason = reason,
                to = email.to,
                subject = email.subject,
                text = email.text,
                html = email.html
            )
        )
    }

    private fun emailSuccess(email: Email) {
        logSuccess(email)
        AggregateLifecycle.apply(
            EmailSent(
                emailId = email.id,
                to = email.to,
                subject = email.subject,
                text = email.text,
                html = email.html
            )
        )
    }

    private fun logSuccess(email: Email) {
        logger().info("Email Sent $email")
    }

    private fun logFailure(email: Email, reason: String) {
        logger().warn("Send failed for ${email.id}: $reason")
    }

    @EventSourcingHandler
    fun on(e: EmailSent) {
        emailId = e.emailId
    }

    @EventSourcingHandler
    fun on(e: EmailSendFailed) {
        emailId = e.emailId
    }
}

private fun SendEmail.toEmail() = Email(
    id = emailId,
    to = to,
    subject = subject,
    text = text,
    html = html
)

private fun ReSendEmail.toEmail() = Email(
    id = emailId,
    to = to,
    subject = subject,
    text = text,
    html = html
)
