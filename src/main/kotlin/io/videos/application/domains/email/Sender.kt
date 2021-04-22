package io.videos.application.domains.email

import io.videos.application.Logging
import io.videos.application.Repository
import io.videos.application.cqrs.Commands
import io.videos.application.cqrs.issue
import io.videos.application.emptyRepository
import io.videos.application.logger
import org.axonframework.eventhandling.EventHandler
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Component
class Sender(private val commands: Commands) : Logging {

    private val retries: Repository<Email> =
        emptyRepository()

    fun send(
        subject: String,
        body: String
    ) {
        SendEmail(
            emailId = UUID.randomUUID(),
            to = "a@b.com",
            subject = subject,
            text = body,
            html = body
        ).issue(commands)
    }

    @Scheduled(fixedRate = 5000)
    fun resendAll() {
        retries.all().forEach(::resend)
    }

    private fun resend(email: Email) {
        if (email.retries > 5) {
            logRetriesExceeded(email)
            retries.delete(email.id)
        } else {
            logResending(email)
            doResend(email)
        }
    }

    private fun logRetriesExceeded(email: Email) {
        logger().warn("Retries exceeded for $email")
    }

    private fun logResending(email: Email) {
        logger().info("Resending $email")
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
        retries.add(e.toEmail().retried())
    }

    @EventHandler
    fun on(e: EmailSent) {
        retries.delete(e.emailId)
    }

    private fun EmailSendFailed.toEmail() = Email(
        id = emailId,
        to = to,
        subject = subject,
        text = text,
        html = html
    )
}

@RestController
class EmailController(private val sender: Sender) {

    @GetMapping("/send-email/{subject}/{body}")
    fun sendEmail(
        @PathVariable("subject") subject: String,
        @PathVariable("body") body: String
    ) {
        sender.send(subject, body)
    }
}