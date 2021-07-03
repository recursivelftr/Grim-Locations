package io.grimlocations.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowSize
import io.grimlocations.data.repo.SqliteRepository
import io.grimlocations.data.repo.action.arePropertiesSetAsync
import io.grimlocations.framework.ui.LocalViewModel
import io.grimlocations.ui.GLStateManager
import io.grimlocations.ui.GLViewModelProvider
import kotlinx.coroutines.*
import net.harawata.appdirs.AppDirsFactory

@ExperimentalCoroutinesApi
@Composable
fun AppEntryStandaloneView(
    newWindow: @Composable (Boolean, GLViewModelProvider) -> Unit
) {
    val state = remember { mutableStateOf<StartState?>(null) }

    remember {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            val s = initializeApp()
            withContext(Dispatchers.Main) {
                state.value = s
            }
        }
    }


    state.value?.also {
        newWindow(it.arePropertiesSet, it.viewModelProvider)
    } ?: SplashScreen()
}

private suspend fun initializeApp(): StartState {
    val repository = SqliteRepository(AppDirsFactory.getInstance())
    repository.initDb()
    return StartState(
        repository.arePropertiesSetAsync().await(),
        GLViewModelProvider(
            GLStateManager(repository)
        )
    )
}

val SPLASH_SCREEN_SIZE = WindowSize(500.dp, 300.dp)

@Composable
private fun SplashScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("LOADING...")
        }
    }
}

private data class StartState(
    val arePropertiesSet: Boolean,
    val viewModelProvider: GLViewModelProvider
)