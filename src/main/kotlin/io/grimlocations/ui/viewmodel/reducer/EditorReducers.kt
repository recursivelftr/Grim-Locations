package io.grimlocations.ui.viewmodel.reducer

import io.grimlocations.data.dto.LocationDTO
import io.grimlocations.data.dto.MetaDTO
import io.grimlocations.data.dto.containsProfileModDifficulty
import io.grimlocations.data.dto.firstContainer
import io.grimlocations.data.repo.*
import io.grimlocations.data.repo.action.*
import io.grimlocations.framework.data.dto.replaceDTO
import io.grimlocations.framework.ui.getState
import io.grimlocations.framework.ui.setState
import io.grimlocations.framework.util.awaitAll
import io.grimlocations.framework.util.extension.asPathToFile
import io.grimlocations.framework.util.guardLet
import io.grimlocations.ui.GLStateManager
import io.grimlocations.ui.viewmodel.state.EditorState
import io.grimlocations.ui.viewmodel.state.PMDManagerState
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File


@Suppress("UNCHECKED_CAST")
suspend fun GLStateManager.loadEditorState(
    previousState: EditorState? = null
) {
    val (pmdMap, meta) = awaitAll(
        repository.getProfilesModsDifficultiesAsync(),
        repository.getMetaAsync()
    )

    if (previousState == null) {
        val container = pmdMap.firstContainer()
        val locList = repository.getLocationsAsync(container).await()

        setState(
            EditorState(
                profileMap = pmdMap,
                selectedPMDLeft = container,
                selectedPMDRight = container.copy(),
                locationsLeft = locList,
                locationsRight = locList.toSet(), //copies the list,
                activePMD = meta.activePMD,
                isGDRunning = false,
                selectedLocationsLeft = setOf(),
                selectedLocationsRight = setOf(),
                isEditLocationLeftPopupOpen = false,
                isEditLocationRightPopupOpen = false,
                isLoadLocationsPopupOpen = false,
                isPropertiesPopupOpen = false,
                isConfirmDeleteLeftPopupOpen = false,
                isConfirmDeleteRightPopupOpen = false,
                isPMDManagerViewOpen = false,
            )
        )
    } else {
        val selectedPMDLeft =
            if (pmdMap.containsProfileModDifficulty(previousState.selectedPMDLeft))
                previousState.selectedPMDLeft
            else
                pmdMap.firstContainer()

        val selectedPMDRight =
            if (pmdMap.containsProfileModDifficulty(previousState.selectedPMDRight))
                previousState.selectedPMDRight
            else
                pmdMap.firstContainer()

        val (locList1, locList2) = awaitAll(
            repository.getLocationsAsync(selectedPMDLeft),
            repository.getLocationsAsync(selectedPMDRight)
        )

        setState(
            previousState.copy(
                selectedPMDLeft = selectedPMDLeft,
                selectedPMDRight = selectedPMDRight,
                profileMap = pmdMap,
                locationsLeft = locList1,
                selectedLocationsLeft = locList1.filter {
                    previousState.selectedLocationsLeft.find { l -> l.id == it.id } != null
                }.toSet(),
                locationsRight = locList2,
                selectedLocationsRight = locList2.filter {
                    previousState.selectedLocationsRight.find { l -> l.id == it.id } != null
                }.toSet(),
                activePMD = meta.activePMD,
            )
        )
    }
}

suspend fun GLStateManager.openConfirmDeleteLeft() {
    val s = getState<EditorState>()
    withContext(Dispatchers.Main) {
        setState(s.copy(isConfirmDeleteLeftPopupOpen = true))
    }
}

suspend fun GLStateManager.closeConfirmDeleteLeft() {
    val s = getState<EditorState>()
    withContext(Dispatchers.Main) {
        setState(s.copy(isConfirmDeleteLeftPopupOpen = false))
    }
}

suspend fun GLStateManager.openConfirmDeleteRight() {
    val s = getState<EditorState>()
    withContext(Dispatchers.Main) {
        setState(s.copy(isConfirmDeleteRightPopupOpen = true))
    }
}

