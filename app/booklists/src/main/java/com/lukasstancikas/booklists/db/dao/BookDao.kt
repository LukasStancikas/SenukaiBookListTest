package com.lukasstancikas.booklists.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lukasstancikas.booklists.db.tables.BookEntity
import java.time.OffsetDateTime

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBook(book: BookEntity): Long

    @Query("SELECT * FROM book_table")
    suspend fun getAllBooks(): List<BookEntity>

    @Query("SELECT * FROM book_table WHERE id = :id")
    suspend fun getBookById(id: Int): BookEntity?

    @Query(
        "UPDATE book_table SET listId = :listId, title = :title, img = :img, " +
                "description = (CASE WHEN description IS NULL THEN :description ELSE description END), " +
                "author = (CASE WHEN author IS NULL THEN :author ELSE author END), " +
                "isbn = (CASE WHEN isbn IS NULL THEN :isbn ELSE isbn END), " +
                "publicationDate = (CASE WHEN publicationDate IS NULL THEN :publicationDate ELSE publicationDate END) " +
                "WHERE id = :id"
    )
    suspend fun updateBook(
        id: Int,
        listId: Int,
        title: String,
        img: String,
        description: String?,
        author: String?,
        isbn: String?,
        publicationDate: OffsetDateTime?
    ): Int

    suspend fun updateBook(book: BookEntity): Int =
        updateBook(
            book.id,
            book.listId,
            book.title,
            book.img,
            book.description,
            book.author,
            book.isbn,
            book.publicationDate
        )

    @Delete
    suspend fun deleteBook(book: BookEntity)
}