package io.videos.application.cqrs

import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Component

@Component
class Commands(private val gateway: CommandGateway) {

    fun <T : Command> issue(command: T) {
        gateway.send<T>(command).join()
    }
}

interface Command
