package io.grimlocations.data.domain

import io.grimlocations.data.dto.ProfileOrderDTO
import io.grimlocations.framework.data.domain.BaseTable
import io.grimlocations.framework.data.domain.DTOEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

object ProfileOrderTable : BaseTable("profile_order") {
    val profile = reference("profile", ProfileTable).uniqueIndex()
    val order = integer("order")

    init {
        uniqueIndex(profile, order)
    }
}

class ProfileOrder(id: EntityID<Int>) : DTOEntity<ProfileOrderTable, ProfileOrderDTO>(id, ProfileOrderTable) {
    var profile by Profile referencedOn ProfileOrderTable.profile
    var order by ProfileOrderTable.order

    val modOrders by ModOrder referrersOn ModOrderTable.profileOrder

    override fun toDTO(): ProfileOrderDTO {
        return ProfileOrderDTO(id.value, created, modified, order, profile.toDTO())
    }

    companion object : IntEntityClass<ProfileOrder>(ProfileOrderTable)
}