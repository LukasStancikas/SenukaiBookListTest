package com.lukasstancikas.booklists.network

import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.BookList
import com.lukasstancikas.booklists.db.BookDatabase
import com.lukasstancikas.booklists.db.tables.BookEntity.Companion.toBookEntity
import com.lukasstancikas.booklists.db.tables.BookListEntity.Companion.toBookListEntity
import com.lukasstancikas.booklists.network.retrofit.BooksApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * BooksRepository implementation using Retrofit provided network api and backing Room database
 * */
class BooksRepositoryImpl(
    private val networkChecker: NetworkChecker,
    private val db: BookDatabase,
    private val booksApi: BooksApi
) :
    BooksRepository {
    override suspend fun getBookLists(forceRefresh: Boolean): Flow<List<BookList>> = flow {
        val listsFromDatabase = db.bookListDao().getAllBookLists().map { it.toBookList() }
        emit(listsFromDatabase)
        if (networkChecker.isConnected() || forceRefresh) {
            val listsFromServer = booksApi.getBookLists()
            emit(listsFromServer)
            listsFromServer.forEach {
                db.bookListDao().insertBookList(it.toBookListEntity())
            }
        }
    }

    override suspend fun getAllBooks(forceRefresh: Boolean): Flow<List<Book>> = flow {
        val booksFromDatabase = db.bookDao().getAllBooks().map { it.toBook() }
        emit(booksFromDatabase)
        if (networkChecker.isConnected() || forceRefresh) {
            val booksFromServer = booksApi.getAllBooks()
            emit(booksFromServer)
            booksFromServer.forEach {
                val bookEntity = it.toBookEntity()
                if (db.bookDao().insertBook(bookEntity) == -1L) {
                    db.bookDao().updateBook(bookEntity)
                }
            }
        }
    }

    override suspend fun getBookDetails(bookId: Int, forceRefresh: Boolean): Flow<Book> = flow {
        val bookFromDatabase = db.bookDao().getBookById(bookId)
        bookFromDatabase?.let { emit(it.toBook()) }
        if (networkChecker.isConnected() || forceRefresh) {
            val bookFromServer = booksApi.getBookDetails(bookId)
            emit(bookFromServer)
            val bookEntity = bookFromServer.toBookEntity()
            if (db.bookDao().insertBook(bookEntity) == -1L) {
                db.bookDao().updateBook(bookEntity)
            }
        }
    }
}