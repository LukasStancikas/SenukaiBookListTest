package com.lukasstancikas.booklists.db.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lukasstancikas.booklists.data.BookList

@Entity(tableName = "book_list_table")
data class BookListEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
) {
    fun toBookList() = BookList(
        id,
        title,
    )

    companion object {
        fun BookList.toBookListEntity() =
            BookListEntity(
                id,
                title,
            )
    }
}