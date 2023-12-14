package com.lukasstancikas.booklists.usecase

import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.BookList
import com.lukasstancikas.booklists.network.BooksRepository
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
        val populatedBookLists = booksLists.toMutableList()
        populatedBookLists.forEachIndexed { index, bookList ->
            val booksForList = bookListsContainers[bookList.id] ?: bookList.books
            populatedBookLists[index] = bookList.copy(books = booksForList, isLoading = false)
            // update UI after each book list has been populated
            emit(populatedBookLists)
        }

        // Once all data has been emitted, cancel the flow to prevent any further emissions
        currentCoroutineContext().cancel()
    }

    private fun List<Book>.bookShowLimitReached() = size >= MAX_BOOKS_PER_LIST

    companion object {
        const val MAX_BOOKS_PER_LIST = 5
    }
}