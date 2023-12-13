package com.lukasstancikas.booklists.data

import android.os.Parcelable
import com.lukasstancikas.booklists.util.DateSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.time.LocalDate

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
    val publicationDate: LocalDate? = null,
): Parcelable