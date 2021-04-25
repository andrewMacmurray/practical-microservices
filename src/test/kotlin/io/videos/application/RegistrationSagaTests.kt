package io.videos.application

import io.videos.application.cqrs.Commands
import io.videos.application.domains.email.SendEmail
import io.videos.application.domains.identity.RegistrationSaga
import io.videos.application.domains.identity.UserRegistered
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.test.matchers.Matchers
import org.axonframework.test.saga.SagaTestFixture
import org.junit.jupiter.api.Test
import java.util.UUID

class RegistrationSagaTests {

    @Test
    fun `test the saga`() {
        val userId = UUID.randomUUID()
        val userRegistered = UserRegistered(
            userId = userId,
            email = "a@b.com",
            passwordHash = "abc12345"
        )

        fixture()
            .givenAggregate(userId.toString())
            .published()
            .whenPublishingA(userRegistered)
            .expectDispatchedCommandsMatching(
                Matchers.listWithAnyOf(
                    Matchers.messageWithPayload(
                        Matchers.predicate<SendEmail> {
                            it.to == userRegistered.email && it.subject == "Welcome"
                        }
                    )
                )
            )
    }

    private fun fixture(): SagaTestFixture<RegistrationSaga> {
        val fixture = SagaTestFixture(RegistrationSaga::class.java)
        val gateway = fixture.registerCommandGateway(CommandGateway::class.java)
        fixture.registerResource(Commands(gateway))
        return fixture
    }
}