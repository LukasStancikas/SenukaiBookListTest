package com.lukasstancikas.booklists.ui.bookdetails

import android.os.Parcelable
import com.lukasstancikas.booklists.data.Book
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookDetailsUiState(
    val isLoading: Boolean = false,
    val book: Book,
) : Parcelable