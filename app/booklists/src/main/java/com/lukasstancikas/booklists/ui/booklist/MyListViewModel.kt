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
import kotlinx.coroutines.CoroutineScope
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

    fun onInitialize() {
        fetchBooksWithDetails(false)
    }
    fun onPullRefresh() {
        fetchBooksWithDetails(true)
    }

    fun onBookClick(book: Book) = viewModelScope.launch {
        emitNavigation(NavigationIntent.BookSelectedFromMyList(book))
    }

    private fun fetchBooksWithDetails(forceRefresh: Boolean) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch(Dispatchers.IO) {
            updateUiState { it.copy(isLoading = true) }
            try {
                myListUseCase(uiState.value.bookList, forceRefresh)
                    .collect { data ->
                        updateUiState { it.copy(bookList = data) }
                    }
            } catch (e: Exception) {
                onError(e)
            } finally {
                cancelLoadingFlags()
            }
        }
    }

    private fun CoroutineScope.cancelLoadingFlags() {
        // cancel any loading flags
        val books = uiState.value.bookList.books
        val bookListWithoutLoadingFlag = uiState.value.bookList
            .copy(books = books.map { book -> book.copy(isLoading = false) })

        updateUiState {
            it.copy(
                isLoading = false,
                bookList = bookListWithoutLoadingFlag
            )
        }
    }
}