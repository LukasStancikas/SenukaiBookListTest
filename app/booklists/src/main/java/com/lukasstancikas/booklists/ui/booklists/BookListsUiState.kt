package com.lukasstancikas.booklists.ui.booklists

import android.os.Parcelable
import com.lukasstancikas.booklists.data.BookList
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookListsUiState(
    val isLoading: Boolean = false,
    val bookLists: List<BookList> = emptyList(),
) : Parcelable