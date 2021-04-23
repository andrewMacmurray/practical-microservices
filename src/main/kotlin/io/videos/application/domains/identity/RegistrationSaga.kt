package io.videos.application.domains.identity

import io.videos.application.Logging
import io.videos.application.cqrs.Commands
import io.videos.application.cqrs.issue
import io.videos.application.domains.email.EmailSent
import io.videos.application.domains.email.SendEmail
import io.videos.application.logger
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.slf4j.Logger
import java.util.UUID

@Saga
class RegistrationSaga : Logging {

    private var userId: UUID? = null
    private val logger: Logger = logger()

    @StartSaga
    @SagaEventHandler(associationProperty = "userId")
    fun handle(e: UserRegistered, commands: Commands) {
        this.userId = e.userId
        logger.info("Saga Started $e")

        val sendEmail = sendWelcomeEmail(e)

        SagaLifecycle.associateWith("emailId", "${sendEmail.emailId}")

        sendEmail.issue(commands)
    }

    private fun sendWelcomeEmail(e: UserRegistered) = SendEmail(
        to = e.email,
        subject = "Welcome",
        text = "Welcome",
        html = "<h1>Welcome</h1>"
    )

    @SagaEventHandler(associationProperty = "emailId")
    fun handle(e: EmailSent, commands: Commands) {
        confirmEmailSent(e).issue(commands)
    }

    private fun confirmEmailSent(e: EmailSent) = ConfirmEmailSent(
        userId = userId!!,
        emailId = e.emailId
    )

    @EndSaga
    @SagaEventHandler(associationProperty = "userId")
    fun handle(e: RegistrationEmailSent) {
        logger.info("Saga Completed")
    }
}