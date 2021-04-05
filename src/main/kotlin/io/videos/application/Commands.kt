package io.videos.application

import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Component

@Component
class Commands(private val commandGateway: CommandGateway) {

    fun <T : Command> issue(command: T) {
        commandGateway.send<T>(command).join()
    }
}

interface Command

interface Event