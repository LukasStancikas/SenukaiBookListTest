package com.lukasstancikas.booklists.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lukasstancikas.booklists.db.tables.BookListEntity

@Dao
interface BookListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookList(bookList: BookListEntity): Long

    @Query("SELECT * FROM book_list_table")
    fun getAllBookLists(): List<BookListEntity>

    @Query("SELECT * FROM book_list_table WHERE id = :id")
    fun getBookListById(id: Int): BookListEntity?

    @Query("DELETE FROM book_list_table WHERE id = :bookListId")
    fun deleteBookList(bookListId: Int)
}