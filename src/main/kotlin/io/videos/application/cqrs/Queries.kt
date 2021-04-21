package io.videos.application.cqrs

import io.videos.application.pipe
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class Queries(private val gateway: QueryGateway) {

    fun <T : Any> get(query: Query<T>): T =
        ResponseTypes.instanceOf(query.type.java)
            .pipe { gateway.query(query, it) }
            .join()
}

interface Query<T : Any> {
    val type: KClass<T>

    fun exec(queries: Queries): T =
        queries.get(this)
}
