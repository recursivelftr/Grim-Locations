package io.grimlocations.shared.data.domain

import io.grimlocations.shared.framework.data.domain.BaseEntity
import io.grimlocations.shared.framework.data.domain.BaseTable
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

//only one row in this table should ever exist

object MetaTable : BaseTable("meta") {
    val version = integer("version")
    val saveLocation = text("save_location").nullable()
    val installLocation = text("install_location").nullable()
}

class Meta(id: EntityID<Int>) : BaseEntity<MetaTable>(id, MetaTable) {
    var version by MetaTable.version
    var saveLocation by MetaTable.saveLocation
    var installLocation by MetaTable.installLocation

    companion object : IntEntityClass<Meta>(MetaTable)
}