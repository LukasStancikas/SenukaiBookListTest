package com.lukasstancikas.booklists.ui.bookdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.NetworkError
import com.lukasstancikas.booklists.network.BooksRepository
import com.lukasstancikas.booklists.ui.base.ViewModelCommonStreams
import com.lukasstancikas.booklists.ui.base.ViewModelCommonStreamsHandler
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BookViewModel(
    book: Book,
    savedStateHandle: SavedStateHandle,
    private val booksRepository: BooksRepository
) : ViewModel(),
    ViewModelCommonStreams<BookDetailsUiState>
    by ViewModelCommonStreamsHandler(savedStateHandle, BookDetailsUiState(book = book)) {

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
        if (uiState.value.book.isWithoutDetails) {
            onPullRefresh()
        }
    }

    fun onPullRefresh() {
        fetchBookDetails()
    }

    private fun fetchBookDetails() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch(exceptionHandler) {
            updateUiState { it.copy(isLoading = true) }
            val bookDetails = booksRepository.getBookDetails(uiState.value.book.id)
            updateUiState { it.copy(book = bookDetails, isLoading = false) }
        }
    }
}