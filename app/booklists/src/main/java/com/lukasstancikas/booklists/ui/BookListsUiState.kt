package com.lukasstancikas.booklists.ui

import android.os.Parcelable
import com.lukasstancikas.booklists.data.BookList
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookListsUiState(
    val isLoading: Boolean = false,
    val bookLists: List<BookList> = emptyList(),
): Parcelable {
    sealed class BookListsError {
        data object Cancelled: BookListsError()
        data object FailedToReachServer: BookListsError()
    }
}

//sealed class BookListsUIEvent {
//    data object InitiatedRefresh : BookListsUIEvent()
//    data class BookListClicked(val bookList: BookList): BookListsUIEvent()
//    data class RepoFetchFailed(val error: Throwable) : BookListsUIEvent()
//    data class RepoFetchSucceeded(val bookLists: List<BookList>) : BookListsUIEvent()
//    data class BookClicked(val book: Book): BookListsUIEvent()
//}