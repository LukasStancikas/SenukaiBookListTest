package com.lukasstancikas.booklists.data

import android.os.Parcelable
import com.lukasstancikas.booklists.util.DateSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.OffsetDateTime

@Serializable
@Parcelize
data class Book(
    val id: Int,
    val listId: Int,
    val title: String,
    val img: String,
    val description: String? = null,
    val author: String? = null,
    val isbn: String? = null,
    @Serializable(with = DateSerializer::class)
    val publicationDate: OffsetDateTime? = null,
    @Transient
    val isLoading: Boolean = false
) : Parcelable {
    val isWithoutDetails: Boolean
        get() = listOf<Any?>(description, author, isbn, publicationDate).all { it == null }
}