suspend fun GLStateManager.closeConfirmDeleteRight() {
    val s = getState<EditorState>()
    withContext(Dispatchers.Main) {
        setState(s.copy(isConfirmDeleteRightPopupOpen = false))
    }
}

suspend fun GLStateManager.openEditLocationLeft() {
    val s = getState<EditorState>()
    withContext(Dispatchers.Main) {
        setState(s.copy(isEditLocationLeftPopupOpen = true))
    }
}

suspend fun GLStateManager.closeEditLocationLeft() {
    val s = getState<EditorState>()
    withContext(Dispatchers.Main) {
        setState(s.copy(isEditLocationLeftPopupOpen = false))
    }
}

suspend fun GLStateManager.openEditLocationRight() {
    val s = getState<EditorState>()
    withContext(Dispatchers.Main) {
        setState(s.copy(isEditLocationRightPopupOpen = true))
    }
}

suspend fun GLStateManager.closeEditLocationRight() {
    val s = getState<EditorState>()
    withContext(Dispatchers.Main) {
        setState(s.copy(isEditLocationRightPopupOpen = false))
    }
}

suspend fun GLStateManager.openPropertiesView() {
    val s = getState<EditorState>()
    withContext(Dispatchers.Main) {
        setState(s.copy(isPropertiesPopupOpen = true))
    }
}

suspend fun GLStateManager.closePropertiesView() {
    val s = getState<EditorState>()
    withContext(Dispatchers.Main) {
        setState(s.copy(isPropertiesPopupOpen = false))
    }
}

suspend fun GLStateManager.openLoadLocationsView() {
    val s = getState<EditorState>()
    withContext(Dispatchers.Main) {
        setState(s.copy(isLoadLocationsPopupOpen = true))
    }
}

suspend fun GLStateManager.closeLoadLocationsView() {
    val s = getState<EditorState>()
    withContext(Dispatchers.Main) {
        setState(s.copy(isLoadLocationsPopupOpen = false))
    }
}

suspend fun GLStateManager.openPMDManagerView() {
    val s = getState<EditorState>()
    withContext(Dispatchers.Main) {
        setState(s.copy(isPMDManagerViewOpen = true))
    }
}

suspend fun GLStateManager.closePMDManagerView() {
    val s = getState<EditorState>()
    withContext(Dispatchers.Main) {
        loadEditorState(s.copy(isPMDManagerViewOpen = false))
    }
}

suspend fun GLStateManager.reloadEditorState(state: EditorState? = null) {
    loadEditorState(state ?: getState())
}

suspend fun GLStateManager.updateIfGDRunning(): Boolean {
    val isRunning = repository.isGDRunning()
    val s = getState<EditorState>()

    if (s.isGDRunning != isRunning) {
        withContext(Dispatchers.Main) {
            setState(s.copy(isGDRunning = isRunning))
        }
    }
    return isRunning
}

private const val TELEPORT_LIST_FILENAME = "GrimInternals_TeleportList.txt"
private const val CHAR_FILENAME = "GrimLocations_Char.txt"
private var locations_file_last_modified: Long? = null
private var char_file_last_modified: Long? = null
private val locationsFileMutex = Mutex()

suspend fun GLStateManager.performFileChecksAndLoads() {
    val meta = repository.getMetaAsync().await()
    performActivePmdFileCheckAndLoad(meta)
    performLocationsFileCheckAndLoad(meta)
}

private suspend fun GLStateManager.performLocationsFileCheckAndLoad(meta: MetaDTO) {
    guardLet(meta.installLocation, meta.activePMD) { loc, pmd ->
        locationsFileMutex.withLock {
            val file = File(loc.asPathToFile(TELEPORT_LIST_FILENAME))
            val lastModified = repository.getFileLastModified(file)

            if (lastModified != null && locations_file_last_modified != lastModified) {
                repository.createLocationsFromFile(file, pmd)
                locations_file_last_modified = repository.getFileLastModified(file)
                reloadEditorState()
            }
        }
    }
}

