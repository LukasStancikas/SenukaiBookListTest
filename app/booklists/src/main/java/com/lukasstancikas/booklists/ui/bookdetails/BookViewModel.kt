package com.lukasstancikas.booklists.ui.bookdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.network.BooksRepository
import com.lukasstancikas.booklists.ui.base.ViewModelCommonStreams
import com.lukasstancikas.booklists.ui.base.ViewModelCommonStreamsHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class BookViewModel(
    book: Book,
    savedStateHandle: SavedStateHandle,
    private val booksRepository: BooksRepository
) : ViewModel(),
    ViewModelCommonStreams<BookDetailsUiState>
    by ViewModelCommonStreamsHandler(savedStateHandle, BookDetailsUiState(book = book)) {

    private var fetchJob: Job? = null

    fun onInitialize() {
        if (uiState.value.book.isWithoutDetails) {
            onPullRefresh()
        }
    }

    fun onPullRefresh() {
        fetchBookDetails()
    }

    private fun fetchBookDetails() {
        val reduceOnError = { latestState: BookDetailsUiState ->
            latestState.copy(isLoading = false)
        }

        fetchJob?.cancel()
        fetchJob = viewModelScope.launchWithScopedErrorHandling(Dispatchers.IO, reduceOnError) {
            updateUiState { it.copy(isLoading = true) }
            val bookDetails = booksRepository.getBookDetails(uiState.value.book.id)
            updateUiState { it.copy(book = bookDetails, isLoading = false) }
        }
    }
}