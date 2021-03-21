package io.grimlocations.data.domain

import io.grimlocations.data.dto.LocationDTO
import io.grimlocations.framework.data.domain.BaseTable
import io.grimlocations.framework.data.domain.DTOEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

object LocationTable : BaseTable("location") {
    val name = text("name")
    val order = integer("order")
    val profile = reference("profile", ProfileTable)
    val mod = reference("mod", ModTable)
    val difficulty = reference("difficulty", DifficultyTable)
    val coordinate = reference("coordinate", CoordinateTable)

    init {
        uniqueIndex(profile, mod, difficulty, coordinate)
        uniqueIndex(profile, mod, difficulty, order)
    }
}

class Location(id: EntityID<Int>) : DTOEntity<LocationTable, LocationDTO>(id, LocationTable) {
    var name by LocationTable.name
    var order by LocationTable.order
    var profile by Profile referencedOn LocationTable.profile
    var mod by Mod referencedOn LocationTable.mod
    var difficulty by Difficulty referencedOn LocationTable.difficulty
    var coordinate by Coordinate referencedOn LocationTable.coordinate

    override fun toDTO(): LocationDTO {
        return LocationDTO(
            id.value,
            created,
            modified,
            name,
            order,
            coordinate.toDTO()
        )
    }

    companion object : IntEntityClass<Location>(LocationTable)
}