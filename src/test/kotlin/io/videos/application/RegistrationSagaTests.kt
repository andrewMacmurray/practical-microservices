package io.videos.application

import io.videos.application.cqrs.Commands
import io.videos.application.domains.email.EmailSendFailed
import io.videos.application.domains.email.EmailSent
import io.videos.application.domains.email.SendEmail
import io.videos.application.domains.identity.ConfirmRegistrationEmailFailed
import io.videos.application.domains.identity.ConfirmRegistrationEmailSent
import io.videos.application.domains.identity.IdGenerator
import io.videos.application.domains.identity.RegistrationSaga
import io.videos.application.domains.identity.UserRegistered
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.test.matchers.Matchers
import org.axonframework.test.saga.SagaTestFixture
import org.junit.jupiter.api.Test
import java.util.UUID

class RegistrationSagaTests {

    @Test
    fun `sends a welcome email command on UserRegistered`() {
        val userId = UUID.randomUUID()
        val userRegistered = userRegistered(userId)

        fixture()
            .givenAggregate(userId.toString())
            .published()
            .whenPublishingA(userRegistered)
            .expectDispatchedCommandsMatching(
                payloadWhere<SendEmail> {
                    it.to == userRegistered.email && it.subject == "Welcome"
                }
            )
    }

    @Test
    fun `Confirms welcome email was sent`() {
        val ids = ConstantIds()
        val userId = UUID.randomUUID()
        val userRegistered = userRegistered(userId)
        val emailSent = emailSent(ids, userRegistered)

        fixture(idGenerator = ids)
            .givenAggregate(userId.toString())
            .published(userRegistered)
            .whenPublishingA(emailSent)
            .expectDispatchedCommandsMatching(
                payloadWhere<ConfirmRegistrationEmailSent> {
                    it.userId == userId && it.emailId == emailSent.emailId
                }
            )
    }

    @Test
    fun `Confirms welcome email failed to send`() {
        val ids = ConstantIds()
        val userId = UUID.randomUUID()
        val userRegistered = userRegistered(userId)
        val emailFailed = emailSendFailed(ids, userRegistered)

        fixture(idGenerator = ids)
            .givenAggregate(userId.toString())
            .published(userRegistered)
            .whenPublishingA(emailFailed)
            .expectDispatchedCommandsMatching(
                payloadWhere<ConfirmRegistrationEmailFailed> {
                    it.userId == userId && it.emailId == emailFailed.emailId
                }
            )
    }

    private fun emailSent(
        ids: ConstantIds,
        userRegistered: UserRegistered
    ) = EmailSent(
        emailId = ids.current,
        to = userRegistered.email,
        subject = "Welcome",
        text = "Welcome",
        html = "Welcome"
    )

    private fun emailSendFailed(
        ids: ConstantIds,
        userRegistered: UserRegistered
    ) = EmailSendFailed(
        emailId = ids.current,
        to = userRegistered.email,
        subject = "Welcome",
        text = "Welcome",
        html = "Welcome",
        reason = "something went wrong"
    )

    private fun userRegistered(userId: UUID) = UserRegistered(
        userId = userId,
        email = "a@b.com",
        passwordHash = "abc12345"
    )

    private fun fixture(idGenerator: ConstantIds = ConstantIds()): SagaTestFixture<RegistrationSaga> {
        val fixture = SagaTestFixture(RegistrationSaga::class.java)
        val gateway = fixture.registerCommandGateway(CommandGateway::class.java)
        fixture.registerResource(Commands(gateway))
        fixture.registerResource(idGenerator)
        return fixture
    }

    private fun <T> payloadWhere(fn: (T) -> Boolean) =
        Matchers.listWithAllOf(
            Matchers.messageWithPayload(
                Matchers.predicate(fn)
            )
        )

    private class ConstantIds : IdGenerator {
        val current: UUID =
            UUID.randomUUID()

        override fun generate(): UUID =
            current
    }
}