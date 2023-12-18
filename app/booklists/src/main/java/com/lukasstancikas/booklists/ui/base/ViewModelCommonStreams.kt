package com.lukasstancikas.booklists.ui.base

import androidx.lifecycle.SavedStateHandle
import com.lukasstancikas.booklists.data.NetworkError
import com.lukasstancikas.booklists.navigator.NavigationIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException

interface ViewModelCommonStreams<UiState> {
    val errorStream: SharedFlow<NetworkError>

    val navigationStream: SharedFlow<NavigationIntent>

    val uiState: StateFlow<UiState>
    fun CoroutineScope.updateUiState(reduce: (UiState) -> UiState)

    suspend fun emitNavigation(navigationIntent: NavigationIntent)

    suspend fun onError(throwable: Throwable)
}

class ViewModelCommonStreamsHandler<UiState>(
    private val savedStateHandle: SavedStateHandle,
    initUiState: UiState
) : ViewModelCommonStreams<UiState> {

    private val _errorStream = MutableSharedFlow<NetworkError>()
    private val _navigationStream = MutableSharedFlow<NavigationIntent>()

    override val uiState = savedStateHandle.getStateFlow(STATE_KEY, initUiState)
    override val errorStream: SharedFlow<NetworkError> = _errorStream
    override val navigationStream: SharedFlow<NavigationIntent> = _navigationStream

    override suspend fun emitNavigation(navigationIntent: NavigationIntent) {
        _navigationStream.emit(navigationIntent)
    }

    override suspend fun onError(throwable: Throwable) {
        when (throwable) {
            is CancellationException -> NetworkError.Cancelled
            is UnknownHostException -> NetworkError.FailedToReachServer
            else -> NetworkError.Unexpected(throwable.localizedMessage)
        }.let { error -> _errorStream.emit(error) }
    }

    override fun CoroutineScope.updateUiState(reduce: (UiState) -> UiState) {
        launch(Dispatchers.Main) {
            savedStateHandle[STATE_KEY] = reduce(uiState.value)
        }
    }

    companion object {
        const val STATE_KEY = "save_state"
    }
}