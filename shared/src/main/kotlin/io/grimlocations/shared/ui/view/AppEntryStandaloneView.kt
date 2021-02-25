package io.grimlocations.shared.ui.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import io.grimlocations.shared.data.repo.SqliteRepository
import io.grimlocations.shared.data.repo.action.arePropertiesSet
import io.grimlocations.shared.ui.GLStateManager
import io.grimlocations.shared.ui.GLViewModelProvider
import kotlinx.coroutines.*
import net.harawata.appdirs.AppDirsFactory

@ExperimentalCoroutinesApi
@Composable
fun AppEntryStandaloneView(
    newWindow: (Boolean, GLViewModelProvider) -> Unit
) {
    val state = remember { mutableStateOf<StartState?>(null) }
    val scope = remember { CoroutineScope(Dispatchers.IO + Job()) }

    remember {
        scope.launch {
            val s = initializeApp()
            withContext(Dispatchers.Main) {
                state.value = s
            }
        }
    }

    state.value?.let {
        newWindow(it.arePropertiesSet, it.viewModelProvider)
    } ?: run {
        SplashScreen()
    }
}

private suspend fun initializeApp(): StartState {
    val repository = SqliteRepository(AppDirsFactory.getInstance())
    repository.initDb()
    return StartState(
        repository.arePropertiesSet().await(),
        GLViewModelProvider(
            GLStateManager(repository)
        )
    )
}

val SPLASH_SCREEN_SIZE = IntSize(500, 300)

@Composable
private fun SplashScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text("LOADING...")
    }
}

private data class StartState(
    val arePropertiesSet: Boolean,
    val viewModelProvider: GLViewModelProvider
)