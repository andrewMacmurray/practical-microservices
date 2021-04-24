package io.videos.application

import java.util.UUID

class Repository<T : Entity> {

    private var entities: MutableMap<UUID, T> = mutableMapOf()

    fun add(entity: T) {
        entities[entity.id] = entity
    }

    fun update(id: UUID, fn: (T) -> T) {
        entities.computeIfPresent(id) { _, v -> fn(v) }
    }

    fun all(): List<T> =
        entities.values.toList()

    fun find(id: UUID): T? =
        entities[id]

    fun find(where: (T) -> Boolean): T? =
        all().find(where)

    fun delete(id: UUID) {
        entities.remove(id)
    }
}

fun <T : Entity> emptyRepository(): Repository<T> =
    Repository()

interface Entity {
    val id: UUID
}