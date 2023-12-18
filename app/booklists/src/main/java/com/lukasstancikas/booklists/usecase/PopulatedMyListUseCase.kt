package com.lukasstancikas.booklists.usecase

import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.BookList
import com.lukasstancikas.booklists.network.BooksRepository
import com.lukasstancikas.booklists.util.update
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class PopulatedMyListUseCase(
    private val booksRepository: BooksRepository
) {
    suspend operator fun invoke(bookList: BookList, forceRefresh: Boolean = false) =
        // retrieve books because previously we could have cut to $MAX_BOOKS_PER_LIST or less
        booksRepository.getAllBooks(forceRefresh)
            .flatMapLatest { books ->
                val allBooksForList = books
                    .filter { it.listId == bookList.id }
                    .map { it.copy(isLoading = it.isWithoutDetails) }
                populateBooksWithDetail(allBooksForList, forceRefresh, bookList)
            }

    private suspend fun populateBooksWithDetail(
        allBooksForList: List<Book>,
        forceRefresh: Boolean,
        bookList: BookList
    ): Flow<BookList> {
        var updatedList = allBooksForList

        // go through each book and fetch details if it's not available in memory
        // NOTE: creating a new instance of list is needed for stateflow to emit a different value
        return allBooksForList.asFlow()
            .flatMapConcat { book ->
                if (book.isWithoutDetails) {
                    booksRepository.getBookDetails(book.id, forceRefresh)
                } else {
                    flowOf(book)
                }
            }
            .map { bookDetails ->
                val index = updatedList.indexOfFirst { it.id == bookDetails.id }
                updatedList = updatedList.update(index, bookDetails.copy(isLoading = false))
                bookList.copy(books = updatedList)
            }
    }
}