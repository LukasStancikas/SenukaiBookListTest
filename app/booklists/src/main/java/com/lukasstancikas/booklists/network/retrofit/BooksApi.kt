package com.lukasstancikas.booklists.network.retrofit

import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.BookList
import retrofit2.http.GET
import retrofit2.http.Path

interface BooksApi {
    @GET("lists")
    suspend fun getBookLists(): List<BookList>

    @GET("books")
    suspend fun getAllBooks(): List<Book>

    @GET("book/{book_id}")
    suspend fun getBookDetails(@Path("book_id") bookId: Int): Book

    companion object {
        const val BASE_URL = "https://my-json-server.typicode.com/KeskoSenukaiDigital/assignment/"
    }
}