private suspend fun GLStateManager.performActivePmdFileCheckAndLoad(meta: MetaDTO) {
    meta.installLocation?.let { loc ->
        val charFile = File(loc.asPathToFile(CHAR_FILENAME))
        val lastModified = repository.getFileLastModified(charFile)

        if (lastModified != null && (meta.activePMD == null || char_file_last_modified != lastModified)) {
            val teleportFile = File(loc.asPathToFile(TELEPORT_LIST_FILENAME))

            repository.createAndSetActivePmd(charFile, teleportFile, meta.activePMD)
            char_file_last_modified = lastModified
            locations_file_last_modified = repository.getFileLastModified(teleportFile)
            reloadEditorState()
        }
    }
}

private suspend fun GLStateManager.copyActivePMDToLocationsFile(pmdc: PMDContainer) {
    val meta = repository.getMetaAsync().await()
    if (pmdc == meta.activePMD) {
        guardLet(meta.installLocation, meta.activePMD) { loc, pmd ->
            locationsFileMutex.withLock {
                val file = File(loc.asPathToFile(TELEPORT_LIST_FILENAME))
                repository.writeLocationsToFile(file, pmd)
                locations_file_last_modified = repository.getFileLastModified(file)
            }
        }
    }
}

suspend fun GLStateManager.selectLocationsLeft(loc: Set<LocationDTO>) {
    val s = getState<EditorState>()
    withContext(Dispatchers.Main) {
        setState(
            s.copy(
                selectedLocationsLeft = loc
            )
        )
    }
}

suspend fun GLStateManager.selectLocationsRight(loc: Set<LocationDTO>) {
    val s = getState<EditorState>()
    withContext(Dispatchers.Main) {
        setState(
            s.copy(
                selectedLocationsRight = loc
            )
        )
    }
}

//suspend fun GLStateManager.deselectLocationsLeft(loc: Set<LocationDTO>) {
//    val s = getState<EditorState>()
//    withContext(Dispatchers.Main) {
//        setState(s.copy(
//            selectedLocationsLeft = s.selectedLocationsLeft.toMutableSet().apply { removeAll(loc) }
//        ))
//    }
//}
//
//suspend fun GLStateManager.deselectLocationsRight(loc: Set<LocationDTO>) {
//    val s = getState<EditorState>()
//    withContext(Dispatchers.Main) {
//        setState(s.copy(
//            selectedLocationsRight = s.selectedLocationsRight.toMutableSet().apply { removeAll(loc) }
//        ))
//    }
//}

suspend fun GLStateManager.selectPMDLeft(pmd: PMDContainer) {
    val s = getState<EditorState>()
    val locs = repository.getLocationsAsync(pmd).await()
    withContext(Dispatchers.Main) {
        setState(
            s.copy(
                selectedPMDLeft = pmd,
                locationsLeft = locs,
                selectedLocationsLeft = setOf()
            )
        )
    }
}

suspend fun GLStateManager.selectPMDRight(pmd: PMDContainer) {
    val s = getState<EditorState>()
    val locs = repository.getLocationsAsync(pmd).await()
    withContext(Dispatchers.Main) {
        setState(
            s.copy(
                selectedPMDRight = pmd,
                locationsRight = locs,
                selectedLocationsRight = setOf()
            )
        )
    }
}

suspend fun GLStateManager.copyLeftSelectedToRight() {
    val s = getState<EditorState>()
    repository.copyLocationsToPMD(
        pmdContainer = s.selectedPMDRight,
        selectedLocations = s.selectedLocationsLeft,
        otherSelectedLocations = s.selectedLocationsRight
    ).await()
    copyActivePMDToLocationsFile(s.selectedPMDRight)
    withContext(Dispatchers.Main) {
        reloadEditorState()
    }
}

