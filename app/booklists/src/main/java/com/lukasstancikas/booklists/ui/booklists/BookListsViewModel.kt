package com.lukasstancikas.booklists.ui.booklists

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.BookList
import com.lukasstancikas.booklists.navigator.NavigationIntent.BookListSelected
import com.lukasstancikas.booklists.navigator.NavigationIntent.BookSelectedFromLists
import com.lukasstancikas.booklists.ui.base.ViewModelCommonStreams
import com.lukasstancikas.booklists.ui.base.ViewModelCommonStreamsHandler
import com.lukasstancikas.booklists.usecase.PopulatedBookListsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BookListsViewModel(
    savedStateHandle: SavedStateHandle,
    private val booksListUseCase: PopulatedBookListsUseCase
) : ViewModel(),
    ViewModelCommonStreams<BookListsUiState>
    by ViewModelCommonStreamsHandler(savedStateHandle, BookListsUiState()) {

    private var fetchJob: Job? = null

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
        val reduceOnError = { latestState: BookListsUiState ->
            latestState.copy(isLoading = false)
        }

        fetchJob?.cancel()
        fetchJob = viewModelScope.launchWithScopedErrorHandling(Dispatchers.IO, reduceOnError) {
            updateUiState { it.copy(isLoading = true) }
            booksListUseCase().collect { data ->
                updateUiState { it.copy(bookLists = data) }
            }
            updateUiState { it.copy(isLoading = false) }
        }
    }
}