package io.videos.application

import io.videos.application.domains.identity.LoginFailed
import io.videos.application.domains.identity.LoginSucceeded
import io.videos.application.domains.identity.LoginUser
import io.videos.application.domains.identity.RegisterUser
import io.videos.application.domains.identity.RegistrationRejected
import io.videos.application.domains.identity.UserAggregate
import io.videos.application.domains.identity.UserRegistered
import io.videos.application.domains.identity.UsersRepository
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.jupiter.api.Test

class UsersAggregateTest {

    @Test
    fun `registers a user`() {
        val register = registerUser()

        fixture()
            .given()
            .`when`(register)
            .expectSuccessfulHandlerExecution()
            .expectEvents(
                UserRegistered(
                    userId = register.userId,
                    email = register.email,
                    passwordHash = register.passwordHash
                )
            )
    }

    @Test
    fun `rejects registration if email taken`() {
        val register = registerUser()

        fixture(registeredEmails = listOf(register.email))
            .given()
            .`when`(register)
            .expectSuccessfulHandlerExecution()
            .expectEvents(
                RegistrationRejected(
                    userId = register.userId,
                    email = register.email,
                    passwordHash = register.passwordHash,
                    reason = "email taken"
                )
            )
    }

    @Test
    fun `logs in an existing user`() {
        val register = registerUser()

        val login = LoginUser(
            userId = register.userId,
            email = register.email
        )

        fixture(registeredEmails = listOf(register.email))
            .givenCommands(register)
            .`when`(login)
            .expectSuccessfulHandlerExecution()
            .expectEvents(
                LoginSucceeded(
                    userId = login.userId,
                    email = login.email
                )
            )
    }

    @Test
    fun `login fails if user does not exist`() {
        val register = registerUser()

        val login = LoginUser(
            userId = register.userId,
            email = "someone@else.com"
        )

        fixture()
            .givenCommands(register)
            .`when`(login)
            .expectSuccessfulHandlerExecution()
            .expectEvents(
                LoginFailed(
                    userId = login.userId,
                    email = login.email,
                    reason = UserAggregate.loginErrorMessage
                )
            )
    }

    private fun fixture(registeredEmails: List<String> = emptyList()): FixtureConfiguration<UserAggregate> =
        AggregateTestFixture(UserAggregate::class.java)
            .registerInjectableResource(FakeUsersRepository(registeredEmails))

    private fun registerUser() = RegisterUser(
        email = "a@b.com",
        passwordHash = "abc12345"
    )
}

class FakeUsersRepository(private val emails: List<String>) : UsersRepository {

    override fun exists(email: String): Boolean =
        emails.contains(email)
}