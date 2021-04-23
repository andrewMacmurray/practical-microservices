package io.videos.application.domains.email

import io.videos.application.Logging
import io.videos.application.logger
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import org.slf4j.Logger
import java.util.UUID

@Aggregate
class EmailAggregate : Logging {

    @AggregateIdentifier
    private var emailId: UUID? = null
    private var mailer: Mailer? = null
    private val logger: Logger = logger()

    constructor()

    @CommandHandler
    constructor(cmd: SendEmail, mailer: Mailer) {
        this.mailer = mailer
        doSend(cmd.toEmail())
    }

    private fun doSend(email: Email) {
        mailer().send(
            email = email,
            onSuccess = ::emailSuccess,
            onFailure = ::emailFailure
        )
    }

    private fun emailFailure(reason: String, email: Email) {
        if (email.retryLimitReached()) {
            logFailure(email, reason)
            AggregateLifecycle.apply(
                EmailSendFailed(
                    emailId = email.id,
                    reason = "Retry Limit reached: $reason",
                    to = email.to,
                    subject = email.subject,
                    text = email.text,
                    html = email.html
                )
            )
        } else {
            logRetry(email)
            Thread.sleep(5000)
            doSend(email)
            AggregateLifecycle.apply(
                EmailSendRetried(
                    emailId = email.id,
                    tries = email.retries,
                    to = email.to,
                    subject = email.subject,
                    text = email.text,
                    html = email.html
                )
            )
        }
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
        logger.info("Email Sent $email")
    }

    private fun logFailure(email: Email, reason: String) {
        logger.warn("Send failed for ${email.id}: $reason")
    }

    private fun logRetry(email: Email) {
        logger.warn("Retrying send: $email")
    }

    @EventSourcingHandler
    fun on(e: EmailSent) {
        emailId = e.emailId
    }

    @EventSourcingHandler
    fun on(e: EmailSendFailed) {
        emailId = e.emailId
    }

    private fun mailer(): Mailer =
        mailer!!
}

private fun SendEmail.toEmail() = Email(
    id = emailId,
    to = to,
    subject = subject,
    text = text,
    html = html
)