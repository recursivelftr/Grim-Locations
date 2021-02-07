package io.grimlocations.shared.data.domain

import io.grimlocations.shared.data.dto.ProfileDTO
import io.grimlocations.shared.framework.data.domain.BaseTable
import io.grimlocations.shared.framework.data.domain.DTOEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID


object ProfileTable : BaseTable("profile") {
    val name = text("name").uniqueIndex()
}

class Profile(id: EntityID<Int>) : DTOEntity<ProfileTable, ProfileDTO>(id, ProfileTable) {
    var name by ProfileTable.name

    var mods by Mod via ProfileModIntermTable
    val locations by Location referrersOn LocationTable.profile

    override fun toDTO(): ProfileDTO {
        return ProfileDTO(id.value, name)
    }

    companion object : IntEntityClass<Profile>(ProfileTable)
}