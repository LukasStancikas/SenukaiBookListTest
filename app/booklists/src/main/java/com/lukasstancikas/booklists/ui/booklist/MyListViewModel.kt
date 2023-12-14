package com.lukasstancikas.booklists.ui.booklist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.BookList
import com.lukasstancikas.booklists.data.NetworkError
import com.lukasstancikas.booklists.navigator.NavigationIntent
import com.lukasstancikas.booklists.ui.base.ViewModelCommonStreams
import com.lukasstancikas.booklists.ui.base.ViewModelCommonStreamsHandler
import com.lukasstancikas.booklists.usecase.PopulatedMyListUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MyListViewModel(
    bookList: BookList,
    savedStateHandle: SavedStateHandle,
    private val myListUseCase: PopulatedMyListUseCase
) : ViewModel(),
    ViewModelCommonStreams<MyListUiState>
    by ViewModelCommonStreamsHandler(savedStateHandle, MyListUiState(bookList = bookList)) {

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
        fetchBooksWithDetails()
    }

    fun onBookClick(book: Book) = viewModelScope.launch {
        emitNavigation(NavigationIntent.BookSelectedFromMyList(book))
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
}