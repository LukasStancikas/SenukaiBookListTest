package com.lukasstancikas.booklists.ui.booklists

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.BookList
import com.lukasstancikas.booklists.data.NetworkError
import com.lukasstancikas.booklists.navigator.NavigationIntent.BookListSelected
import com.lukasstancikas.booklists.navigator.NavigationIntent.BookSelectedFromLists
import com.lukasstancikas.booklists.ui.base.ViewModelCommonStreams
import com.lukasstancikas.booklists.ui.base.ViewModelCommonStreamsHandler
import com.lukasstancikas.booklists.usecase.PopulatedBookListsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BookListsViewModel(
    savedStateHandle: SavedStateHandle,
    private val booksListUseCase: PopulatedBookListsUseCase
) : ViewModel(),
    ViewModelCommonStreams<BookListsUiState>
    by ViewModelCommonStreamsHandler(savedStateHandle, BookListsUiState()) {

    private var fetchJob: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()

        updateUiState { it.copy(isLoading = false) }

        when (throwable) {
            is CancellationException -> NetworkError.Cancelled
            else -> null
        }?.let { error -> viewModelScope.launch { emitError(error) } }

    }

    init {
        onPullRefresh()
    }

    fun onPullRefresh() {
        fetchBookLists()
    }

    fun onAllClick(bookList: BookList) = viewModelScope.launch {
        emitNavigation(BookListSelected(bookList))
    }

    fun onBookClick(book: Book) = viewModelScope.launch {
        emitNavigation(BookSelectedFromLists(book))
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
}