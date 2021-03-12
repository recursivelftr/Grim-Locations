package io.grimlocations.shared.ui.viewmodel.reducer

import io.grimlocations.shared.data.dto.firstContainer
import io.grimlocations.shared.data.repo.action.detectAndCreateProfilesAsync
import io.grimlocations.shared.data.repo.action.getLocationsAsync
import io.grimlocations.shared.data.repo.action.getMetaAsync
import io.grimlocations.shared.data.repo.action.getProfilesModsDifficultiesAsync
import io.grimlocations.shared.data.repo.writeLocationsToFile
import io.grimlocations.shared.framework.ui.getState
import io.grimlocations.shared.framework.ui.setState
import io.grimlocations.shared.framework.util.awaitAll
import io.grimlocations.shared.framework.util.extension.endsWithOne
import io.grimlocations.shared.ui.GLStateManager
import io.grimlocations.shared.ui.viewmodel.state.EditorState
import io.grimlocations.shared.ui.viewmodel.state.container.PMDContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


@Suppress("UNCHECKED_CAST")
suspend fun GLStateManager.loadEditorState(
    selected: Pair<PMDContainer, PMDContainer>? = null,
    isGDRunning: Boolean? = null
) {
    val (pmdMap, meta) = awaitAll(
        repository.getProfilesModsDifficultiesAsync(),
        repository.getMetaAsync()
    )

    if (selected == null) {
        val container = pmdMap.firstContainer()
        val locList = repository.getLocationsAsync(container).await()

        setState(
            EditorState(
                profileMap = pmdMap,
                selectedPMDLeft = container,
                selectedPMDRight = container.copy(),
                locationsLeft = locList,
                locationsRight = locList.toList(), //copies the list,
                activePMD = meta.activePMD,
                isGDRunning = isGDRunning ?: false,
                installLocation = meta.installLocation!!
            )
        )
    } else {
        val (locList1, locList2) = awaitAll(
            repository.getLocationsAsync(selected.first),
            repository.getLocationsAsync(selected.second)
        )

        setState(
            EditorState(
                profileMap = pmdMap,
                selectedPMDLeft = selected.first,
                selectedPMDRight = selected.second,
                locationsLeft = locList1,
                locationsRight = locList2,
                activePMD = meta.activePMD,
                isGDRunning = isGDRunning ?: false,
                installLocation = meta.installLocation!!
            )
        )
    }
}

suspend fun GLStateManager.loadCharacterProfiles() {
    repository.detectAndCreateProfilesAsync().await()
    reloadEditorState()
}

suspend fun GLStateManager.reloadEditorState() {
    val s = getState<EditorState>()
    loadEditorState(Pair(s.selectedPMDLeft, s.selectedPMDRight))
}

suspend fun GLStateManager.updateIfGDRunning(): Boolean =
    withContext(Dispatchers.IO) {
        lateinit var line: String
        var pidInfo = ""

        val p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe")

        val input = BufferedReader(InputStreamReader(p.inputStream))

        while (input.readLine()?.also { line = it } != null) {
            pidInfo += line
        }

        input.close()
        val isRunning = pidInfo.contains("Grim Dawn.exe")
        val s = getState<EditorState>()

        if (s.isGDRunning != isRunning) {
            withContext(Dispatchers.Main) {
                loadEditorState(Pair(s.selectedPMDLeft, s.selectedPMDRight), isRunning)
            }
        }
        isRunning
    }

suspend fun GLStateManager.checkIfLocationsFileChangedAndLoadLocation() {
    val s = getState<EditorState>()
    val file = if(s.installLocation.endsWithOne("/", "\\"))
        File(s.installLocation+"GrimInternals_TeleportList.txt")
    else
        File(s.installLocation+ File.separator+"GrimInternals_TeleportList.txt")


}