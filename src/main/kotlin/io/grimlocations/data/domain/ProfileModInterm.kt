package io.grimlocations.data.domain

import org.jetbrains.exposed.sql.Table

object ProfileModIntermTable : Table() {
    val profile = reference("profile", ProfileTable)
    val mod = reference("mod", ModTable)

    override val primaryKey = PrimaryKey(profile, mod, name = "PK_ProfileModInterim_profile_mod")
}