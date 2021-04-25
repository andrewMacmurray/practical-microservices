package io.videos.application

import io.videos.application.domains.email.Email
import io.videos.application.domains.email.EmailAggregate
import io.videos.application.domains.email.EmailSendFailed
import io.videos.application.domains.email.EmailSendRetried
import io.videos.application.domains.email.EmailSent
import io.videos.application.domains.email.Mailer
import io.videos.application.domains.email.SendEmail
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.Test

class EmailAggregateTests {

    @Test
    fun `sends an email`() {
        val sendEmail = sendEmail()

        fixture()
            .given()
            .`when`(sendEmail)
            .expectEvents(
                emailSent(sendEmail)
            )
    }

    @Test
    fun `retries sending an email after failure`() {
        val sendEmail = sendEmail()

        fixture(failNTimes = 2)
            .given()
            .`when`(sendEmail)
            .expectEvents(
                emailSent(sendEmail),
                emailSendRetried(2, sendEmail),
                emailSendRetried(1, sendEmail),
            )
    }

    @Test
    fun `retries until limit reached`() {
        val sendEmail = sendEmail()

        fixture(failNTimes = 6)
            .given()
            .`when`(sendEmail)
            .expectEvents(
                emailFailed(sendEmail),
                emailSendRetried(5, sendEmail),
                emailSendRetried(4, sendEmail),
                emailSendRetried(3, sendEmail),
                emailSendRetried(2, sendEmail),
                emailSendRetried(1, sendEmail),
            )
    }

    private fun emailFailed(sendEmail: SendEmail) = EmailSendFailed(
        emailId = sendEmail.emailId,
        to = sendEmail.to,
        subject = sendEmail.subject,
        text = sendEmail.text,
        html = sendEmail.html,
        reason = "Retry Limit reached: fake fail"
    )

    private fun emailSent(sendEmail: SendEmail) = EmailSent(
        emailId = sendEmail.emailId,
        to = sendEmail.to,
        subject = sendEmail.subject,
        text = sendEmail.text,
        html = sendEmail.html
    )

    private fun emailSendRetried(times: Int, sendEmail: SendEmail) = EmailSendRetried(
        emailId = sendEmail.emailId,
        to = sendEmail.to,
        subject = sendEmail.subject,
        text = sendEmail.text,
        html = sendEmail.html,
        tries = times
    )

    private fun sendEmail() = SendEmail(
        to = "to",
        subject = "subject",
        text = "text",
        html = "html"
    )

    private fun fixture(failNTimes: Int = 0) =
        AggregateTestFixture(EmailAggregate::class.java)
            .registerInjectableResource(MailerStub(failNTimes))
}

private class MailerStub(var failNTimes: Int) : Mailer {
    override fun send(
        email: Email,
        onSuccess: (Email) -> Unit,
        onFailure: (Mailer.Failure) -> Unit
    ) {
        if (failNTimes > 0) {
            failNTimes--
            onFailure(
                Mailer.Failure(
                    reason = "fake fail",
                    email = email.incrementRetries()
                )
            )
        } else {
            onSuccess(email)
        }
    }

    override fun pause() {}
}