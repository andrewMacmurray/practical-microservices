package io.videos.application.cqrs

import org.axonframework.eventhandling.gateway.EventGateway
import org.springframework.stereotype.Component

@Component
class Events(private val eventGateway: EventGateway) {

    fun <T : Event> emit(e: T) {
        eventGateway.publish(e)
    }
}

interface Event
