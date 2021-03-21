package io.grimlocations.util.extension

import io.grimlocations.constant.TITLE
import net.harawata.appdirs.AppDirs
import java.io.File

val AppDirs.glDataDir: String
    get() = getUserDataDir(TITLE, null, null)

val AppDirs.glDatabaseDir: String
    get() = "$glDataDir${File.separator}database"

val AppDirs.glDatabaseBackupDir: String
    get() = "$glDataDir${File.separator}database_backup"