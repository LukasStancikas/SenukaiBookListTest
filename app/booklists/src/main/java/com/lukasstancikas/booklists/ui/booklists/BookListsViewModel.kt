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

    fun onInitialize() {
        fetchBookLists(false)
    }
    fun onPullRefresh() {
        fetchBookLists(true)
    }

    fun onAllClick(bookList: BookList) = viewModelScope.launch {
        emitNavigation(BookListSelected(bookList))
    }

    fun onBookClick(book: Book) = viewModelScope.launch {
        emitNavigation(BookSelectedFromLists(book))
    }

    private fun fetchBookLists(forceRefresh: Boolean) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch(Dispatchers.IO) {
            updateUiState { it.copy(isLoading = true) }
            try {
                booksListUseCase(forceRefresh)
                    .collect { data ->
                        updateUiState { it.copy(bookLists = data) }
                    }
            } catch (e: Exception) {
                onError(e)
            } finally {
                updateUiState { it.copy(isLoading = false) }
            }
        }
    }
}