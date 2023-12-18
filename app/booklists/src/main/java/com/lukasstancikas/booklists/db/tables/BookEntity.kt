package com.lukasstancikas.booklists.db.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lukasstancikas.booklists.data.Book
import java.time.OffsetDateTime

@Entity(tableName = "book_table")
data class BookEntity(
    @PrimaryKey
    val id: Int,
    val listId: Int,
    val title: String,
    val img: String,
    val description: String? = null,
    val author: String? = null,
    val isbn: String? = null,
    val publicationDate: OffsetDateTime? = null,
) {
    fun toBook() = Book(
        id,
        listId,
        title,
        img,
        description,
        author,
        isbn,
        publicationDate
    )

    companion object {
        fun Book.toBookEntity() =
            BookEntity(
                id,
                listId,
                title,
                img,
                description,
                author,
                isbn,
                publicationDate
            )
    }
}