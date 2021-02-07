package io.grimlocations.shared.framework.data.domain

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.CurrentDateTime
import org.jetbrains.exposed.sql.`java-time`.datetime

abstract class BaseTable(name: String = "", columnName: String = "id") : IntIdTable(name, columnName) {
    val created = datetime("created").defaultExpression(timeDefaultExpression)
    val modified = datetime("modified").defaultExpression(timeDefaultExpression)

    companion object {
        private val timeDefaultExpression = CurrentDateTime()
    }
}