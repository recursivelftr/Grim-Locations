package io.grimlocations.util.extension

import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.Shell32Util
import com.sun.jna.platform.win32.ShlObj
import com.sun.jna.platform.win32.WinReg
import io.grimlocations.constant.TITLE
import net.harawata.appdirs.AppDirs
import org.apache.logging.log4j.LogManager
import java.io.File

private val logger = LogManager.getLogger()

private var didFetchSteamInstallDir = false
private var didFetchDocsDir = false
private var steamInstallDir: String? = null
private var documentsDir: String? = null

val AppDirs.glDataDir: String
    get() = getUserDataDir(TITLE, null, null)

val AppDirs.glDatabaseDir: String
    get() = "$glDataDir${File.separator}database"

val AppDirs.glDatabaseBackupDir: String
    get() = "$glDataDir${File.separator}database_backup"

val AppDirs.gdInstallLocation: String?
    get() = getSteamInstallLocation()?.let {
        "$it${File.separator}steamapps${File.separator}common${File.separator}Grim Dawn"
    }

val AppDirs.gdSaveLocation: String?
    get() = getDocumentsDirectory()?.let {
        "$it${File.separator}My Games${File.separator}Grim Dawn${File.separator}save${File.separator}main"
    }

private fun getDocumentsDirectory(): String? {
    try {
        if (!didFetchDocsDir) {
            didFetchDocsDir = true
            documentsDir = Shell32Util.getFolderPath(ShlObj.CSIDL_PERSONAL)
        }
    } catch (e: Exception) {
        logger.error("Unable to get the documents directory.", e)
    }
    return documentsDir
}

private fun getSteamInstallLocation(): String? {
    try {
        if (!didFetchSteamInstallDir) {
            didFetchSteamInstallDir = true
            steamInstallDir = Advapi32Util.registryGetStringValue(
                WinReg.HKEY_LOCAL_MACHINE,
                "SOFTWARE${File.separator}Wow6432Node${File.separator}Valve${File.separator}Steam",
                "InstallPath"
            )
        }
    } catch (e: Exception) {
        logger.error("Unable to get the steam install folder from the registry.", e)
    }
    return steamInstallDir
}