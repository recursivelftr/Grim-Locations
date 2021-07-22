package io.grimlocations.data.domain

import io.grimlocations.data.dto.DifficultyOrderDTO
import io.grimlocations.framework.data.domain.BaseTable
import io.grimlocations.framework.data.domain.DTOEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

object DifficultyOrderTable : BaseTable("difficulty_order") {
    val modOrder = reference("modOrder", ModOrderTable)
    val difficulty = reference("difficulty", DifficultyTable)
    val order = integer("order")

    init {
        uniqueIndex(modOrder, difficulty)
        uniqueIndex(modOrder, difficulty, order)
    }
}

class DifficultyOrder(id: EntityID<Int>) : DTOEntity<DifficultyOrderTable, DifficultyOrderDTO>(id, DifficultyOrderTable) {
    var modOrder by ModOrder referencedOn DifficultyOrderTable.modOrder
    var difficulty by Difficulty referencedOn DifficultyOrderTable.difficulty
    var order by DifficultyOrderTable.order

    override fun toDTO(): DifficultyOrderDTO {
        return DifficultyOrderDTO(id.value, created, modified, order, difficulty.toDTO())
    }

    companion object : IntEntityClass<DifficultyOrder>(DifficultyOrderTable)
}