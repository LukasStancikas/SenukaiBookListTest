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
import kotlinx.coroutines.launch

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
            fetchBookDetails(false)
        }
    }

    fun onPullRefresh() {
        fetchBookDetails(true)
    }

    private fun fetchBookDetails(forceRefresh: Boolean) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch(Dispatchers.IO) {
            updateUiState { it.copy(isLoading = true) }
            try {
                booksRepository.getBookDetails(uiState.value.book.id, forceRefresh)
                    .collect { bookDetails ->
                        updateUiState { it.copy(book = bookDetails) }
                    }
            } catch (e: Exception) {
                onError(e)
            } finally {
                updateUiState { it.copy(isLoading = false) }
            }
        }
    }
}