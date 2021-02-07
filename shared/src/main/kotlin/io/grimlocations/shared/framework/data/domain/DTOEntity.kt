package io.grimlocations.shared.framework.data.domain

import io.grimlocations.shared.framework.data.dto.DTO
import org.jetbrains.exposed.dao.id.EntityID

abstract class DTOEntity<T : BaseTable, D : DTO>(id: EntityID<Int>, table: T) : BaseEntity<T>(id, table) {
    abstract fun toDTO(): D
}