suspend fun GLStateManager.copyRightSelectedToLeft() {
    val s = getState<EditorState>()
    repository.copyLocationsToPMD(
        pmdContainer = s.selectedPMDLeft,
        selectedLocations = s.selectedLocationsRight,
        otherSelectedLocations = s.selectedLocationsLeft
    ).await()
    copyActivePMDToLocationsFile(s.selectedPMDLeft)
    withContext(Dispatchers.Main) {
        reloadEditorState()
    }
}

suspend fun GLStateManager.moveSelectedLeftUp() {
    val s = getState<EditorState>()
    repository.decrementLocationsOrderAsync(
        pmdContainer = s.selectedPMDLeft,
        locations = s.selectedLocationsLeft
    ).await()
    copyActivePMDToLocationsFile(s.selectedPMDLeft)
    withContext(Dispatchers.Main) {
        reloadEditorState()
    }
}

suspend fun GLStateManager.moveSelectedLeftDown() {
    val s = getState<EditorState>()
    repository.incrementLocationsOrderAsync(
        pmdContainer = s.selectedPMDLeft,
        locations = s.selectedLocationsLeft
    ).await()
    copyActivePMDToLocationsFile(s.selectedPMDLeft)
    withContext(Dispatchers.Main) {
        reloadEditorState()
    }
}

suspend fun GLStateManager.moveSelectedRightUp() {
    val s = getState<EditorState>()
    repository.decrementLocationsOrderAsync(
        pmdContainer = s.selectedPMDRight,
        locations = s.selectedLocationsRight
    ).await()
    copyActivePMDToLocationsFile(s.selectedPMDRight)
    withContext(Dispatchers.Main) {
        reloadEditorState()
    }
}

suspend fun GLStateManager.moveSelectedRightDown() {
    val s = getState<EditorState>()
    repository.incrementLocationsOrderAsync(
        pmdContainer = s.selectedPMDRight,
        locations = s.selectedLocationsRight
    ).await()
    copyActivePMDToLocationsFile(s.selectedPMDRight)
    withContext(Dispatchers.Main) {
        reloadEditorState()
    }
}

suspend fun GLStateManager.deleteSelectedLeft() {
    val s = getState<EditorState>()
    repository.deleteLocationsAsync(s.selectedPMDLeft, s.selectedLocationsLeft).await()
    copyActivePMDToLocationsFile(s.selectedPMDLeft)
    withContext(Dispatchers.Main) {
        reloadEditorState()
    }
}

suspend fun GLStateManager.deleteSelectedRight() {
    val s = getState<EditorState>()
    repository.deleteLocationsAsync(s.selectedPMDRight, s.selectedLocationsRight).await()
    copyActivePMDToLocationsFile(s.selectedPMDRight)
    withContext(Dispatchers.Main) {
        reloadEditorState()
    }
}

suspend fun GLStateManager.updateAndCloseLocationLeft(loc: LocationDTO) {
    val s = getState<EditorState>()
    updateAndCloseLocation(loc, s.selectedPMDLeft, s)
}

suspend fun GLStateManager.updateAndCloseLocationRight(loc: LocationDTO) {
    val s = getState<EditorState>()
    updateAndCloseLocation(loc, s.selectedPMDRight, s)
}

private suspend fun GLStateManager.updateAndCloseLocation(location: LocationDTO, pmd: PMDContainer, s: EditorState) {
    if (repository.updateLocationAsync(location).await() == null) {
        copyActivePMDToLocationsFile(pmd)
        withContext(Dispatchers.Main) {
            setState(
                s.copy(
                    locationsRight = s.locationsRight.replaceDTO(location),
                    locationsLeft = s.locationsLeft.replaceDTO(location),
                    selectedLocationsRight = s.selectedLocationsRight.replaceDTO(location),
                    selectedLocationsLeft = s.selectedLocationsLeft.replaceDTO(location),
                    isEditLocationLeftPopupOpen = false,
                    isEditLocationRightPopupOpen = false,
                )
            )
        }
    }
}

suspend fun GLStateManager.clearActivePMD() {
    repository.clearActivePMD()
    withContext(Dispatchers.Main) {
        loadEditorState(getState())
    }
}