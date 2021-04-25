package io.videos.application.domains.identity

import io.videos.application.Logging
import io.videos.application.cqrs.Commands
import io.videos.application.cqrs.issue
import io.videos.application.domains.email.EmailSendFailed
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
        associate("emailId", sendEmail.emailId)
        sendEmail.issue(commands)
    }

    @SagaEventHandler(associationProperty = "emailId")
    fun handle(e: EmailSent, commands: Commands) {
        confirmEmailSent(e).issue(commands)
    }

    @SagaEventHandler(associationProperty = "emailId")
    fun handle(e: EmailSendFailed, commands: Commands) {
        confirmEmailFailed(e).issue(commands)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "userId")
    fun handle(e: RegistrationEmailSent) {
        logger.info("Saga Completed: registration completed")
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "userId")
    fun handle(e: RegistrationEmailSendFailed) {
        logger.info("Saga Completed: registration failed")
    }

    private fun sendWelcomeEmail(e: UserRegistered) = SendEmail(
        to = e.email,
        subject = "Welcome",
        text = "Welcome",
        html = "<h1>Welcome</h1>"
    )

    private fun confirmEmailSent(e: EmailSent) = ConfirmRegistrationEmailSent(
        userId = userId!!,
        emailId = e.emailId
    )

    private fun confirmEmailFailed(e: EmailSendFailed) = ConfirmRegistrationEmailFailed(
        userId = userId!!,
        emailId = e.emailId
    )

    private fun associate(key: String, id: UUID) {
        SagaLifecycle.associateWith(key, id.toString())
    }
}