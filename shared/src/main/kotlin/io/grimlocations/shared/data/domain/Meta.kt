package io.grimlocations.shared.data.domain

import io.grimlocations.shared.data.dto.MetaDTO
import io.grimlocations.shared.framework.data.domain.BaseTable
import io.grimlocations.shared.framework.data.domain.DTOEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

//only one row in this table should ever exist

object MetaTable : BaseTable("meta") {
    val version = integer("version")
    val saveLocation = text("save_location").nullable()
    val installLocation = text("install_location").nullable()
    val selectedProfile = reference("selected_profile", ProfileTable).nullable()
    val selectedMod = reference("selected_mod", ModTable).nullable()
    val selectedDifficulty = reference("selected_difficulty", DifficultyTable).nullable()
}

class Meta(id: EntityID<Int>) : DTOEntity<MetaTable, MetaDTO>(id, MetaTable) {
    var version by MetaTable.version
    var saveLocation by MetaTable.saveLocation
    var installLocation by MetaTable.installLocation
    var selectedProfile by Profile optionalReferencedOn MetaTable.selectedProfile
    var selectedMod by Mod optionalReferencedOn MetaTable.selectedMod
    var selectedDifficulty by Difficulty optionalReferencedOn MetaTable.selectedDifficulty

    override fun toDTO(): MetaDTO {
        return MetaDTO(id.value, created, modified, version, saveLocation, installLocation)
    }

    companion object : IntEntityClass<Meta>(MetaTable)
}