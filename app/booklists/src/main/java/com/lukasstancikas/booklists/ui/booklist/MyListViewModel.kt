package com.lukasstancikas.booklists.ui.booklist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.BookList
import com.lukasstancikas.booklists.data.NetworkError
import com.lukasstancikas.booklists.navigator.NavigationIntent
import com.lukasstancikas.booklists.usecase.PopulatedMyListUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyListViewModel(
    bookList: BookList,
    private val savedStateHandle: SavedStateHandle,
    private val myListUseCase: PopulatedMyListUseCase
) : ViewModel() {

    val uiState: StateFlow<MyListUiState> =
        savedStateHandle.getStateFlow(STATE_KEY, MyListUiState(bookList = bookList))

    private val _errorStream = MutableSharedFlow<NetworkError>()
    val errorStream: SharedFlow<NetworkError> = _errorStream

    private val _navigationStream = MutableSharedFlow<NavigationIntent>()
    val navigationStream: SharedFlow<NavigationIntent> = _navigationStream

    private var fetchJob: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()

        updateUiState { it.copy(isLoading = false) }

        when (throwable) {
            is CancellationException -> NetworkError.Cancelled
            else -> null
        }?.let { error -> viewModelScope.launch { _errorStream.emit(error) } }
    }

    init {
        onPullRefresh()
    }

    fun onPullRefresh() {
        fetchBooksWithDetails()
    }

    fun onBookClick(book: Book) = viewModelScope.launch {
        _navigationStream.emit(NavigationIntent.BookSelectedFromLists(book))
    }

    private fun fetchBooksWithDetails() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch(exceptionHandler) {
            updateUiState { it.copy(isLoading = true) }
            myListUseCase(uiState.value.bookList).collect { data ->
                updateUiState { it.copy(bookList = data) }
            }
            updateUiState { it.copy(isLoading = false) }
        }
    }

    private fun updateUiState(reduce: (MyListUiState) -> MyListUiState) {
        savedStateHandle[STATE_KEY] = reduce(uiState.value)
    }

    companion object {
        const val STATE_KEY = "book_lists_save_state"
    }
}