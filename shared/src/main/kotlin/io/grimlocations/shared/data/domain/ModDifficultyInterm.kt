package io.grimlocations.shared.data.domain

import org.jetbrains.exposed.sql.Table

object ModDifficultyIntermTable: Table() {
    val mod = reference("mod", ModTable)
    val difficulty = reference("difficulty", DifficultyTable)

    override val primaryKey = PrimaryKey(mod, difficulty, name = "PK_ModDifficultyInterm_mod_difficulty")
}