package io.videos.application.pages.registration

import io.videos.application.Result
import io.videos.application.cqrs.Commands
import io.videos.application.cqrs.Events
import io.videos.application.domains.identity.LoginFailed
import io.videos.application.domains.identity.LoginUser
import io.videos.application.domains.identity.RegisterUser
import io.videos.application.domains.identity.RegisteredUser
import io.videos.application.domains.identity.UsersRepository
import io.videos.application.pipe
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UsersService(
    private val commands: Commands,
    private val users: UsersRepository,
    private val events: Events
) {

    fun register(registration: Registration): UUID {
        val register = registration.toCmd()
        commands.issue(register)
        return register.userId
    }

    fun login(login: Login): Result<RegisteredUser, String> {
        val user = findUser(login)
        return if (user != null) {
            loginSuccess(user)
        } else {
            loginFailure(login)
        }
    }

    private fun loginFailure(login: Login): Result.Error<RegisteredUser, String> {
        signalFailure(login)
        return Result.Error("no user exists")
    }

    private fun loginSuccess(user: RegisteredUser): Result.Ok<RegisteredUser, String> {
        doLogin(user)
        return Result.Ok(user)
    }

    private fun doLogin(user: RegisteredUser) {
        user.toCmd().pipe(commands::issue)
    }

    private fun findUser(login: Login) =
        users.findByEmail(login.email)

    private fun signalFailure(login: Login) {
        events.publish(
            LoginFailed(
                email = login.email,
                reason = "user does not exist"
            )
        )
    }
}

private fun RegisteredUser.toCmd() = LoginUser(
    userId = this.id,
    email = this.email
)

private fun Registration.toCmd() = RegisterUser(
    email = this.email,
    passwordHash = password
)