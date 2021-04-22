package io.videos.application

import java.util.UUID

class Repository<T : Entity> {

    private var entities: MutableMap<UUID, T> = mutableMapOf()

    fun add(entity: T) {
        entities[entity.id] = entity
    }

    fun all(): List<T> =
        entities.values.toList()

    fun find(id: UUID): T? =
        entities[id]

    fun delete(id: UUID) {
        entities.remove(id)
    }
}

fun <T : Entity> emptyRepository(): Repository<T> =
    Repository()

interface Entity {
    val id: UUID
}