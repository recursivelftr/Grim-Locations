package io.grimlocations.shared.data.domain

import io.grimlocations.shared.data.dto.MetaDTO
import io.grimlocations.shared.framework.data.domain.BaseEntity
import io.grimlocations.shared.framework.data.domain.BaseTable
import io.grimlocations.shared.framework.data.domain.DTOEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

//only one row in this table should ever exist

object MetaTable : BaseTable("meta") {
    val version = integer("version")
    val saveLocation = text("save_location").nullable()
    val installLocation = text("install_location").nullable()
}

class Meta(id: EntityID<Int>) : DTOEntity<MetaTable, MetaDTO>(id, MetaTable) {
    var version by MetaTable.version
    var saveLocation by MetaTable.saveLocation
    var installLocation by MetaTable.installLocation

    override fun toDTO(): MetaDTO {
        return MetaDTO(id.value, created, modified, version, saveLocation, installLocation)
    }

    companion object : IntEntityClass<Meta>(MetaTable)
}