package com.lukasstancikas.booklists.usecase

import com.lukasstancikas.booklists.data.BookList
import com.lukasstancikas.booklists.network.BooksRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PopulatedMyListUseCase(
    private val booksRepository: BooksRepository
) {
    operator fun invoke(bookList: BookList): Flow<BookList> = flow {
        // retrieve books because previously we could have cut to $MAX_BOOKS_PER_LIST or less
        val mutableListBooks = booksRepository.getAllBooks()
            .filter { it.listId == bookList.id }
            .map { it.copy(isLoading = true) }
            .toMutableList()
        emit(bookList.copy(books = mutableListBooks))

        // go through each book and fetch details if it's not available in memory
        // emit after each book update
        mutableListBooks.forEachIndexed { index, book ->
            val bookWithDetails = if (book.isWithoutDetails) {
                booksRepository.getBookDetails(book.id)
            } else {
                book
            }
            mutableListBooks[index] = bookWithDetails.copy(isLoading = false)
            emit(bookList.copy(books = mutableListBooks))
        }

        // Once all data has been emitted, cancel the flow to prevent any further emissions
        currentCoroutineContext().cancel()
    }
}