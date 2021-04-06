package io.videos.application.cqrs

import kotlin.reflect.KClass

interface Query<T : Any> {
    val type: KClass<T>
}