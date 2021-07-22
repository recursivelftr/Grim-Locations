package io.grimlocations.data.domain

import io.grimlocations.data.dto.ModOrderDTO
import io.grimlocations.framework.data.domain.BaseTable
import io.grimlocations.framework.data.domain.DTOEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

object ModOrderTable : BaseTable("mod_order") {
    val profileOrder = reference("profile_order", ProfileOrderTable)
    val mod = reference("mod", ModTable)
    val order = integer("order")

    init {
        uniqueIndex(profileOrder, mod, order)
    }
}

class ModOrder(id: EntityID<Int>) : DTOEntity<ModOrderTable, ModOrderDTO>(id, ModOrderTable) {
    var profileOrder by ProfileOrder referencedOn ModOrderTable.profileOrder
    var mod by Mod referencedOn ModOrderTable.mod
    var order by ModOrderTable.order

    val difficultyOrders by DifficultyOrder referrersOn DifficultyOrderTable.modOrder

    override fun toDTO(): ModOrderDTO {
        return  ModOrderDTO(id.value, created, modified,  order, mod.toDTO())
    }

    companion object : IntEntityClass<ModOrder>(ModOrderTable)
}