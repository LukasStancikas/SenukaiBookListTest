package com.lukasstancikas.booklists.network

import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.BookList
import kotlinx.coroutines.flow.Flow

interface BooksRepository {
    suspend fun getBookLists(forceRefresh: Boolean = false): Flow<List<BookList>>
    suspend fun getAllBooks(forceRefresh: Boolean = false): Flow<List<Book>>
    suspend fun getBookDetails(bookId: Int, forceRefresh: Boolean = false): Flow<Book>
}