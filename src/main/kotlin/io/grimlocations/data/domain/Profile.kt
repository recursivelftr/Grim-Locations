package io.grimlocations.data.domain

import io.grimlocations.data.dto.ProfileDTO
import io.grimlocations.framework.data.domain.BaseTable
import io.grimlocations.framework.data.domain.DTOEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID


object ProfileTable : BaseTable("profile") {
    val name = text("name").uniqueIndex()
    val isUserCreated = integer("is_user_created").default(0)
}

class Profile(id: EntityID<Int>) : DTOEntity<ProfileTable, ProfileDTO>(id, ProfileTable) {
    var name by ProfileTable.name
    var isUserCreated by ProfileTable.isUserCreated

    @Deprecated("Use ModOrder entities instead")
    var mods by Mod via ProfileModIntermTable

//    var profileOrder by ProfileOrder referencedOn ProfileOrderTable.profile

    val locations by Location referrersOn LocationTable.profile

    override fun toDTO(): ProfileDTO {
        return ProfileDTO(id.value, created, modified, name, isUserCreated == 1)
    }

    companion object : IntEntityClass<Profile>(ProfileTable)
}