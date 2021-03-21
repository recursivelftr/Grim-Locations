package io.grimlocations.data.domain

import io.grimlocations.data.dto.MetaDTO
import io.grimlocations.framework.data.domain.BaseTable
import io.grimlocations.framework.data.domain.DTOEntity
import io.grimlocations.framework.util.guardLet
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

//only one row in this table should ever exist

object MetaTable : BaseTable("meta") {
    val version = integer("version")
    val saveLocation = text("save_location").nullable()
    val installLocation = text("install_location").nullable()
    val activeProfile = reference("active_profile", ProfileTable).nullable()
    val activeMod = reference("active_mod", ModTable).nullable()
    val activeDifficulty = reference("active_difficulty", DifficultyTable).nullable()
}

class Meta(id: EntityID<Int>) : DTOEntity<MetaTable, MetaDTO>(id, MetaTable) {
    var version by MetaTable.version
    var saveLocation by MetaTable.saveLocation
    var installLocation by MetaTable.installLocation
    var activeProfile by Profile optionalReferencedOn MetaTable.activeProfile
    var activeMod by Mod optionalReferencedOn MetaTable.activeMod
    var activeDifficulty by Difficulty optionalReferencedOn MetaTable.activeDifficulty

    override fun toDTO(): MetaDTO {
        var activePMD: PMDContainer? = null

        guardLet(activeProfile, activeMod, activeDifficulty) { p, m, d ->
            activePMD = PMDContainer(p.toDTO(), m.toDTO(), d.toDTO())
        }

        return MetaDTO(id.value, created, modified, version, saveLocation, installLocation, activePMD)
    }

    companion object : IntEntityClass<Meta>(MetaTable)
}