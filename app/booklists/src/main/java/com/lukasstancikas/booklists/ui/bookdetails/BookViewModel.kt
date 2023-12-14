package com.lukasstancikas.booklists.ui.bookdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.NetworkError
import com.lukasstancikas.booklists.network.BooksRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookViewModel(
    book: Book,
    private val savedStateHandle: SavedStateHandle,
    private val booksRepository: BooksRepository
) : ViewModel() {

    val uiState: StateFlow<BookDetailsUiState> =
        savedStateHandle.getStateFlow(STATE_KEY, BookDetailsUiState(book = book))

    private val _errorStream = MutableSharedFlow<NetworkError>()
    val errorStream: SharedFlow<NetworkError> = _errorStream

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

    private fun updateUiState(reduce: (BookDetailsUiState) -> BookDetailsUiState) {
        savedStateHandle[STATE_KEY] = reduce(uiState.value)
    }

    companion object {
        const val STATE_KEY = "book_lists_save_state"
    }
}