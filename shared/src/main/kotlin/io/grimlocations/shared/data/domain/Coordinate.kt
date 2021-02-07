package io.grimlocations.shared.data.domain

import io.grimlocations.shared.data.dto.CoordinateDTO
import io.grimlocations.shared.framework.data.domain.BaseTable
import io.grimlocations.shared.framework.data.domain.DTOEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

object CoordinateTable : BaseTable("difficulty") {
    val coordinate1 = text("coordinate1")
    val coordinate2 = text("coordinate2")
    val coordinate3 = text("coordinate3")

    init {
        uniqueIndex(coordinate1, coordinate2, coordinate3)
    }

}

class Coordinate(id: EntityID<Int>) : DTOEntity<CoordinateTable, CoordinateDTO>(id, CoordinateTable){
    var coordinate1 by CoordinateTable.coordinate1
    var coordinate2 by CoordinateTable.coordinate2
    var coordinate3 by CoordinateTable.coordinate3

    val locations by Location referrersOn LocationTable.coordinate

    override fun toDTO(): CoordinateDTO {
        return CoordinateDTO(id.value, coordinate1, coordinate2, coordinate3)
    }

    companion object : IntEntityClass<Coordinate>(CoordinateTable)
}