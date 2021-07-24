package io.grimlocations.data.domain

import io.grimlocations.data.dto.ModDTO
import io.grimlocations.framework.data.domain.BaseTable
import io.grimlocations.framework.data.domain.DTOEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

object ModTable : BaseTable("mod") {
    val name = text("name").uniqueIndex()
    val isUserCreated = integer("is_user_created").default(0)
}

class Mod(id: EntityID<Int>) : DTOEntity<ModTable, ModDTO>(id, ModTable) {
    var name by ModTable.name
    var isUserCreated by ModTable.isUserCreated

    @Deprecated("Use ProfileOrder entities instead")
    var profiles by Profile via ProfileModIntermTable

    @Deprecated("Use DifficultyOrder entities instead")
    var difficulties by Difficulty via ModDifficultyIntermTable

    val locations by Location referrersOn LocationTable.mod

    override fun toDTO(): ModDTO {
        return ModDTO(id.value, created, modified, name, isUserCreated == 1)
    }

    companion object : IntEntityClass<Mod>(ModTable)
}