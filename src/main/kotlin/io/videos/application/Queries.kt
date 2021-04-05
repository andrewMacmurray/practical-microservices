package io.videos.application

import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class Queries(private val queryGateway: QueryGateway) {

    fun <T : Any> get(q: Query, g: KClass<T>): T =
        queryGateway.query(q, ResponseTypes.instanceOf(g.java)).join()
}

interface Query