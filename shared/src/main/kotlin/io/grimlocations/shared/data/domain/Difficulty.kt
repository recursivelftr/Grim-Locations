package io.grimlocations.shared.data.domain

import io.grimlocations.shared.data.dto.DifficultyDTO
import io.grimlocations.shared.framework.data.domain.BaseTable
import io.grimlocations.shared.framework.data.domain.DTOEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

object DifficultyTable : BaseTable("difficulty") {
    val name = text("name").uniqueIndex()
}

class Difficulty(id: EntityID<Int>) : DTOEntity<DifficultyTable, DifficultyDTO>(id, DifficultyTable) {
    var name by DifficultyTable.name

    var mods by Mod via ModDifficultyIntermTable
    val locations by Location referrersOn LocationTable.difficulty

    override fun toDTO(): DifficultyDTO {
        return DifficultyDTO(id.value, name)
    }

    companion object : IntEntityClass<Difficulty>(DifficultyTable)
}