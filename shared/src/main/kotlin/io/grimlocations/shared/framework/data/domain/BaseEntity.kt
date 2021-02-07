package io.grimlocations.shared.framework.data.domain

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID

abstract class BaseEntity<T: BaseTable>(id: EntityID<Int>, table: T): IntEntity(id) {
    var created by table.created
    var modified by table.modified
}