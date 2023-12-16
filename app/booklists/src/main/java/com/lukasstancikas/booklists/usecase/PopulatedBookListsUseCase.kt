package com.lukasstancikas.booklists.usecase

import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.BookList
import com.lukasstancikas.booklists.network.BooksRepository
import com.lukasstancikas.booklists.util.update
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PopulatedBookListsUseCase(private val booksRepository: BooksRepository) {
    operator fun invoke(): Flow<List<BookList>> = flow {
        // retrieve data
        val booksLists = booksRepository.getBookLists().map { it.copy(isLoading = true) }
        emit(booksLists)
        val books = booksRepository.getAllBooks()

        // create list_id containers to sort all books into containers
        val bookListsContainers = booksLists.map { it.id }.associateWith { mutableListOf<Book>() }
        books.forEach {
            bookListsContainers[it.listId]?.let { container ->
                // business requirement to show up to 5 books in a list of lists
                if (!container.bookShowLimitReached()) {
                    container.add(it)
                }
            }
        }

        // populate existing book lists with books from corresponding containers
        // NOTE: creating a new instance of list is needed for stateflow to emit a different value
        var updatedBookLists = booksLists
        booksLists.forEachIndexed { index, bookList ->
            val booksForList = bookListsContainers[bookList.id] ?: bookList.books
            val updatedList = bookList.copy(books = booksForList, isLoading = false)
            updatedBookLists = updatedBookLists.update(index, updatedList)
            // emit each change
            emit(updatedBookLists)
        }

        // Once all data has been emitted, cancel the flow to prevent any further emissions
        currentCoroutineContext().cancel()
    }

    private fun List<Book>.bookShowLimitReached() = size >= MAX_BOOKS_PER_LIST

    companion object {
        const val MAX_BOOKS_PER_LIST = 5
    }
}