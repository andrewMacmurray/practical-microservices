package io.videos.application.cqrs

import io.videos.application.pipe
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class Queries(private val gateway: QueryGateway) {

    fun <T : Any> get(query: Query, response: KClass<T>): T =
        ResponseTypes.instanceOf(response.java)
            .pipe { gateway.query(query, it) }
            .join()
}

