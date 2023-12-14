package com.lukasstancikas.booklists.ui.booklist

import android.os.Parcelable
import com.lukasstancikas.booklists.data.BookList
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyListUiState(
    val isLoading: Boolean = false,
    val bookList: BookList,
): Parcelable {
    sealed class Error {
        data object Cancelled: Error()
        data object FailedToReachServer: Error()
    }
}