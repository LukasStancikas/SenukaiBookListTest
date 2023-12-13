package com.lukasstancikas.booklists.network

import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.BookList

interface BooksRepository {
    suspend fun getBookLists(): List<BookList>
    suspend fun getAllBooks(): List<Book>
    suspend fun getBookDetails(bookId: Int): Book
}