package io.grimlocations.shared.framework.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import io.grimlocations.shared.framework.ui.State
import io.grimlocations.shared.framework.ui.viewmodel.ViewModel
import io.grimlocations.shared.framework.ui.viewmodel.stateFlow
import io.grimlocations.shared.util.guardLet
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
inline fun <reified S : State, VM : ViewModel<S, *>>
        View(
    viewModel: VM,
    disabled: Boolean = false,
    loading: @Composable () -> Unit = { Spinner() },
    noinline content: @Composable (S) -> Unit
) {
    val state = viewModel.stateFlow.collectAsState().value

    if (state == null) {
        loading()
    } else {
        if(disabled){
            Overlay { content(state) }
        } else {
            content(state)
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
inline fun <reified S1 : State, reified S2 : State, VM1 : ViewModel<S1, *>, VM2 : ViewModel<S2, *>>
        View(
    viewModel1: VM1,
    viewModel2: VM2,
    disabled: Boolean = false,
    loading: @Composable () -> Unit = { Spinner() },
    noinline content: @Composable (S1, S2) -> Unit
) {

    val state1 = viewModel1.stateFlow.collectAsState().value
    val state2 = viewModel2.stateFlow.collectAsState().value

    guardLet(state1, state2) { s1, s2 ->
        if(disabled){
            Overlay { content(s1, s2) }
        } else {
            content(s1, s2)
        }
    } ?: loading()
}

@ExperimentalCoroutinesApi
@Composable
inline fun <reified S1 : State, reified S2 : State, reified S3 : State, VM1 : ViewModel<S1, *>, VM2 : ViewModel<S2, *>, VM3 : ViewModel<S3, *>>
        View(
    viewModel1: VM1,
    viewModel2: VM2,
    viewModel3: VM3,
    disabled: Boolean = false,
    loading: @Composable () -> Unit = { Spinner() },
    noinline content: @Composable (S1, S2, S3) -> Unit
) {

    val state1 = viewModel1.stateFlow.collectAsState().value
    val state2 = viewModel2.stateFlow.collectAsState().value
    val state3 = viewModel3.stateFlow.collectAsState().value

    guardLet(state1, state2, state3) { s1, s2, s3 ->
        if(disabled){
            Overlay { content(s1, s2, s3) }
        } else {
            content(s1, s2, s3)
        }
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
    disabled: Boolean = false,
    loading: @Composable () -> Unit = { Spinner() },
    noinline content: @Composable (S1, S2, S3, S4) -> Unit
) {

    val state1 = viewModel1.stateFlow.collectAsState().value
    val state2 = viewModel2.stateFlow.collectAsState().value
    val state3 = viewModel3.stateFlow.collectAsState().value
    val state4 = viewModel4.stateFlow.collectAsState().value

    guardLet(state1, state2, state3, state4) { s1, s2, s3, s4 ->
        if(disabled){
            Overlay { content(s1, s2, s3, s4) }
        } else {
            content(s1, s2, s3, s4)
        }
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
    disabled: Boolean = false,
    loading: @Composable () -> Unit = { Spinner() },
    noinline content: @Composable (S1, S2, S3, S4, S5) -> Unit
) {

    val state1 = viewModel1.stateFlow.collectAsState().value
    val state2 = viewModel2.stateFlow.collectAsState().value
    val state3 = viewModel3.stateFlow.collectAsState().value
    val state4 = viewModel4.stateFlow.collectAsState().value
    val state5 = viewModel5.stateFlow.collectAsState().value

    guardLet(state1, state2, state3, state4, state5) { s1, s2, s3, s4, s5 ->
        if(disabled){
            Overlay { content(s1, s2, s3, s4, s5) }
        } else {
            content(s1, s2, s3, s4, s5)
        }
    } ?: loading()
}

@Composable
fun Spinner() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text("Loading...")
    }
}

@Composable
fun Overlay(content: @Composable () -> Unit) {
    Providers(LocalContentAlpha provides ContentAlpha.disabled) {
        Box(Modifier.fillMaxSize()) {
            content()
            Box(modifier = Modifier.matchParentSize().alpha(0f).clickable(onClick = {}))
        }
    }
}