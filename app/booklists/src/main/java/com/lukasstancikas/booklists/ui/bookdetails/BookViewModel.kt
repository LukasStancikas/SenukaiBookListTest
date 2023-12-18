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
import kotlinx.coroutines.flow.catch
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
            booksRepository.getBookDetails(uiState.value.book.id, forceRefresh)
                .catch { error ->
                    onError(error)
                    updateUiState { it.copy(isLoading = false) }
                }
                .collect { bookDetails ->
                    updateUiState { it.copy(book = bookDetails, isLoading = false) }
                }
        }
    }
}