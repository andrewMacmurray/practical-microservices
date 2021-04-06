package io.videos.application.registration

import io.videos.application.cqrs.Commands
import io.videos.application.identity.RegisterUser
import io.videos.application.pipe
import org.springframework.stereotype.Service

@Service
class UsersService(private val commands: Commands) {

    fun register(registration: Registration) {
        registration
            .toCmd()
            .pipe(commands::issue)
    }
}

private fun Registration.toCmd() = RegisterUser(
    email = this.email,
    passwordHash = password
)