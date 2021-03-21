package io.grimlocations.framework.data.domain

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.CurrentDateTime
import org.jetbrains.exposed.sql.`java-time`.datetime
import java.time.LocalDateTime

abstract class BaseTable(name: String = "", columnName: String = "id") : IntIdTable(name, columnName) {
    val created = datetime("created")
    val modified = datetime("modified")

    init {
        created.defaultValueFun = ::defaultValueFun
        modified.defaultValueFun = ::defaultValueFun
    }

    companion object {
        private fun defaultValueFun(): LocalDateTime {
            return LocalDateTime.now()
        }
    }
}