package com.lukasstancikas.booklists.ui.booklists

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.BookList
import com.lukasstancikas.booklists.navigator.NavigationIntent
import com.lukasstancikas.booklists.navigator.NavigationIntent.BookListSelected
import com.lukasstancikas.booklists.navigator.NavigationIntent.BookSelected
import com.lukasstancikas.booklists.usecase.PopulatedBookListsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookListsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val booksListUseCase: PopulatedBookListsUseCase
) : ViewModel() {

    val uiState: StateFlow<BookListsUiState> =
        savedStateHandle.getStateFlow(STATE_KEY, BookListsUiState())

    private val _errorStream = MutableSharedFlow<BookListsUiState.Error>()
    val errorStream: SharedFlow<BookListsUiState.Error> = _errorStream

    private val _navigationStream = MutableSharedFlow<NavigationIntent>()
    val navigationStream: SharedFlow<NavigationIntent> = _navigationStream

    private var fetchJob: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()

        updateUiState { it.copy(isLoading = false) }

        when (throwable) {
            is CancellationException -> BookListsUiState.Error.Cancelled
            else -> null
        }?.let { error -> viewModelScope.launch { _errorStream.emit(error) } }

    }

    init {
        onPullRefresh()
    }

    fun onPullRefresh() {
        fetchBookLists()
    }

    fun onAllClick(bookList: BookList) = viewModelScope.launch {
        _navigationStream.emit(BookListSelected(bookList))
    }

    fun onBookClick(book: Book) = viewModelScope.launch {
        _navigationStream.emit(BookSelected(book))
    }

    private fun fetchBookLists() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch(exceptionHandler) {
            updateUiState { it.copy(isLoading = true) }
            booksListUseCase().collect { data ->
                updateUiState { it.copy(bookLists = data) }
            }
            updateUiState { it.copy(isLoading = false) }
        }
    }

    private fun updateUiState(reduce: (BookListsUiState) -> BookListsUiState) {
        savedStateHandle[STATE_KEY] = reduce(uiState.value)
    }

    companion object {
        const val STATE_KEY = "book_lists_save_state"
    }
}