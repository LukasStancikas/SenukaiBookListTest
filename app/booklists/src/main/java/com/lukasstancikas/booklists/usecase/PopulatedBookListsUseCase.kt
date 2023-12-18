package com.lukasstancikas.booklists.usecase

import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.BookList
import com.lukasstancikas.booklists.network.BooksRepository
import com.lukasstancikas.booklists.util.update
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PopulatedBookListsUseCase(private val booksRepository: BooksRepository) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(forceRefresh: Boolean = false) =
        booksRepository.getBookLists(forceRefresh)
            .map { list -> list.map { it.copy(isLoading = true) } }
            .combine(booksRepository.getAllBooks(forceRefresh)) { bookLists, books ->
                Pair(bookLists, books)
            }
            .flatMapLatest {
                val bookLists = it.first
                val books = it.second
                populateBookListWithBooks(bookLists, books)
            }

    private suspend fun populateBookListWithBooks(
        bookLists: List<BookList>,
        books: List<Book>
    ) = flow {
        // create list_id containers to sort all books into containers
        val bookListsContainers =
            bookLists.map { it.id }.associateWith { mutableListOf<Book>() }
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
        var updatedBookLists = bookLists
        bookLists.forEachIndexed { index, bookList ->
            val booksForList = bookListsContainers[bookList.id] ?: bookList.books
            val updatedList = bookList.copy(books = booksForList, isLoading = false)
            updatedBookLists = updatedBookLists.update(index, updatedList)
            // emit each change
            emit(updatedBookLists)
        }
    }

    private fun List<Book>.bookShowLimitReached() = size >= MAX_BOOKS_PER_LIST

    companion object {
        const val MAX_BOOKS_PER_LIST = 5
    }
}