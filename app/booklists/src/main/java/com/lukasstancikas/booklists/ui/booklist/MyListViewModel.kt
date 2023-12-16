package com.lukasstancikas.booklists.ui.booklist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.BookList
import com.lukasstancikas.booklists.navigator.NavigationIntent
import com.lukasstancikas.booklists.ui.base.ViewModelCommonStreams
import com.lukasstancikas.booklists.ui.base.ViewModelCommonStreamsHandler
import com.lukasstancikas.booklists.usecase.PopulatedMyListUseCase
import kotlinx.coroutines.Dispatchers
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

    fun onPullRefresh() {
        fetchBooksWithDetails()
    }

    fun onBookClick(book: Book) = viewModelScope.launch {
        emitNavigation(NavigationIntent.BookSelectedFromMyList(book))
    }

    private fun fetchBooksWithDetails() {
        val reduceOnError = { latestState: MyListUiState ->
            latestState.copy(isLoading = false)
        }

        fetchJob?.cancel()
        fetchJob = viewModelScope.launchWithScopedErrorHandling(Dispatchers.IO, reduceOnError) {
            updateUiState { it.copy(isLoading = true) }
            myListUseCase(uiState.value.bookList).collect { data ->
                updateUiState { it.copy(bookList = data) }
            }
            updateUiState { it.copy(isLoading = false) }
        }
    }
}