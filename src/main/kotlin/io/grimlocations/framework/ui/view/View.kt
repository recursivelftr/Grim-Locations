package io.grimlocations.framework.ui.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import io.grimlocations.framework.ui.State
import io.grimlocations.framework.ui.viewmodel.ViewModel
import io.grimlocations.framework.ui.viewmodel.stateFlow
import io.grimlocations.framework.util.guardLet
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
inline fun <reified S : State, VM : ViewModel<S, *>>
        View(
    viewModel: VM,
    loading: @Composable () -> Unit = { Spinner() },
    noinline content: @Composable (S) -> Unit
) {
    val state = viewModel.stateFlow.collectAsState().value

    if (state == null) {
        loading()
    } else {
        content(state)
    }
}

@ExperimentalCoroutinesApi
@Composable
inline fun <reified S1 : State, reified S2 : State, VM1 : ViewModel<S1, *>, VM2 : ViewModel<S2, *>>
        View(
    viewModel1: VM1,
    viewModel2: VM2,
    loading: @Composable () -> Unit = { Spinner() },
    noinline content: @Composable (S1, S2) -> Unit
) {
    val state1 = viewModel1.stateFlow.collectAsState().value
    val state2 = viewModel2.stateFlow.collectAsState().value

    guardLet(state1, state2) { s1, s2 ->
        content(s1, s2)
    } ?: loading()
}

@ExperimentalCoroutinesApi
@Composable
inline fun <reified S1 : State, reified S2 : State, reified S3 : State, VM1 : ViewModel<S1, *>, VM2 : ViewModel<S2, *>, VM3 : ViewModel<S3, *>>
        View(
    viewModel1: VM1,
    viewModel2: VM2,
    viewModel3: VM3,
    loading: @Composable () -> Unit = { Spinner() },
    noinline content: @Composable (S1, S2, S3) -> Unit
) {
    val state1 = viewModel1.stateFlow.collectAsState().value
    val state2 = viewModel2.stateFlow.collectAsState().value
    val state3 = viewModel3.stateFlow.collectAsState().value

    guardLet(state1, state2, state3) { s1, s2, s3 ->
        content(s1, s2, s3)
    } ?: loading()
}

@ExperimentalCoroutinesApi
@Composable
inline fun <reified S1 : State, reified S2 : State, reified S3 : State, reified S4 : State, VM1 : ViewModel<S1, *>, VM2 : ViewModel<S2, *>, VM3 : ViewModel<S3, *>, VM4 : ViewModel<S4, *>>
        View(
    viewModel1: VM1,
    viewModel2: VM2,
    viewModel3: VM3,
    viewModel4: VM4,
    loading: @Composable () -> Unit = { Spinner() },
    noinline content: @Composable (S1, S2, S3, S4) -> Unit
) {
    val state1 = viewModel1.stateFlow.collectAsState().value
    val state2 = viewModel2.stateFlow.collectAsState().value
    val state3 = viewModel3.stateFlow.collectAsState().value
    val state4 = viewModel4.stateFlow.collectAsState().value

    guardLet(state1, state2, state3, state4) { s1, s2, s3, s4 ->
        content(s1, s2, s3, s4)
    } ?: loading()
}

@ExperimentalCoroutinesApi
@Composable
inline fun <reified S1 : State, reified S2 : State, reified S3 : State, reified S4 : State, reified S5 : State, VM1 : ViewModel<S1, *>, VM2 : ViewModel<S2, *>, VM3 : ViewModel<S3, *>, VM4 : ViewModel<S4, *>, VM5 : ViewModel<S5, *>>
        View(
    viewModel1: VM1,
    viewModel2: VM2,
    viewModel3: VM3,
    viewModel4: VM4,
    viewModel5: VM5,
    loading: @Composable () -> Unit = { Spinner() },
    noinline content: @Composable (S1, S2, S3, S4, S5) -> Unit
) {
    val state1 = viewModel1.stateFlow.collectAsState().value
    val state2 = viewModel2.stateFlow.collectAsState().value
    val state3 = viewModel3.stateFlow.collectAsState().value
    val state4 = viewModel4.stateFlow.collectAsState().value
    val state5 = viewModel5.stateFlow.collectAsState().value

    guardLet(state1, state2, state3, state4, state5) { s1, s2, s3, s4, s5 ->
        content(s1, s2, s3, s4, s5)
    } ?: loading()
}

@Composable
fun Spinner() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text("Loading...")
    }
}
