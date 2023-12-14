package com.lukasstancikas.booklists.ui.base

import androidx.lifecycle.SavedStateHandle
import com.lukasstancikas.booklists.data.NetworkError
import com.lukasstancikas.booklists.navigator.NavigationIntent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface ViewModelCommonStreams<UiState> {
    val errorStream: SharedFlow<NetworkError>

    val navigationStream: SharedFlow<NavigationIntent>

    val uiState: StateFlow<UiState>
    fun updateUiState(reduce: (UiState) -> UiState)

    suspend fun emitError(error: NetworkError)

    suspend fun emitNavigation(navigationIntent: NavigationIntent)
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

    override suspend fun emitError(error: NetworkError) {
        _errorStream.emit(error)
    }

    override suspend fun emitNavigation(navigationIntent: NavigationIntent) {
        _navigationStream.emit(navigationIntent)
    }

    override fun updateUiState(reduce: (UiState) -> UiState) {
        savedStateHandle[STATE_KEY] = reduce(uiState.value)
    }

    companion object {
        const val STATE_KEY = "save_state"
    }
}