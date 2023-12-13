package com.lukasstancikas.booklists.network

import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.BookList
import com.lukasstancikas.booklists.network.retrofit.BooksApi

/**
 * BooksRepository implementation using Retrofit provided network api
 * */
class BooksRepositoryImpl(private val booksApi: BooksApi) : BooksRepository {
    override suspend fun getBookLists(): List<BookList> {
        return booksApi.getBookLists()
    }

    override suspend fun getAllBooks(): List<Book> {
        return booksApi.getAllBooks()
    }

    override suspend fun getBookDetails(bookId: Int): Book {
        return booksApi.getBookDetails(bookId)
    }
}