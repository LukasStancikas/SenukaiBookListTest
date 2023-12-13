package com.lukasstancikas.booklists.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Parcelize
data class BookList(
    val id: Int,
    val title: String,
    @Transient
    val books: List<Book> = emptyList(),
    @Transient
    val isLoading: Boolean = false
): Parcelable