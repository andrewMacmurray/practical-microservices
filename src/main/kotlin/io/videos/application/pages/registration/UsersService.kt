package io.videos.application.pages.registration

import io.videos.application.Result
import io.videos.application.cqrs.Commands
import io.videos.application.cqrs.Events
import io.videos.application.cqrs.Queries
import io.videos.application.domains.identity.LoginFailed
import io.videos.application.domains.identity.LoginUser
import io.videos.application.domains.identity.RegisterUser
import io.videos.application.domains.identity.RegisteredUsersQuery
import io.videos.application.domains.identity.User
import io.videos.application.pipe
import org.springframework.stereotype.Service

@Service
class UsersService(
    private val commands: Commands,
    private val queries: Queries,
    private val events: Events
) {

    fun register(registration: Registration) {
        registration
            .toCmd()
            .pipe(commands::issue)
    }

    fun login(login: Login): Result<User, String> {
        val user = findUser(login)
        return if (user != null) {
            loginSuccess(user)
        } else {
            loginFailure(login)
        }
    }

    private fun loginFailure(login: Login): Result.Error<User, String> {
        signalFailure(login)
        return Result.Error("no user exists")
    }

    private fun loginSuccess(user: User): Result.Ok<User, String> {
        doLogin(user)
        return Result.Ok(user)
    }

    private fun doLogin(user: User) {
        user.toCmd().pipe(commands::issue)
    }

    private fun findUser(login: Login) =
        RegisteredUsersQuery
            .exec(queries)
            .findByEmail(login.email)

    private fun signalFailure(login: Login) {
        events.emit(
            LoginFailed(
                email = login.email,
                reason = "user does not exist"
            )
        )
    }
}

private fun User.toCmd() = LoginUser(
    userId = this.id,
    email = this.email
)

private fun Registration.toCmd() = RegisterUser(
    email = this.email,
    passwordHash = password
)