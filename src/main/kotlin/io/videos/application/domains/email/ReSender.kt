package io.videos.application.domains.email

import io.videos.application.Logging
import io.videos.application.Repository
import io.videos.application.cqrs.Commands
import io.videos.application.cqrs.issue
import io.videos.application.emptyRepository
import io.videos.application.logger
import org.axonframework.eventhandling.EventHandler
import org.slf4j.Logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ReSender(private val commands: Commands) : Logging {

    private val logger: Logger = logger()
    private val toRetry: Repository<Email> =
        emptyRepository()

    @Scheduled(fixedRate = 5000)
    fun resendAll() {
        toRetry.all().forEach(::resend)
    }

    private fun resend(email: Email) {
        if (email.retries > 5) {
            logRetriesExceeded(email)
            toRetry.delete(email.id)
        } else {
            logResending(email)
            doResend(email)
        }
    }

    private fun logRetriesExceeded(email: Email) {
        logger.warn("Retries exceeded for $email")
    }

    private fun logResending(email: Email) {
        logger.info("Resending $email")
    }

    private fun doResend(email: Email) {
        ReSendEmail(
            emailId = email.id,
            to = email.to,
            subject = email.subject,
            text = email.text,
            html = email.html
        ).issue(commands)
    }

    @EventHandler
    fun on(e: EmailSendFailed) {
        toRetry.add(e.toEmail().retried())
    }

    @EventHandler
    fun on(e: EmailSent) {
        toRetry.delete(e.emailId)
    }

    private fun EmailSendFailed.toEmail() = Email(
        id = emailId,
        to = to,
        subject = subject,
        text = text,
        html = html
    )
}